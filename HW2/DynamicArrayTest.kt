package edu.ucdavis.cs.ecs036c

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.security.SecureRandom
import kotlin.random.asKotlinRandom
import kotlin.test.assertFailsWith
import edu.ucdavis.cs.ecs036c.testing.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty
import kotlin.time.measureTime
import kotlin.reflect.jvm.javaField

/**
 * Randomness is often really useful for testing,
 * and this makes sure we have a guaranteed GOOD
 * random number generator
 */
val secureRNG = SecureRandom().asKotlinRandom()


class DynamicArrayTest  () {
    /**
     * You will need to write MANY more tests, but this is just a simple
     * example: it creates a DynamicArray<String> of 3 entries,
     * and then calls the toString() function.  Since toString needs
     * iterator to work this actually tests a remarkable amount of your code!
     */
    @Test
    fun testInit(){
        val testArray = arrayOf("A", "B", "C")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toDynamicArray(*testArray)
        assert(data.toString() == "[A, B, C]")
    }

    @Test
    fun testInitResize(){
        val testArray = arrayOf("A", "B", "C", "D", "E", "F")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toDynamicArray(*testArray)
        assert(data.toString() == "[A, B, C, D, E, F]")
    }


    /**
     * Similarly, we give you this test as well.
     */
    @Test
    fun testBasicInit(){
        val testArray = arrayOf(0,1,2,3,4,5)
        testArray.shuffle(random = secureRNG)
        val data = toDynamicArray(*testArray)
        // Useful kotlin shortcut:  Kotlin's native Arrays
        // (and also our DynamicArray) has a dynamic
        // field
        for(x in data.indices){
            assert(testArray[x] == data[x])
        }
        assert(testArray.size == data.size)
        assertFailsWith<IndexOutOfBoundsException>() {
            data[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data[data.size]
        }
    }

    @Test
    fun testBasicNullableData(){
        val data = DynamicArray<Int?>()
        for(x in 0..<5){
            data.append(x)
        }
        for(x in 5..<10){
            data.append(null)
        }
        for(x in 0..<10){
            if(x < 5){
                assert(data[x] == x)
            } else {
                assert(data[x] == null)
            }
        }
    }

    /*
     * An example of a timing test, ensuring that append() is constant time.
     * You will want other timing tests as well.
     */
    @Test
    @Timeout(5, unit = TimeUnit.SECONDS)
    fun testTiming(){
        fun internal(iterationCount : Int){
            val data: DynamicArray<Int> = toDynamicArray()
            for(x in 0..<iterationCount){
                assert(data.size == x)
                data.append(x)
                assert(data[data.size-1] == x)
            }
        }
        val small = measureTime{internal(1000)}
        val large = measureTime{internal(100000)}
        // We have it be 200 rather than 100 to account for measurement errors
        // in timing, as this is something that can routinely occur.
        assert(large < (small * 200))
    }


    /*
     * This is how we check to make sure you don't add any more
     * fields to the class and that the types are right.
     *
     * We are making this test public so you can both see how it
     * works (it is a fair bit of interesting meta-programming,
     * that is, programming to manipulate programming)
     * and to make sure that you don't add any more fields
     * to your class.
     */
    @Test
    @GradescopeAnnotation("Test Introspection", maxScore = 0)
    fun testIntrospection(){
        val fields = DynamicArray::class.members
        val allowedFields = mapOf("privateSize" to "int",
            "size" to null,
            "start" to "int",
            "storage" to "java.lang.Object[]",
            "indices" to null
        )
        for (item in fields){
            if (item is KProperty){
                if (item.name !in allowedFields){
                    println("Unknown additional varibale: "+ item.name)
                    assert(false)
                }
                val javaType = item.javaField?.type?.getTypeName()
                if(javaType != allowedFields[item.name]){
                    println("Declared " + item.name + " as incorrect type " +
                    javaType)
                    assert(false)
                }
            }
        }
    }

    @Test
    fun testInit2(){
        val testArray = arrayOf("A", "B", "C")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toDynamicArray(*testArray)
        assert(data.toString() == "[A, B, C]")
    }

    @Test
    fun testIterator(){
        val testArray = arrayOf("A", "B", "C")
        val data = toDynamicArray(*testArray)
        val tempArray = DynamicArray<String>()
        for (x in data) {
            tempArray.append(x)
        }

        assert(tempArray.toString() == "[A, B, C]")
        tempArray.mapInPlace{ it + " bee"}
        assert(tempArray.toString() == "[A bee, B bee, C bee]")
    }

    /**
     * Similarly, we give you this test as well.  It will require that
     * you implement the toDynamicArray operation, which requires
     * also implementing append.  It also requires implementing
     * get.
     */
    @Test
    fun testBasicInit2(){
        val testArray = arrayOf(0,1,2,3,4,5)
        testArray.shuffle(random = secureRNG)
        val testList = toDynamicArray(*testArray)
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
    fun testBasicNullableData2(){
        val testList = DynamicArray<Int?>()
        testList.push(50)
        testList.pushLeft(60)
        testList.pop()
        testList.popLeft()
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
        val testList = DynamicArray<Int?>()
        for(x in 0..<5){
            testList.append(x)
        }
        for(x in 5..<10){
            testList.append(null)
        }
        assert(testList[0] == 0)
        testList.push(50)
        testList.pushLeft(60)
        testList.pop()
        testList.popLeft()
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
        val testList = DynamicArray<Int?>()
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
        testList.push(50)
        testList.pushLeft(60)
        testList.pop()
        testList.popLeft()
        assert(testList[10] == 0)
        testList.removeAt(0)
        assert(testList.removeAt(testList.size-2) == 13)
    }

    @Test
    fun testGetAndIndex() {
        val testList = DynamicArray<Int?>()
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
        testList.push(50)
        testList.pushLeft(60)
        testList.pop()
        testList.popLeft()
        testList[8] = 20
        assert(testList[8] == 20)
        testList[9] = 30
        assert(testList[9] == 30)
        assert(20 in testList)
    }

    @Test
    fun testRemoveElementsSmallList() {
        val testList = DynamicArray<Int?>()
        for(x in 0..<4) {
            testList.append(x)
        }
        assert(testList.removeAt(2) == 2)
        assert(testList.removeAt(2) == 3)
        testList.removeAt(0)
        assert(testList.toString() == "[1]")
        testList.append(2)
        testList.push(50)
        testList.pushLeft(60)
        testList.pop()
        testList.popLeft()
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
        val testList = DynamicArray<Int>()
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
        val data = toDynamicArray(*testArray)
        val out = data.map {it*50}
        data.push(50)
        data.pushLeft(60)
        data.pop()
        data.popLeft()
        assert(out.toString() == "[0, 50, 100, 150]")
    }

    @Test
    fun testMapInPlace() {
        val testArray = arrayOf(0, 1, 2, 3)
        val data = toDynamicArray(*testArray)
        data.mapInPlace {it*50}
        assert(data.toString() == "[0, 50, 100, 150]")
    }

    @Test
    fun testFilter() {
        val testArray = arrayOf(0, 1, 2, 3)
        var data = toDynamicArray(*testArray)
        var mapList = data.filter {it%2 == 0}
        data.push(50)
        data.pushLeft(60)
        data.pop()
        data.popLeft()
        assert(mapList.toString() == "[0, 2]")
        for (i in 0..<12) {
            mapList.append(i)
        }
        data = mapList.filter {it > 5}
        mapList = data.filter {it == 6}
        for (i in 0..<12) {
            mapList.append(i)
        }
    }

    @Test
    fun testFilterInPlace() {
        val testArray = arrayOf(0, 1, 2, 3)
        val data = toDynamicArray(*testArray)
        data.filterInPlace {it%2 == 0}
        assert(data.toString() == "[0, 2]")
        data.push(50)
        data.pushLeft(60)
        data.pop()
        data.popLeft()
        data.prepend(1)
        data.filterInPlace {it%2 == 0}
        assert((data[0] == 0) && (data[data.size-1] == 2))
    }

    @Test
    fun testCorrectErrors() {
        val testArray = arrayOf(0,1,2,3,4,5)
        val testList = toDynamicArray(*testArray)
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
        val data = toDynamicArray(*testArray)
        data.push(50)
        data.pushLeft(60)
        data.pop()
        data.popLeft()
        data.mapInPlace { 6000 }
        for (x in data) {
            assert(x == 6000)
        }
    }

    @Test
    fun testTheBigKahuna() {
        val data = DynamicArray<Int>()
        for (i in 0..<4) {
            data.append(i)
        }
        for (i in 0..<4) {
            data.removeAt(0)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data[0]
        }
        data.push(50)
        data.pushLeft(60)
        data.pop()
        data.popLeft()
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
        val list = DynamicArray<Int>()
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
        val list = DynamicArray<Int>()
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
        val list = DynamicArray<String>()
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

    @Test
    fun testRemoveAt() {
        val list = DynamicArray<Int>()
        list.prepend(5)
        assert(list.removeAt(0) == 5)
        for (i in 1..<3) {
            list.prepend(i)
        }
        for (i in 0..<2) {
            list.removeAt(0)
        }

        for (i in 1..<6) {
            list.append(i)
        }
        for (i in 1..<6) {
            list.prepend(i)
        }
        assert(list.removeAt(0) == 5)
        assert(list.removeAt(0) == 4)
        assert(list.removeAt(list.size -1) == 5)
        assert(list.removeAt(list.size -1) == 4)
        assert(list.removeAt(4) == 2)

    }
}