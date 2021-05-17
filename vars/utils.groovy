


def runSh(String command) {
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
Set-PSDebug -Trace 0;
"""
  }
}
