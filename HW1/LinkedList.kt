package edu.davis.cs.ecs036c

import org.w3c.dom.Node

/**
 * Welcome to the first normal data structure homework assignment for ECS 032C
 *
 * In this you will be implementing many functions for the most
 * basic LinkedList class.  It is strongly advised that you start by
 * implementing toLinkedList (which accepts a vararg array), append
 * (which toLinkedList uses), and get first, as those 3 function are
 * required for all the test code.
 *
 * You will also need to write a lot of tests, as we provide only the
 * most basic tests and the autograder deliberately hides the test results
 * until after the grades are released.
 */

/**
 * A placeholder function that we use to specify that you need
 * to implement this particular piece of code
 */
fun toDo(): Nothing {
    throw Error("Need to implement")
}

/**
 * This is the data class for a cell within the LinkedList.  You won't need to
 * add anything to this class.
 */
data class LinkedListCell<T>(var data:T, var next:LinkedListCell<T>?) {}

/**
 * This is the basic LinkedList class, you will need to
 * implement several member functions in this class to
 * implement a LinkedList library.
 */
class LinkedList<T> {
    // You should have 3 variables:
    // A pointer to the first cell or null
    // A pointer to the last cell or null
    // An internal record to keep track of the number
    // of elements present.
    private var headCell: LinkedListCell<T>? = null
    private var tailCell: LinkedListCell<T>? = null
    private var privateSize = 0

    /**
     * This allows .size to be accessed but not set, with the private
     * variable to track the actual size elsewhere.
     */
    val size: Int
        get() = privateSize


    /**
     * You want to implement an iterator for the DATA in the LinkedList,
     * So you will need to implement a computeNext() function.
     *
     * Note: Kotlin will continue to treat at as nullable within the
     * computeNext function, because you could have some concurrent access
     * that could change the structure of the list.
     *
     * Thus in accessing the data one option you can use is an elvis operator ?:
     * to throw an IllegalStateException if there is an inconsistency
     * during iteration.
     *
     * We will not check whether your code would actually throw such an error,
     * because we aren't assuming the LinkedList class is thread safe, but by
     * doing that you can make the Kotlin type system happy.
     *
     * As an alternative you could just use the !!. operation that
     * will throw a NullPointerException instead.  Either are acceptable.
     */
    class LinkedListIterator<T>(var at: LinkedListCell<T>?):AbstractIterator<T>(){
        override fun computeNext():Unit {
            if (at == null) {
                done()
            } else {
                setNext(at!!.data) // Haven't tested this one yet. Could be very wrong
                at = at!!.next
            }
        }
    }

    /**
     * You will also want an iterator for the cells themselves in the LinkedList,
     * as there are multiple cases where you are going to want to iterate over
     * the cells not just the data in the cells.
     */
    class LinkedListCellIterator<T>(var at: LinkedListCell<T>?):AbstractIterator<LinkedListCell<T>>(){
        override fun computeNext():Unit {
            if (at == null) {
                done()
            } else {
                setNext(at!!)
                at = at!!.next
            }
        }
    }




    /**
     * Append ads an item to the end of the list.  It should be
     * a constant-time (O(1)) function regardless of the number
     * of elements in the LinkedList
     */
    fun append(item:T)  {
        val node = LinkedListCell(item, null)
        if (privateSize == 0) {
            headCell = node
            tailCell = node
        } else {
            tailCell!!.next = node
            tailCell = node
        }
        privateSize++
    }

    /**
     * Adds an item to the START of the list.  It should be
     * a constant-time (O(1)) function.
     */
    fun prepend(item: T)  {
        val node = LinkedListCell(item, headCell)
        if (privateSize == 0) {
            headCell = node
            tailCell = node
        } else {
            headCell = node
        }
        privateSize++
    }

    /**
     * Get the data at the specified index.  For a linked-list
     * this is an O(N) operation in general, but it should be O(1)
     * for both the first and last element.
     *
     * Invalid indices should throw an IndexOutOfBoundsException
     */
    operator fun get(index: Int) : T{
        if (index < 0) {
            throw IndexOutOfBoundsException("Negative Index")
        } else if (index >= size) {
            throw IndexOutOfBoundsException("Index too large")
        } else if (index == 0) {
            return headCell!!.data
        } else if (index == size - 1){
            return tailCell!!.data
        }
        var currNode = headCell!!.next
        for (i in 1..<index) {
            currNode = currNode!!.next
        }
        return currNode!!.data
    }

