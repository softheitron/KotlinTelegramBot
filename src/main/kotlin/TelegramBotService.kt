package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    private val httpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val getUpdatesRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
        val getUpdatesResponse = httpClient.send(getUpdatesRequest, HttpResponse.BodyHandlers.ofString())
        return getUpdatesResponse.body()
    }

    fun sendMessage(chatId: Int, message: String) {
        val sendMessageUrl = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$message"
        val sendMessageRequest = HttpRequest.newBuilder().uri(URI.create(sendMessageUrl)).build()
        httpClient.send(sendMessageRequest, HttpResponse.BodyHandlers.ofString())
    }

}