def call() {
  try {
    return sh(
      script: "git log -1 --oneline --pretty=\"format:%s\"",
      returnStdout: true
    ).trim()
  } catch (Exception ex) {
    return powershell(
      script: "git log -1 --oneline --pretty=\"format:%s\"",
      returnStdout: true
    ).trim()
  }
}
