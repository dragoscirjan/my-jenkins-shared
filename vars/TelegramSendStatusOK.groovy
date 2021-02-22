

def call(def token, def chatId) {
    def message = """-------------------------------------\n\
Jenkins build *OK!*\n\
Repository:  ${env.JOB_NAME}\n\
Branch:      ${env.BRANCH_NAME}\n\
*Commit Msg:*\n\
...TODO:\n\
[Job Log here](${env.BUILD_URL}/consoleText)\n\
--------------------------------------"""

    TelegramSend(message, token, chatId)
}