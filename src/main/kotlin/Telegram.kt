package org.example

const val START_MENU = "/start"
const val ALL_WORDS_LEARNED_MESSAGE = "Все слова выучены"
const val RIGHT_ANSWER_MESSAGE = "Правильно!"

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
        val chatId = chatIdGroups?.get(1)?.value?.toLongOrNull() ?: continue

        val matchResult = messageTextRegex.findAll(updates)
        val groups = matchResult.lastOrNull()?.groups
        val receivedText = groups?.get(1)?.value ?: "No messages yet"

        val dataMatches = dataRegex.find(updates)
        val dataGroups = dataMatches?.groups
        val data = dataGroups?.get(1)?.value

        if (receivedText.equals(START_MENU, true)) telegramBotService.sendMenu(chatId)

        when {
            data.equals(LEARN_WORDS_CLICK) -> checkNextQuestionAndSend(trainer, telegramBotService, chatId)
            data.equals(STATISTICS_CLICK) -> showStatistics(trainer, telegramBotService, chatId)
            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX, true) == true -> {
                val answerId = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull() ?: continue
                val lastQuestion = trainer.getLastQuestion()
                checkAnswerAndSendMessage(
                    answerId,
                    lastQuestion,
                    trainer,
                    telegramBotService,
                    chatId
                )
            }
        }

    }

}

fun checkAnswerAndSendMessage(
    answerId: Int,
    lastQuestion: Question?,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    if (trainer.checkAnswer(answerId)) {
        telegramBotService.sendMessage(chatId, RIGHT_ANSWER_MESSAGE)
        checkNextQuestionAndSend(trainer, telegramBotService, chatId)
    } else {
        val wrongAnswerMessage = "Неправильно! " +
                "${lastQuestion?.correctAnswer?.originalWord}" +
                " - это ${lastQuestion?.correctAnswer?.translatedWord}"
        telegramBotService.sendMessage(chatId, wrongAnswerMessage)
        checkNextQuestionAndSend(trainer, telegramBotService, chatId)
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val nextQuestion = trainer.getNextQuestion()
    if (nextQuestion == null) {
        telegramBotService.sendMessage(chatId, ALL_WORDS_LEARNED_MESSAGE)
    } else {
        telegramBotService.sendQuestion(chatId, nextQuestion)
    }
}

fun showStatistics(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val statistics = trainer.getStatistics()
    val statisticsMessageText = "Выучено ${statistics.learnedWords} из ${statistics.wordsAmount} слов | ${statistics.learnedPercent}%\n"
    telegramBotService.sendMessage(chatId, statisticsMessageText)
}


