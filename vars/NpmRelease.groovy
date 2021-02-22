


def call(def releaseArgs = 'patch', def packageManager = 'npm') {
  def commitMessage = GitLastCommitMessage()

  releaseArgs += ' --no-git.requireUpstream --git.commitArgs=--no-verify'

  if (commitMessage.indexOf("chore: release v") < 0) {
    withCredentials([usernamePassword(
      credentialsId: '52e756f6-5625-41fb-bde9-ead983f84629',
      usernameVariable: 'GIT_USER',
      passwordVariable: 'GIT_TOKEN'
    )]) {
      def command = """
        set -ex;
        git remote rm origin;
        git remote add origin https://${GIT_USER}:${GIT_TOKEN}@github.com/mists-aside/tempjs.git;
        git fetch;
        git checkout .;
        git checkout ${env.BRANCH_NAME};
        git pull origin ${env.BRANCH_NAME};
        git checkout .;
        git status;
        ${packageManager} install;
        ${packageManager} run release -- ${releaseArgs};
        ${packageManager} publish;
      """
    }

    echo releaseArgs;

    // try {
    //   sh command
    // } catch (Exception ex) {
    //   powershell command
    // }
  }

}
