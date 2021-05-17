
/**
 * This method should help you run npm (yarn/pnpm) install and adding a cache layer.
 *
 * @param manager     Node manager to use for installing pakcages. Default: 'npm'.
 * @param cache       Whether to use node_modules cache or not. Default: true.
 * @param useNvm      Whether to use nvm or not. Default: false.
 * @param nodeVersion Nodejs version to use for nvm.runSh() method.
 */
def install(Map options) {
  if (!options.manager) {
    options.manager = 'npm'
  }

  if (!options.find({ it -> it.key == 'cache' })) {
    options.cache = true
  }

  command = "${options.manager} install"

  jobName=env.JOB_NAME.replaceAll(/[^\w]/, '_')

  if (options.cache) {
    try {
      sh "uname"
      command = """
hash=\$(cat ./package.json | sha256sum | awk -F ' ' '{ print \$1 }')
archive_path=\"/tmp/${jobName}_\${hash}.tgz\"
if [ -f \"\$archive_path\" ]; then tar -xzf \"\$archive_path\" .; fi
${command}
tar -czf \"\$archive_path\" ./node_modules
"""
    } catch (Exception e) {
      command = """
\$hash = (Get-FileHash -Path .\\package.json).Hash
\$archivePath = \"${jobName}_\${env:TEMP}\${hash}.zip\"
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
 * @param gitCredentialsId     Credentials ID for github/gitlab, if you're publishing to npmjs.com
 * @param gitUrl               Git URL no http(s):// included.
 *                             i.e. 'gitlab-forensic.cellebrite.com/devops-rnd/shared-library.git'
 * @param npmTokenCredentialId Default null. Credentials ID for npmjs.com token, if you're publishing to npmjs.com
 * @param noNpmPublish         Default false. Ignore the npm publish command
 * @param packageManager       Default 'npm'. Package manager to use.
 * @param releaseManager       Default 'release-it'. Release manager to use.
 * @param releaseItArgs        Default 'patch'. release-it command arguments. See release-it documentation.
 *
 * Example of realease-it.json config:
 * <code>
 * {
 *   "git": {
 *     "commitMessage": "chore: release v${version}",
 *     "requireUpstream": false,
 *     "commitArgs": "--no-verify",
 *     "--requireBranch": "master"
 *   },
 *   "plugins": {
 *     "@release-it/conventional-changelog": {
 *       "preset": "angular",
 *       "infile": "CHANGELOG.md"
 *     }
 *   }
 * }
 * </code>
 */
def release(Map args) {

    if (!args.gitUrl) {
        throw new Exception('gitUrl not mentioned')
    }

    if (!args.gitCredentialsId) {
        throw new Exception('gitCredentialsId not mentioned')
    }

    def commitMessage = git.lastCommitMessage()
    if (commitMessage.indexOf("chore: release v") >= 0) {
        return
    }

    def packageManager = "${args.packageManager ? args.packageManager : 'npm'}"
    def releaseManager = args.releaseManager ? args.releaseManager : 'release-it'

    def command = """${args.preCommand ? args.preCommand : ''}"""

    if (args.npmTokenCredentialId) {
        withCredentials([
            string(credentialsId: npmTokenCredentialId, variable: 'NPMJS_AUTH_TOKEN')
        ]) {
            command = """
${command}

npm config set registry http://registry.npmjs.com
npm set //registry.npmjs.com/:_authToken ${NPMJS_AUTH_TOKEN}
"""
        }
    }

    withCredentials([usernamePassword(
        credentialsId: args.gitCredentialsId,
        usernameVariable: 'GIT_USER',
        passwordVariable: 'GIT_TOKEN'
    )]) {
        command = """
${command}

git remote rm origin;
git remote add origin https://${GIT_USER}:${GIT_TOKEN}@${args.gitUrl};
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
        def releaseItArgs = args.releaseItArgs ? args.releaseItArgs : 'patch'

        command = """
${command}

${packageManager} run release -- ${releaseItArgs};
"""
    }

    if (!args.noNpmPublish) {
        command = """
${command}

npm publish;
"""
    }

    if (args.debug) {
      echo command

      return
    }

    try {
        sh """
set -ex
${command}
set +x
"""
    /* groovylint-disable-next-line CatchException */
    } catch (Exception ex) {
        powershell """
Set-PSDebug -Trace 1;

${command}
"""
    }
}
