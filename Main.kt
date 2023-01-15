package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

val start = System.currentTimeMillis()

class Words(private val wordsFile: File, candidatesFile: File) {
    init {
        if (!wordsFile.exists()) println("Error: The words file ${wordsFile.name} doesn't exist.")
            .also { exitProcess(0) }
        if (!candidatesFile.exists()) println("Error: The candidate words file ${candidatesFile.name} doesn't exist.")
            .also { exitProcess(0) }
    }

    private var counter = 0
    private val invalidLetters = mutableSetOf<Char>()
    private val words = mutableListOf<String>()
    private val wordsList = checkWordsInFile(wordsFile)
    private val candidateList = checkWordsInFile(candidatesFile)
    private val secretWord = candidateList[Random.nextInt(0, candidateList.size)]

    private fun checkWord(word: String) {
        if (word.length != 5) counter++.also { return }
        if (!word.matches("""[a-zA-Z]+""".toRegex())) counter++.also { return }
        if (word.toCharArray().distinct().size != word.toCharArray().size) counter++
    }

    fun checkInputWord(word: String) {
        counter++

        when {
            word == "exit" -> println("The game is over.").also { exitProcess(0) }
            word.length != 5 -> println("The input isn't a 5-letter word.")
            !word.matches("""[a-z]+""".toRegex()) -> println("One or more letters of the input aren't valid.")
            word.toCharArray().distinct().size != word.toCharArray().size -> println("The input has duplicate letters.")
            !wordsList.contains(word) -> println("The input word isn't included in my words list.")

            word == secretWord -> {
                val end = System.currentTimeMillis()
                println(words.joinToString("\n"))
                println(word.uppercase())
                println("Correct!")
                if (counter == 1) {
                    println("Amazing luck! The solution was found at once.")
                } else {
                    println("The solution was found after $counter tries in ${(end - start)/1000} seconds.")
                }
                exitProcess(0)
            }

            else -> {
                var txt = ""
                word.forEach {
                    if (secretWord.contains(it)) {
                        if (word.indexOf(it) == secretWord.indexOf(it)) {
                            txt += it.uppercase()
                        } else {
                            txt += it
                        }
                    } else {
                        invalidLetters.add(it)
                        txt += "_"
                    }
                }
                words.add(txt)
                println(words.joinToString("\n"))
                println()
                println(invalidLetters.sorted().joinToString("").uppercase())
            }
        }
    }

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
