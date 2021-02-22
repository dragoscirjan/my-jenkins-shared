def call() {
  def useShell = sh
  try {
    sh "echo"
  } catch (Exception ex) {
    useShell = powershell
  }

  return useShell(
    script: "git log -1 --oneline --pretty=\"format:%s\"",
    returnStdout: true
  ).trim()


  // try {
  //   return sh(
  //     script: "git log -1 --oneline --pretty=\"format:%s\"",
  //     returnStdout: true
  //   ).trim()
  // } catch (Exception ex) {
  //   return powershell(
  //     script: "git log -1 --oneline --pretty=\"format:%s\"",
  //     returnStdout: true
  //   ).trim()
  // }
}
