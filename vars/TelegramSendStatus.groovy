
def call(def status, def token, def chatId) {
    def message = """-------------------------------------\n
Jenkins build: *${status.toUpperCase()}!*\n
-------------------------------------\n
Repository:    ${env.JOB_NAME}\n
Branch:        ${env.BRANCH_NAME}\n
*Commit Msg:*\n\
...TODO:\n

[Job Log here](${env.BUILD_URL}/consoleText)\n
--------------------------------------"""

    TelegramSend(message, token, chatId)
}