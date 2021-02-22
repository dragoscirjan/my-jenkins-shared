


def call(def gitCredentialsId, Map args) {
  def commitMessage = GitLastCommitMessage()

// def releaseArgs = 'patch', def packageManager = 'npm', def preRun = ''

  def releaseArgs = "${args.releaseArgs 'patch'}  --no-git.requireUpstream --git.commitArgs=--no-verify"
  def packageManager = "${args.packageManager 'npm'}"
  def preRun = "${args.preRun ''}"

  if (commitMessage.indexOf("chore: release v") < 0) {
    withCredentials([usernamePassword(
      credentialsId: gitCredentialsId,
      usernameVariable: 'GIT_USER',
      passwordVariable: 'GIT_TOKEN'
    )]) {
      def command = """
        ${preRun};
        git remote rm origin;
        git remote add origin https://${GIT_USER}:${GIT_TOKEN}@github.com/mists-aside/tempjs.git;
        git fetch;
        git checkout .;
        git checkout ${env.BRANCH_NAME};
        git pull origin ${env.BRANCH_NAME};
        git checkout .;
        git status;
        ${packageManager} install;
        ${packageManager} run release -- ${localReleaseArgs};
        ${packageManager} publish;
      """

      echo command
      // try {
      //   sh command
      // } catch (Exception ex) {
      //   powershell command
      // }
    }
  }

}
