package org.example

const val WELCOME_MESSAGE = "Hello!"
const val START_TEXT = "/start"

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val idTextRegex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":.\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(1000)
        val updates = telegramBotService.getUpdates(updateId)
        println(updates)

        val idMatches = idTextRegex.findAll(updates)
        val idGroups = idMatches.lastOrNull()?.groups
        updateId = idGroups?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue

        val chatIdMatches = chatIdRegex.findAll(updates)
        val chatIdGroups = chatIdMatches.lastOrNull()?.groups
        val chatId = chatIdGroups?.get(1)?.value?.toIntOrNull() ?: continue

        val matchResult = messageTextRegex.findAll(updates)
        val groups = matchResult.lastOrNull()?.groups
        val receivedText = groups?.get(1)?.value ?: "No messages yet"

        val dataMatches = dataRegex.find(updates)
        val dataGroups = dataMatches?.groups
        val data = dataGroups?.get(1)?.value

        if (receivedText.equals("hello", true)) telegramBotService.sendMessage(chatId, WELCOME_MESSAGE)
        if (receivedText.equals(START_TEXT, true)) telegramBotService.sendMenu(chatId)
        if (data.equals(STATISTICS_CLICK, true)) telegramBotService.sendMessage(chatId, "Выучено 10 из 10 слов | 100%")
    }

}


