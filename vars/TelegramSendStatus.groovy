
def call(def status, def token, def chatId) {
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

    TelegramSend(message, token, chatId)
}