    /**
     * Replace the data at the specified index.  Again, this is an
     * O(N) operation, except if it is the first or last element
     * in which case it should be O(1)
     *
     * Invalid indexes should throw an IndexOutOfBoundsException
     */
    operator fun set(index: Int, data: T) : Unit {
        if (index < 0) {
            throw IndexOutOfBoundsException("Negative Index")
        } else if (index >= size) {
            throw IndexOutOfBoundsException("Index too large")
        } else if (index == 0) {
            headCell!!.data = data
            return
        } else if (index == size - 1){
            tailCell!!.data = data
            return
        }
        var currNode = headCell!!.next
        for (i in 1..<index) {
            currNode = currNode!!.next
        }
        currNode!!.data = data
    }

    /**
     * This inserts the element at the index.
     *
     * If the index isn't valid, throw an IndexOutOfBounds exception
     *
     * This should be O(1) for the start and the end, O(n) for all other cases.
     */
    fun insertAt(index: Int, value: T) {
        if (index < 0) {
            throw IndexOutOfBoundsException("Negative Index")
        } else if (index > size) {
            throw IndexOutOfBoundsException("Index too large")
        } else if (index == 0) {
            prepend(value)
            return
        } else if (index == size){
            append(value)
            return
        }
        var currNode = headCell
        for (i in 0..<(index-1)) {
            currNode = currNode!!.next
        }
        val newNode = LinkedListCell(value, currNode!!.next)
        currNode.next = newNode
        privateSize++
    }

    /**
     * This removes the element at the index and return the data that was there.
     *
     * Again, if the data doesn't exist it should throw an
     * IndexOutOfBoundsException.
     *
     * This is O(N), and there is no shortcut possible for the last element
     */
    fun removeAt(index: Int) : T {
        if (index < 0) {
            throw IndexOutOfBoundsException("Negative Index")
        } else if (index >= size) {
            throw IndexOutOfBoundsException("Index too large")
        }

        var currNode = headCell
        var prevNode = headCell
        if (size == 1) {
            headCell = null
            tailCell = null
            privateSize--
            return currNode!!.data
        } else if (index == 0) {
            headCell = headCell!!.next
            privateSize--
            return currNode!!.data
        } else {
            currNode = currNode!!.next
            for (i in 1..<(index)) {
                prevNode = currNode
                currNode = currNode!!.next
            }
            if (index == size - 1) {
                tailCell = prevNode
            }
            prevNode!!.next = currNode!!.next
            privateSize--
            return currNode.data
        }
    }

//        if (index < 0) {
//            throw IndexOutOfBoundsException("Negative Index")
//        } else if (index >= size) {
//            throw IndexOutOfBoundsException("Index too large")
//        }
//        var currNode = headCell
//        val data : T
//        if (index == 0) { // If head cell needs to be reassigned
//            data = currNode!!.data
//            headCell = headCell!!.next
//        } else {
//            for (i in 0..<(index - 1)) {
//                currNode = currNode!!.next
//            }
//            data = currNode!!.next!!.data
//        }
//        if (index == size - 1) { // If tail cell needs to be reassigned
//            if (index == 0) {
//                tailCell = null
//                privateSize--
//                return data
//            } else {
//                tailCell = currNode
//            }
//        }
//        currNode.next = currNode.next!!.next
//        privateSize--
//        return data

        /*
        var currNode = headCell
        var prevNode = headCell
        var data : T
        if (size == 1) {
            headCell = null
            tailCell = null
            privateSize--
            return currNode.data
        } elif (index == 0) {
            headCell = headCell.next
            privateSize--
            return currNode.data
        } else {
            currNode = currNode!!.next
            for (i in 1..<(index)) {
                prevNode = currNode
                currNode = currNode!!.next
            }
            data = currNode.data
            if (index == size - 1) {tailCell = prevNode}
            prevNode.next = currNode.next
            privateSize--
            return currNode.data
         }

         */
        // An example of what you should do on a state that you
        // know is unreachable but the compiler doesn't necessarily
        // know is unreachable, so it doesn't complain about a lack
        // of a return value
        // throw UnsupportedOperationException("Unreachable Code")

    /**
     * This does a linear search for the item to see
     * what index it is at, or -1 if it isn't in the list
     */
    fun indexOf(item:T) :Int {
        var index = 0
        for (x in this) {
            if (x == item) {
                return index
            }
            index++
        }
        return -1
    }
        /*
        var currNode = headCell
        var index = 0
        while (currNode != null) {
            if (currNode.data == item) {
                return index
            }
            index++
            currNode = currNode.next
        }
        return -1
         */

