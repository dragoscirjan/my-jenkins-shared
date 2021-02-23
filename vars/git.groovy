

def lastCommitMessage() {
  def command = "git log -1 --oneline --pretty=\"format:%s\""

  try {
    return sh(script: command, returnStdout: true).trim()
  } catch (Exception ex) {
    return powershell(script: command, returnStdout: true).trim()
  }
}