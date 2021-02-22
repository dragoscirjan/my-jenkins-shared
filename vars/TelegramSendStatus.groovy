
def call(def status, def token, def chatId) {
    if (status.toLowerCase() == 'ok') {
        status = "<span style='color: green;'>${status}</status>"
    }
    if (status.toLowerCase() == 'failed') {
        status = "<span style='color: red;'>${status}</status>"
    }


    def message = """-------------------------------------\n\
Jenkins build *${status}!*\n\
Repository:  ${env.JOB_NAME}\n\
Branch:      ${env.BRANCH_NAME}\n\
*Commit Msg:*\n\
...TODO:\n\
[Job Log here](${env.BUILD_URL}/consoleText)\n\
--------------------------------------"""

    TelegramSend(message, token, chatId)
}