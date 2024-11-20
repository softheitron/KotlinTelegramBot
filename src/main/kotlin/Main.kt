package org.example

import java.io.File

fun main() {

    val wordsFile = File("words.txt")
    wordsFile.writeText("hello привет")
    wordsFile.appendText("\ndog собака\ncat кошка")

    wordsFile.readLines().forEach { word ->
        println(word)
    }


}