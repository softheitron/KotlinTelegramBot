package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val STATISTICS_CLICK = "statistics_clicked"
const val LEARN_WORDS_CLICK = "learn_words_clicked"
const val TELEGRAM_API_URL = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {

    private val httpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "$TELEGRAM_API_URL$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = httpClient.send(getUpdatesRequest, HttpResponse.BodyHandlers.ofString())
        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: Long, message: String) {
        val encoded = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        )
        val sendMessageUrl = "$TELEGRAM_API_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl)).build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Long) {
        val sendMessageUrl = "$TELEGRAM_API_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$LEARN_WORDS_CLICK"
                            }
                        ],
                        [
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS_CLICK"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

}