    /**
     * Because we have indexOf already defined, we can do
     * contains as a one-liner, so we can do (x in list) and
     * have that convention work.
     */
    operator fun contains(item:T) = (indexOf(item) != -1 )


    /**
     * This needs to return an Iterator for the data in the cells.  This allows
     * the "for (x in aLinkedList) {...}" to work as expected.
     *
     * You want your iterator to be one of the first things you ensure
     * works because you are going to want to do things like
     * for (x in this) {...} in your own internal code
     */
    operator fun iterator() = LinkedListIterator(headCell)

    /**
     * An internal helper function that returns an iterator for the
     * cells themselves.  This is very useful for both mapInPlace and
     * other functions you may need to implement.
     */
    fun cellIterator() = LinkedListCellIterator(headCell)

    /**
     * A very useful function for debugging, as it will print out
     * the list in a convenient form.  Actually showing you the code
     * as is rather than having you implement it, because it gives you
     * an idea of how powerful things are now that you have an iterator
     * and can convert that iterator to a sequence (which supports fold).
     */
    override fun toString(): String {
        return iterator()
            .asSequence()
            .fold("[") {initial , item ->
                if (initial != "[") initial + ", " + item.toString()
                else "[" + item.toString()} + "]"
    }

    /**
     * Of course, however, you have to implement your own version of fold
     * directly...  If the list is empty, fold returns the initial value.
     *
     * Otherwise, it accumulates a new value by applying the function
     * for each element.  See the toString() function for an example of
     * how to use fold
     */
    fun <R>fold(initial: R, operation: (R, T) -> R): R {
        if (privateSize == 0) {
            return initial
        }
        var result = initial
        var currNode = headCell
        while (currNode != null) {
            result = operation(result, currNode.data)
            currNode = currNode.next
        }
        return result
    }

    /**
    * And you need to implement map, creating a NEW LinkedList
    * and applying the function to each element in the old list.
    *
    * One useful note, because append is constant time, you
    * can just go in order and make a new list.
    */
    fun <R>map(operator: (T)->R): LinkedList<R>{
        val result = LinkedList<R>()
        for (x in this) {
            result.append(operator(x))
        }
        return result
    }

    /**
     * You also need to implement mapInPlace.  mapInPlace is like Map with a difference:
     * instead of creating a new list it applies the function to each data
     * element and uses that to replace the elements in the existing list, returning
     * the list itself when done.
     */
    fun mapInPlace(operator: (T)->T) : LinkedList<T> {
        for (x in cellIterator()) {
            x.data = operator(x.data)
        }
        return this
    }
        /* var result = this
        var currNode = headCell
        while (currNode != null) {
            currNode.data = operator(currNode.data)
            currNode = currNode.next
        }
        return result
         */


    /**
     * Filter creates a new list by only adding the elements in the original list
     * that are true when the operator is applied to the element.
     */
    fun filter(operator: (T)->Boolean) : LinkedList<T>{
        val result = LinkedList<T>()
        var currNode = headCell
        while (currNode != null) {
            if (operator(currNode.data)) { result.append(currNode.data) }
            currNode = currNode.next
        }
        return result
    }

    /**
     * And filterInPlace.  filterInPlace will keep only the elements
     * that are true.
     */
    fun filterInPlace(operator: (T)->Boolean) : LinkedList<T>{
        if (privateSize == 0) {
            return this
        }

        // Find headCell
        var valid = headCell
        while (valid != null) {
            if (operator(valid.data)) {
                headCell = valid
                break
            } else {
                valid = valid.next
                privateSize--
            }
        }

        // Quick check for if headCell doesn't exist
        if (privateSize == 0) {
            headCell = null
            tailCell = null
            return this
        } // Since privateSize not zero, guaranteed ONE element (the valid headCell)

        // Doing rest of body
        var search = valid!!.next
        while (search != null) {
            if (operator(search.data)) {
                valid!!.next = search
                valid = search
            } else {
                privateSize--
            }
            search = search.next
        }
        valid!!.next = null
        tailCell = valid
        return this
    }
}

/**
 * And this function builds a new LinkedList of the given type with
 * a vararg (variable argument) set of inputs.  You should
 * implement this first as all other tests will depend on this.
 */
fun <T> toLinkedList(vararg input:T) : LinkedList<T> {
    val retval = LinkedList<T>()
    for (item in input) {
        retval.append(item)
    }
    return retval
}