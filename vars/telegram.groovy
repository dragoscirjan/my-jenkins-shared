
// https://testdriven.io/blog/getting-telegram-notifications-from-travis-ci/
def send(def telegramTokenCredentialId, def telegramChatIdCredentialId, Map args) {
  withCredentials([
    string(credentialsId: telegramTokenCredentialId, variable: 'TL_TOKEN'),
    string(credentialsId: telegramChatIdCredentialId, variable: 'TL_CHAT_ID')
  ]) {
    try {
      sh """
        curl -s -X POST https://api.telegram.org/bot${TL_TOKEN}/sendMessage \
          -d chat_id=${TL_CHAT_ID} \
          -d parse_mode="${args.parseMode ? args.parseMode : 'Markdown'}" \
          -d text="${args.message ? args.message : 'Telegram bot is alive!'}"
      """
    } catch (Exception ex) {
      powerhsell """
        echo TODO:
      """
    }
  }
}

def sendStatus(def telegramTokenCredentialId, def telegramChatIdCredentialId, def status) {
  // https://unicode.org/emoji/charts/full-emoji-list.html
  def icon = ''
  if (status.toLowerCase() == 'ok') {
    icon = '🎉'
  }
  if (status.toLowerCase() == 'fail') {
    icon = '🌧'
  }

    def message = """-------------------------------------
${icon} Jenkins build: *${status.toUpperCase()}!*
-------------------------------------
Repository:    ${env.JOB_NAME}
Branch:        ${env.BRANCH_NAME}
Params:        ${JsonOutput.toJson(params ? params : [])}
*Commit Msg:*
...TODO:

[Job Log here](${env.BUILD_URL}/consoleText)
--------------------------------------"""

  send(telegramTokenCredentialId, telegramChatIdCredentialId, [message: message])
}

def sendStatusFail(def telegramTokenCredentialId, def telegramChatIdCredentialId) {
  sendStatus(telegramTokenCredentialId, telegramChatIdCredentialId, 'Fail')
}

def sendStatusOk(def telegramTokenCredentialId, def telegramChatIdCredentialId) {
    sendStatus(telegramTokenCredentialId, telegramChatIdCredentialId, 'OK')
}


