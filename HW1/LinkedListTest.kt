package edu.davis.cs.ecs036c

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.security.SecureRandom
import kotlin.random.asKotlinRandom
import kotlin.test.assertFailsWith
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.measureTime

/**
 * Randomness is often really useful for testing,
 * and this makes sure we have a guaranteed GOOD
 * random number generator
 */
val secureRNG = SecureRandom().asKotlinRandom()


class LinkedListTest  () {

    /**
     * You will need to write MANY more tests, but this is just a simple
     * example: it creates a LinkedList<String> of 3 entries,
     * and then calls the toString() function.  Since toString needs
     * iterator to work this actually tests a remarkable amount of your code!
     */
    @Test
    fun testInit(){
        val testArray = arrayOf("A", "B", "C")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toLinkedList(*testArray)
        assert(data.toString() == "[A, B, C]")
    }

    @Test
    fun testIterator(){
        val testArray = arrayOf("A", "B", "C")
        val data = toLinkedList(*testArray)
        val tempArray = LinkedList<String>()
        for (x in data) {
            tempArray.append(x)
        }
        assert(tempArray.toString() == "[A, B, C]")
        tempArray.mapInPlace{ it + " bee"}
        assert(tempArray.toString() == "[A bee, B bee, C bee]")
    }

    /**
     * Similarly, we give you this test as well.  It will require that
     * you implement the toLinkedList operation, which requires
     * also implementing append.  It also requires implementing
     * get.
     */
    @Test
    fun testBasicInit(){
        val testArray = arrayOf(0,1,2,3,4,5)
        testArray.shuffle(random = secureRNG)
        val testList = toLinkedList(*testArray)
        for(x in 0..<testArray.size){
            assert(testArray[x] == testList[x])
        }
        assert(testArray.size == testList.size)
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testList.size]
        }
    }

    @Test
    fun testBasicNullableData(){
        val testList = LinkedList<Int?>()
        for(x in 0..<5){
            testList.append(x)
        }
        for(x in 5..<10){
            testList.append(null)
        }
        for(x in 0..<10){
            if(x < 5){
                assert(testList[x] == x)
            } else {
                assert(testList[x] == null)
            }
        }
    }

    @Test
    fun testHeadCell() {
        val testList = LinkedList<Int?>()
        for(x in 0..<5){
            testList.append(x)
        }
        for(x in 5..<10){
            testList.append(null)
        }
        assert(testList[0] == 0)
        assert(testList[1] == 1)
        assert(testList[2] == 2)
        testList.removeAt(0)
        assert(testList[0] == 1)
        testList.insertAt(0, 10)
        assert(testList[0] == 10)
        testList.prepend(0)
        assert(testList[0] == 0)
        testList.removeAt(0)
        assert(testList.removeAt(testList.size-2) == null)
    }

    @Test
    fun testTailCell() {
        val testList = LinkedList<Int?>()
        assert(testList.size == 0)
        for(x in 0..<5){
            testList.append(null)
        }
        for(x in 5..<10){
            testList.append(x)
        }
        assert(testList[9] == 9)
        testList.removeAt(testList.size-1)
        assert(testList[8] == 8)
        testList.insertAt(testList.size, 13)
        assert(testList[9] == 13)
        testList.append(0)
        assert(testList[10] == 0)
        testList.removeAt(0)
        assert(testList.removeAt(testList.size-2) == 13)
    }

    @Test
    fun testGetAndIndex() {
        val testList = LinkedList<Int?>()
        assert(testList.size == 0)
        for(x in 0..<5){
            testList.append(null)
        }
        for(x in 5..<10){
            testList.append(x)
        }
        assert(testList.indexOf(8) == 8)
        assert(testList.indexOf(null) == 0)
        assert(testList.indexOf(100) == -1)
        testList[8] = 20
        assert(testList[8] == 20)
        testList[9] = 30
        assert(testList[9] == 30)
        assert(20 in testList)
    }

    @Test
    fun testRemoveElementsSmallList() {
        val testList = LinkedList<Int?>()
        for(x in 0..<4) {
            testList.append(x)
        }
        assert(testList.removeAt(2) == 2)
        assert(testList.removeAt(2) == 3)
        testList.removeAt(0)
        assert(testList.toString() == "[1]")
        testList.append(2)
        assert(testList.removeAt(1) == 2)
        assert(testList.toString() == "[1]")
        assert(testList.removeAt(0) == 1)
        assert(testList.toString() == "[]")
        for(x in 0..<10) {
            testList.append(x)
        }
        assert(testList.removeAt(8) == 8)
    }

    @Test
    fun testFold() {
        val testList = LinkedList<Int>()
        for(x in 0..<4) {
            testList.append(x)
        }
        assert(testList.fold(0) {initial, element -> initial+element} == 6)
        for (i in 0..<testList.size) {
            testList.removeAt(0)
        }
        assert(testList.fold(20) {initial, element -> initial+element} == 20)
    }

    @Test
    fun testMap() {
        val testArray = arrayOf(0, 1, 2, 3)
        val data = toLinkedList(*testArray)
        val out = data.map {it*50}
        assert(out.toString() == "[0, 50, 100, 150]")
    }

    @Test
    fun testMapInPlace() {
        val testArray = arrayOf(0, 1, 2, 3)
        val data = toLinkedList(*testArray)
        data.mapInPlace {it*50}
        assert(data.toString() == "[0, 50, 100, 150]")
    }

    @Test
    fun testFilter() {
        val testArray = arrayOf(0, 1, 2, 3)
        var data = toLinkedList(*testArray)
        var mapList = data.filter {it%2 == 0}
        assert(mapList.toString() == "[0, 2]")
        for (i in 0..<12) {
            mapList.append(i)
        }
        data = mapList.filter {it > 5}
        mapList = data.filter {it == 6}
        for (i in 0..<12) {
            mapList.append(i)
        }
        mapList = mapList.filter {it == 11}
    }

    @Test
    fun testFilterInPlace() {
        val testArray = arrayOf(0, 1, 2, 3)
        val data = toLinkedList(*testArray)
        var filter = LinkedList<Int>()
        data.filterInPlace {it%2 == 0}
        assert(data.toString() == "[0, 2]")
        data.prepend(1)
        data.filterInPlace {it%2 == 0}
        assert((data[0] == 0) && (data[data.size-1] == 2))
        filter = data.filterInPlace { it == 200 }
        assert(data.toString() == "[]")
    }

    @Test
    fun testCorrectErrors() {
        val testArray = arrayOf(0,1,2,3,4,5)
        val testList = toLinkedList(*testArray)
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testList.size]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1] = 0
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testList.size] = 0
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList.insertAt(-1, 0)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList.insertAt(testList.size+1, 0)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList.removeAt(-1)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList.removeAt(testList.size)
        }
    }

    @Test
    fun testCellIterator() {
        val testArray = arrayOf(0, 2, 4, 6)
        val data = toLinkedList(*testArray)
        var spam = LinkedList<Int>()
        for (x in data.cellIterator()) {
            assert(x.data%2 == 0)
            x.data = 6000
        }
        for (x in data) {
            assert(x == 6000)
        }
    }

    @Test
    fun testTheBigKahuna() {
        val data = LinkedList<Int>()
        for (i in 0..<4) {
            data.append(i)
        }
        for (i in 0..<4) {
            data.removeAt(0)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data[0]
        }
        data.prepend(10)
        for (i in 0..<3) {
            data.insertAt(1, i)
        }
        for (i in 0..<3) {
            assert(data[1+i] == 2-i)
        }
        data.filterInPlace { it%2 == 0 }
        assert(data.toString() == "[10, 2, 0]")
        assert(data.indexOf(0) == 2)
        assert(!data.contains(20))
        data[2] = 2
        assert(!data.contains(0))
        assert(data.contains(2))
        assert(data[2] == 2)
        data.removeAt(data.size-1)
        for (i in 0..<3) {
            data.insertAt(2, i)
        }
        data.mapInPlace { it*7 }
        data.filterInPlace { it == 0 }
        assert(data.removeAt(data.size-1) == 0)
        for (i in 0..<7) {
            data.append(i)
        }
        data.insertAt(4, 100)
        assert(data[4] == 100)
        data[0] = 60
        assert(data[0] == 60)
    }

    @Test
    fun testFoldSum() {
        // Create a linked list with integers from 1 to 5
        val list = LinkedList<Int>()
        for (i in 1..<6) {
            list.append(i)
        }

        // Use fold to calculate the sum of elements
        val sum = list.fold(0) { acc, element ->
            acc + element
        }

        // Expected result: 1 + 2 + 3 + 4 + 5 = 15
        assert(15 == sum)
    }

    @Test
    fun testFoldProduct() {
        // Create a linked list with integers from 1 to 5
        val list = LinkedList<Int>()
        for (i in 1..<6) {
            list.append(i)
        }

        // Use fold to calculate the product of elements
        val product = list.fold(1) { acc, element ->
            acc * element
        }

        // Expected result: 1 * 2 * 3 * 4 * 5 = 120
        assert(120 == product)
    }

    @Test
    fun testFoldConcatenate() {
        // Create a linked list with strings
        val list = LinkedList<String>()
        list.append("Hello")
        list.append(", ")
        list.append("World")
        list.append("!")

        // Use fold to concatenate strings
        val result = list.fold("") { acc, element ->
            acc + element
        }

        // Expected result: "Hello, World!"
        assert("Hello, World!" == result)
    }
}
