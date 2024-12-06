package org.example

import java.io.File

data class Statistics(
    val wordsAmount: Int,
    val learnedWords: Int,
    val learnedPercent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val learnedAnswerCount: Int = MAX_CORRECT_ANSWERS,
    private val countOfQuestionWords: Int = QUESTION_WORDS_AMOUNT
) {

    private val dictionary = loadDictionary()
    private var question: Question? = null

    fun getStatistics(): Statistics {
        val wordsAmount = dictionary.size
        val learnedWords = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.size
        val learnedPercent = learnedWords * PERCENTAGE_HUNDRED / wordsAmount

        return Statistics(wordsAmount, learnedWords, learnedPercent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedWords = dictionary.filter { it.correctAnswersCount < learnedAnswerCount }
        if (notLearnedWords.isEmpty()) return null
        val questionWords = if (notLearnedWords.size < countOfQuestionWords) {
            val learnedWords = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.shuffled()
            notLearnedWords.shuffled().take(countOfQuestionWords) +
                    learnedWords.take(countOfQuestionWords - notLearnedWords.size)
        } else {
            notLearnedWords.shuffled().take(countOfQuestionWords)
        }.shuffled()

        val correctAnswer = questionWords.random()
        question = Question(
            questionWords,
            correctAnswer
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerIndex = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerIndex == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
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
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        dictionary.forEach {
            wordsFile.appendText("${it.originalWord}|${it.translatedWord}|${it.correctAnswersCount}\n")
        }
    }

}