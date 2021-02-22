
def call(def message, def token, def chatId, def parseMode = 'Markdown') {
    sh """
        curl -s -X POST https://api.telegram.org/bot${token}/sendMessage \
            -d chat_id=${chatId} \
            -d parse_mode="${parseMode}" \
            -d text="${message}"
    """
}