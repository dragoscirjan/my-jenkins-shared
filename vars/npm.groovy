/**
 * This method should help you run npm (yarn/pnpm) install and adding a cache layer.
 *
 * @param manager Node package manager to use for installing packages. Default: 'npm'. (Deprecated. Use `packageManager`)
 * @param cache Whether to use node_modules cache or not. Default: true.
 * @param cacheKey Cache unique key to be used in the cache file name. Default: ''.
 * @param useNvm Whether to use nvm or not. Default: false.
 * @param nodeVersion Nodejs version to use for nvm.runSh() method.
 * @param packageManager Node package manager to use for installing packages. Default: 'npm'. (Overwrites `manager` which is deprecated.)
 */
def install(Map options) {
  if (options.packageManager) {
    options.manager = options.packageManager
  }

  if (!options.manager) {
    options.manager = 'npm'
  }

  if (!options.find({ it -> it.key == 'cache' })) {
    options.cache = true
  }

  if (!options.cacheKey) {
    options.cacheKey = ''
  }

  command = "${options.manager} install"

  jobName = env.JOB_NAME.replaceAll(/[^\w]/, '_')

  if (options.cache) {
    try {
      // if not Linux this will trigger error
      sh "uname"
      command = """
# clear files older than 15 days
find /tmp -type f -iname \"${jobName}_*\" -mtime +14 | xargs rm -rf

# find old hashcacheKey
old_hash=
if [ -f '/tmp/${jobName}' ]; then old_hash=\$(cat '/tmp/${jobName}'); fi

# calculate present hash & unzip archive if it exists
hash=\$(cat ./package.json | sha256sum | awk -F ' ' '{ print \$1 }')
archive_path=\"/tmp/${jobName}_${options.cacheKey}_\${hash}.tgz\"
old_archive_path=\"/tmp/${jobName}_${options.cacheKey}_\${old_hash}.tgz\"
if [ -f \"\$archive_path\" ]; then tar -xzf \"\$archive_path\" .; fi

# run install command
${command}

# make new archive if old hash is different than new one
if [ \"\$old_hash\" != \"\$hash\" ]; then
  tar -czf \"\$archive_path\" ./node_modules
  rm -rf \"\$old_archive_path\"
  echo \"\$hash\" > '/tmp/${jobName}'
fi
"""
    } catch (Exception e) {
      command = """
\$hash = (Get-FileHash -Path .\\package.json).Hash
\$archivePath = \"${jobName}_${options.cacheKey}_\${env:TEMP}\${hash}.zip\"
if (Test-Path -Path \"\$archivePath\" -PathType Leaf) {
 Expand-Archive -Path \"\$archivePath\" -DestinationPath .
}
${command}
Compress-Archive -Path .\\node_modules -DestinationPath \"\$archivePath\"
"""
    }
  }

  if (!options.useNvm) {
    utils.runSh command
  } else {
    nvm.runSh command, options.nodeVersion
  }
}


/**
 * This method should help you make automated module releases.
 *
 * NOTE:
 * This command assumes there is a "release" script in your package.json, and will run
 * "npm run release" to actually perform a realease.
 *
 * WARNING:
 * Since this method may also imply a git commit/push command, it is imperative that your commit
 * message for marking the release should have the following form: chore: release v${version}";
 * otherwise you will generate an endless pipeline loop.
 *
 * @param gitCredentialsId Credentials ID for github/gitlab, if you're publishing to npmjs.com
 * @param gitUrl Git URL no http(s):// included.
 *                             i.e. 'github.com/dragoscirjan/my-jenkins-shared.git'
 * @param gitMessageToInclude Default 'chore: release v'. If this message is met in the commit message, release
 *                             command will abort, considering release has been performed already.
 * @param npmTokenCredentialId Default null. Credentials ID for npmjs.com token, if you're publishing to npmjs.com
 * @param noNpmPublish Default false. Ignore the npm publish command
 * @param packageManager Default 'npm'. Package manager to use.
 * @param releaseManager Default 'release-it'. Release manager to use.
 * @param releaseItArgs Default 'patch'. release-it command arguments. See release-it documentation.
 * @param useNvm Default: false. Whether to use nvm or not.
 * @param nodeVersion Default null. Nodejs version to use for nvm.runSh() method.
 *
 * Example of realease-it.json config:
 * <code>{ "git": { "commitMessage": "chore: release v${version}",
 "requireUpstream": false,
 "commitArgs": "--no-verify",
 "--requireBranch": "master"},
 "plugins": { "@release-it/conventional-changelog": { "preset": "angular",
 "infile": "CHANGELOG.md" }}} </code>
 */
def release(Map options) {
  if (!options.gitUrl) {
    throw new Exception('gitUrl not mentioned')
  }

  if (!options.gitCredentialsId) {
    throw new Exception('gitCredentialsId not mentioned')
  }

  if (!options.gitMessageToInclude) {
    options.gitMessageToInclude = "chore: release v"
  }

  def commitMessage = git.lastCommitMessage()
  if (commitMessage.indexOf(options.gitMessageToInclude) >= 0) {
    return
  }

  def packageManager = "${options.packageManager ? options.packageManager : 'npm'}"
  def releaseManager = options.releaseManager ? options.releaseManager : 'release-it'

  def command = """${options.preCommand ? options.preCommand : ''}"""

  if (options.npmTokenCredentialId) {
    withCredentials([
      string(credentialsId: options.npmTokenCredentialId, variable: 'NPMJS_AUTH_TOKEN')
    ]) {
      command = """
${command}

npm config set registry http://registry.npmjs.com
npm set //registry.npmjs.com/:_authToken ${NPMJS_AUTH_TOKEN}
"""
    }
  }

  withCredentials([usernamePassword(
    credentialsId: options.gitCredentialsId,
    usernameVariable: 'GIT_USER',
    passwordVariable: 'GIT_TOKEN'
  )]) {
    command = """
${command}

git remote rm origin;
git remote add origin https://${GIT_USER}:${GIT_TOKEN}@${options.gitUrl};
git fetch;
git checkout .;
git checkout ${env.BRANCH_NAME};
git pull origin ${env.BRANCH_NAME};
git status;
"""
  }

  command = """
${command}

${packageManager} install;
"""

  if (releaseManager == 'release-it') {
    def releaseItArgs = options.releaseItArgs ? options.releaseItArgs : 'patch'

    command = """
${command}

${packageManager} run release -- ${releaseItArgs};
"""
  }

  if (!options.noNpmPublish) {
    command = """
${command}

npm publish;
"""
  }

  if (options.debug) {
    echo command

    return
  }

  if (!options.useNvm) {
    utils.runSh command
  } else {
    nvm.runSh command, options.nodeVersion
  }
}
