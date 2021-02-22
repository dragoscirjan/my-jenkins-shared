
// https://testdriven.io/blog/getting-telegram-notifications-from-travis-ci/
def call(def telegramTokenCredentialId, def telegramChatIdCredentialId, Map args) {
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
