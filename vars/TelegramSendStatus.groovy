
def call(def status, def token, def chatId) {
    def message = """-------------------------------------
Jenkins build: *${status.toUpperCase()}!*
> Repository:    ${env.JOB_NAME}
> Branch:        ${env.BRANCH_NAME}
> *Commit Msg:*\
> ...TODO:
> 
> [Job Log here](${env.BUILD_URL}/consoleText)
--------------------------------------"""

    TelegramSend(message, token, chatId)
}