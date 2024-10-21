package edu.davis.cs.ecs36c.homework0

import java.io.File
import java.io.FileNotFoundException
import java.util.Scanner


val defaultFile = "/usr/share/dict/words"

// A regular expression pattern that will match words:

// A useful trick: Given the regular expression object re,
// re.findAll returns a Collection (an iteratable structure)
// that contains the words, while re.split() returns an array
// of the strings that AREN'T matched.  So you can take an input
// line, create both the split and the collection, and iterate over
// the collection keeping track of the iteration count.
val splitString = "\\p{Alpha}+"

/**
 * This function should take a filename (either the default file or the
 * one specified on the command line.)  It should create a new MutableSet,
 * open the file, and load each line into the set.
 *
 * @param filename may not exist, and in that case the function should
 * throw a FileNotFound exception.
 */
fun loadFile(filename: String): Set<String>{
    val set = mutableSetOf<String>()
    // You need to implement this
    val f = File(filename)

    if (!f.exists()) {
        throw FileNotFoundException("File attempted to be opened did not exist")
    }
    f.forEachLine {set.add(it)}
    return set
}

/**
 * This function should check if a word is valid by checking the word,
 * the word in all lower case, and the word with all but the first character
 * converted in lower case with the first character unchanged.
 */
fun checkWord(word: String, dict: Set<String>) : Boolean{
    // You need to implement this
    val str = word
    val lower = str.lowercase()
    val capitalized = "${str[0]}${lower.subSequence(1, lower.length)}"
    return ((str in dict) || (lower in dict) || (capitalized in dict))  // If any of them are in dict, returns true
}

/**
 * This function should take a set (returned from loadFile) and then
 * processes standard input one line at a time using readLine() until standard
 * input is closed
 *
 * Note: readLine() returns a String?: that is, a string or null, with null
 * when standard input is closed.  Under Unix or Windows in IntelliJ you can
 * close standard input in the console with Control-D, while on the mac it is
 * Command-D
 *
 * Once you have the line you should split it with a regular expression
 * into words and nonwords,
 */
fun processInput(dict: Set<String>){
    val re = Regex(splitString)
    // You need to implement this

//    println("Enter input")
    while (true){
        val inLine = readLine() ?: "done?" // We can treat it as a string from here on
        if (inLine == "done?") {break}
        val matches = re.findAll(inLine) // Collections of words
        val notMatches = re.split(inLine) // Not words

        val liz = matches.toList()
        var out = ""
        var i = 1
        for (match in liz) {
            var word = match.value
            if (!checkWord(word, dict)) {
                word = "$word [sic]"
            }
            print(word)
            print(notMatches[i])
            i++
        }
        if (liz.isEmpty()) {
            print(notMatches[0])
        }
        println()
    }
}

/**
 * Your main function should accept an argument on the command line or
 * use the default filename if no argument is specified.  If the dictionary
 * fails to load with a FileNotFoundException it should use
 * kotlin.system.exitProcess with status code of 55
 */
fun main(args :Array<String>) {
    // You need to implement this
    try {
        if (args.size == 1) {
            val dict = loadFile(args[0])
            processInput(dict)
        } else if (args.size == 0){
            val dict = loadFile(defaultFile)
            processInput(dict)
        } else {
            throw FileNotFoundException("Incorrect program usage")
        }
    } catch (e: FileNotFoundException){
//        println(e.message)
        kotlin.system.exitProcess(55)
    }


}