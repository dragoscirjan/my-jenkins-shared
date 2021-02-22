

def TelegramSend(message, token, chatId) {
    sh """
        curl -s -X POST https://api.telegram.org/bot${token}/sendMessage \
            -d chat_id=${chatId} \
            -d parse_mode="Markdown" \
            -d text="\
-------------------------------------\n\
Jenkins build *OK!*\n\
Repository:  ${env.JOB_NAME}\n\
Branch:      ${env.BRANCH_NAME}\n\
*Commit Msg:*\n\
...TODO:\n\
[Job Log here](${env.BUILD_URL}/consoleText)\n\
--------------------------------------\n\
"
    """
}