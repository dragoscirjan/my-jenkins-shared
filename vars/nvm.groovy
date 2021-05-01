
def info() {
  try {
    sh """
set +ex;
export NVM_DIR="\$HOME/.nvm";
[ -s "\$NVM_DIR/nvm.sh" ] && . "\$NVM_DIR/nvm.sh";

nvm --version;
"""
    /* groovylint-disable-next-line CatchException */
  } catch (Exception ex) {
    powershell """
Set-PSDebug -Trace 1;

nvm --version
"""
  }
}

def runSh(String command, String version = env ? env.NODE_VERSION_DEFAULT : null) {
  if (!version) {
    throw new Exception("No node version mentioned");
  }
  sh """
export NVM_DIR="\$HOME/.nvm"
[ -s "\$NVM_DIR/nvm.sh" ] && \\. "\$NVM_DIR/nvm.sh"

nvm install ${version}
nvm use ${version}
set -ex
${command}
set +x
"""
}

def runPowershell(String command, String version = env ? env.NODE_VERSION_DEFAULT : null) {
  if (!version) {
    throw new Exception("No node version mentioned");
  }
  powershell """
nvm install ${version}
nvm use ${version}
Set-PSDebug -Trace 1;

${command}
"""
}
