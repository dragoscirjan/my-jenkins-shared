


def call(def body) {
  def commitMessage = GitLastCommitMessage()

  body.releaseArgs = body.releaseArgs ? body.releaseArgs : 'patch'
  body.packageManager = body.packageManager ? body.packageManager : 'npm'
  body.preRun = body.preRun ? body.preRun : ''

  body.releaseArgs += ' --no-git.requireUpstream --git.commitArgs=--no-verify'

  if (commitMessage.indexOf("chore: release v") < 0) {
    withCredentials([usernamePassword(
      credentialsId: '52e756f6-5625-41fb-bde9-ead983f84629',
      usernameVariable: 'GIT_USER',
      passwordVariable: 'GIT_TOKEN'
    )]) {
      def command = """
        ${body.preRun};
        git remote rm origin;
        git remote add origin https://${GIT_USER}:${GIT_TOKEN}@github.com/mists-aside/tempjs.git;
        git fetch;
        git checkout .;
        git checkout ${env.BRANCH_NAME};
        git pull origin ${env.BRANCH_NAME};
        git checkout .;
        git status;
        ${body.packageManager} install;
        ${body.packageManager} run release -- ${body.releaseArgs};
        ${body.packageManager} publish;
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
