package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

val start = System.currentTimeMillis()

// Game of words
class Words(private val wordsFile: File, candidatesFile: File) {
    init {
        if (!wordsFile.exists()) println("Error: The words file ${wordsFile.name} doesn't exist.")
            .also { exitProcess(0) }
        if (!candidatesFile.exists()) println("Error: The candidate words file ${candidatesFile.name} doesn't exist.")
            .also { exitProcess(0) }
    }

    private var counter = 0
    private val invalidLetters = mutableSetOf<String>()
    private val words = mutableListOf<String>()
    private val wordsList = checkWordsInFile(wordsFile)
    private val candidateList = checkWordsInFile(candidatesFile)
    private val secretWord = candidateList[Random.nextInt(0, candidateList.size)]

    private fun checkWord(word: String) {
        if (word.length != 5) counter++.also { return }
        if (!word.matches("""[a-zA-Z]+""".toRegex())) counter++.also { return }
        if (word.toCharArray().distinct().size != word.toCharArray().size) counter++
    }

    // Checking words that player inputs
    fun checkInputWord(word: String) {
        counter++

        when {
            word == "exit" -> println("The game is over.").also { exitProcess(0) }
            word.length != 5 -> println("The input isn't a 5-letter word.")
            !word.matches("""[a-z]+""".toRegex()) -> println("One or more letters of the input aren't valid.")
            word.toCharArray().distinct().size != word.toCharArray().size -> println("The input has duplicate letters.")
            !wordsList.contains(word) -> println("The input word isn't included in my words list.")
            else -> checkResult(word)
        }
    }

    // Checking and printing results in coloured background
    private fun checkResult(word: String) {
        if (word == secretWord) {
            val end = System.currentTimeMillis()
            println(words.joinToString("\n"))
            word.forEach { print("\u001B[48:5:10m${it.uppercase()}\u001B[0m") }
            println()
            println("Correct!")
            if (counter == 1) {
                println("Amazing luck! The solution was found at once.")
            } else {
                println("The solution was found after $counter tries in ${(end - start)/1000} seconds.")
            }
            exitProcess(0)
        }

        else {
            var txt = ""
            word.forEach {
                txt += if (secretWord.contains(it)) {
                    if (word.indexOf(it) == secretWord.indexOf(it)) {
                        "\u001B[48:5:10m${it.uppercase()}\u001B[0m"
                    } else {
                        "\u001B[48:5:11m${it.uppercase()}\u001B[0m"
                    }
                } else {
                    invalidLetters.add(it.uppercase())
                    "\u001B[48:5:7m${it.uppercase()}\u001B[0m"
                }
            }
            words.add(txt)
            println(words.joinToString("\n"))
            println()
            println("\u001B[48:5:14m${invalidLetters.sorted().joinToString("")}\u001B[0m")
        }
    }

    // Checking if file contains correct words
    private fun checkWordsInFile(file: File): List<String> {
        val listOfWords = file.readLines().map { it.lowercase() }
        listOfWords.forEach {
            checkWord(it)
        }
        if (counter == 0) {
            return listOfWords
        } else {
            println("Error: $counter invalid words were found in the ${file.name} file.").also { exitProcess(0) }
        }
    }

    // Checking if all words in candidates file are in words file
    fun checkIfCandidatesAreInWordsFile() {
        if (wordsList.containsAll(candidateList)) {
            println("Words Virtuoso")
        } else {
            counter = candidateList.count { !wordsList.contains(it) }
            println("Error: $counter candidate words are not included in the ${wordsFile.name} file.")
            exitProcess(0)
        }
    }
}

fun main(args: Array<String>) {
    if (args.size != 2) println("Error: Wrong number of arguments.").also { return }
    Words(File(args[0]), File(args[1])).apply {
        checkIfCandidatesAreInWordsFile()
        while (true) {
            println("Input a 5-letter word:")
            checkInputWord(readln().lowercase())
        }
    }
}
