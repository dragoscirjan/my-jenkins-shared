
def call(def telegramTokenCredentialId, def telegramChatIdCredentialId, def status) {
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
*Commit Msg:*
...TODO:

[Job Log here](${env.BUILD_URL}/consoleText)
--------------------------------------"""

    TelegramSend(telegramTokenCredentialId, telegramChatIdCredentialId, [message: message])
}
