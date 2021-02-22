
def call(def status, def token, def chatId) {
    def icon = ':yellow_heart:'
    if (status.toLowerCase() == 'ok') {
        icon = ':green_heart:'
    }
    if (status.toLowerCase() == 'fail') {
        icon = ':broken_heart:'
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