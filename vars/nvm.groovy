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
  try {
    sh "uname > /dev/null"
    sh """
set +ex;
export NVM_DIR="\$HOME/.nvm";
[ -s "\$NVM_DIR/nvm.sh" ] && . "\$NVM_DIR/nvm.sh";

nvm install ${version};
nvm use ${version};

set -ex;
${command}
"""
  } catch (Exception e) {
    powershell """
if ((Get-Command nvm).Command) {
  nvm install ${version};
  nvm use ${version};
}

Set-PSDebug -Trace 1;
${command}
"""
  }
}
