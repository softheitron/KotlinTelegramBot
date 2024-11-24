package org.example

import java.io.File

data class Word(
    val originalWord: String,
    val translatedWord: String,
    val correctAnswersCount: Int = 0
)

fun main() {

    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()
    val fileLines = wordsFile.readLines()

    fileLines.forEach { line ->
        val splitLine = line.split("|")
        dictionary.add(
            Word(
                originalWord = splitLine[0],
                translatedWord = splitLine[1],
                correctAnswersCount = splitLine.getOrNull(2)?.toIntOrNull() ?: 0
            )
        )
    }

    dictionary.forEach {
        println("${it.originalWord} - ${it.translatedWord}|count: ${it.correctAnswersCount}")
    }

}