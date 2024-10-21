package edu.ucdavis.cs.ecs036c.homework7

import java.rmi.UnexpectedException

/*
 * Class for a priority queue that supports the comparable trait
 * on elements.  It sets up to return the lowest value priority (a min heap),
 * if you want the opposite use a comparable object that is reversed.
 *
 * You could use this for implementing Dijkstra's in O(|V + E| log (V) ) time instead
 * of the default O(V^2) time.
 */
class PriorityQueue<T, P: Comparable<P>> {

    /*
     * Invariants that need to be maintained:
     *
     * priorityData must always be in heap order
     * locationData must map every data element to its
     * corresponding index in the priorityData, and
     * must not include any extraneous entries.
     *
     * You must NOT change these variable names and you MUST
     * maintain these invariants, as the autograder checks that
     * the internal structure is maintained.
     */
    val priorityData = mutableListOf<Pair<T, P>>()
    val locationData = mutableMapOf<T, Int>()

    /*
    * Size function is just the internal size of the priority queue...
    */
    val size : Int
        get() = priorityData.size



    /*
     * This is a secondary constructor that takes a series of
     * data/priority pairs.  It should put the pairs in the heap
     * and then call heapify/ensure the invariants are maintained
     */
    constructor (vararg init: Pair<T, P>) {
        var i = 0
        for (x in init) {
            if (x.first in locationData) { throw UnsupportedOperationException("Dupe key") }
            priorityData.add(x)
            locationData[x.first] = i
            i++
        }
        heapify()
    }

    /*
     * Heapify should ensure that the constraints are all updated.  This
     * is called by the secondary constructor.
     */
    fun heapify(){
        for (i in (0..<(priorityData.size / 2)).reversed() ) {
            // Sink handles swapping / checking validity
            sink(i, size)
        }
    }

    /*
     * We support ranged-sink so that this could also be
     * used for heapsort, so sink without it just specifies
     * the range.
     */
    fun sink(i : Int) {
        sink(i, priorityData.size)
    }

    /*
     * The main sink function.  It accepts a range
     * argument, that by default is the full array, and
     * which considers that only indices < range are valid parts
     * of the heap.  This enables sink to be used for heapsort.
     */

    // Relying on it to sink as far as it needs to go.
    // Needs to swap with smallest element.
    // Needs to update location data too
    fun sink(i : Int, range: Int){
        if (i < 0 || i > range) { throw UnsupportedOperationException("Out of range") }
//        if (isValidStack(i)) { return }
//        var newIndex = i
//        val (c1, c2) = Pair(2 * i + 1, 2 * i + 2)
//
//
//        if (c1 > c2) {
//            swap(c2, i)
//            newIndex = c2
//        } else {
//            swap(c1, i)
//            newIndex = c1
//        }
//        sink(newIndex, priorityData.size)

        var index = i
        while (index < size) {
            if (isValidStack(index, range)) { return }
            if ((2*index + 2) >= size) {
                swap(2*index + 1, index)
                return
            }
            val (c1, c2) = Pair(2*index + 1, 2*index + 2)
            if (priorityData[c1].second > priorityData[c2].second) {
                swap(c2, index)
                index = c2
            } else {
                swap(c1, index)
                index = c1
            }
        }
    }


    /*
     * And the swim operation as well...
     */
    fun swim(i : Int) {
        if (i < 0 || i > size) { throw UnsupportedOperationException("Out of range") }

        var index = i
        while (index > 0) {
            val parent = (index - 1) / 2
            if (priorityData[parent].second > priorityData[index].second) {
                swap(parent, index)
            } else { return }
            index = (index - 1) / 2
        }
    }


    /*
     * This pops off the data with the lowest priority.  It MUST
     * throw an exception if there is no data left.
     */
    fun pop() : T {
        if (size == 0) {
            throw Exception("Size is 0")
        } else if (size == 1) {
            val retVal = priorityData[0].first
            locationData.remove(retVal)
            priorityData.removeAt(size - 1)
            return retVal
        }
        val retVal = priorityData[0].first
        locationData.remove(retVal)
        priorityData[0] = priorityData[size - 1]
        priorityData.removeAt(size - 1)
        locationData[priorityData[0].first] = 0

        sink(0)
        return retVal
    }

    /*
     * And this function enables updating the priority of something in
     * the queue.  It should sink or swim the element as appropriate to update
     * its new priority.
     *
     * If the key doesn't exist it should create a new one
     */
    fun update(data: T, newPriority: P ) {
        // If updating
        if (data in locationData) {
            val index = locationData[data]!!
            priorityData[index] = Pair(data, newPriority)
            if (isValidStack(index)) {
                swim(index)
            } else { sink(index) }

            // Need to bug check and see if location is being updated
        } else {  // If creating
            val payload = Pair(data, newPriority)
            priorityData.add(payload)
            locationData[data] = size - 1
            swim(size - 1)

            // Should be working?
        }
    }

    /*
     * A convenient shortcut for update, allowing array assignment
     */
    operator fun set(data: T, newPriority: P) {
        update(data, newPriority)
    }


    /*
     * You don't need to implement this function but it is
     * strongly advised that you do so for testing purposes, to check
     * that all invariants are correct.
     */
    fun isValid() : Boolean {
        for (i in 0..<size) {
            if ((2*i + 2) < size)  {
                if (priorityData[i].second > priorityData[2*i+2].second) { return false }
            }
            if ((2*i + 1) < size) {
                if (priorityData[i].second > priorityData[2*i+1].second) { return false }
            }
        }
        return true
    }

    fun isValidStack(i : Int) : Boolean {
        return isValidStack(i, size)
    }

    fun isValidStack(i : Int, range: Int) : Boolean {
        if (i < 0 || i > range) { throw UnsupportedOperationException("Out of range") }
        if ((2*i + 2) < range)  {
            if (priorityData[i].second > priorityData[2*i+2].second) { return false }
        }
        if ((2*i + 1) < range) {
            if (priorityData[i].second > priorityData[2*i+1].second) { return false }
        }
        return true
    }

    class PriorityQueueIterator<T, P : Comparable<P>>(var array : PriorityQueue<T, P>) : AbstractIterator<Pair<T, P>>() {
        var curIndex = 0

        override fun computeNext() {
            if (curIndex < array.size) {
                setNext(array.priorityData[curIndex])
                curIndex++
            } else {
                done()
            }
        }
    }

    operator fun iterator() = PriorityQueueIterator(this)

    override fun toString(): String {
        var counter = 1
        var double = 1
        val out = sequence {
            for (x in this@PriorityQueue) {
                yield(x)
                counter--
                if (counter == 0) {
                    double *= 2
                    counter = double
                    yield(" | ")
                }
            }
        }
        return out.joinToString(prefix = "[", postfix = "]")
    }

    // Give indices i and k
    // Remember to swap both prio and locat
    fun swap(i : Int, k : Int) {
        val tmp = priorityData[i]

        // Swap 1: location data
        locationData[priorityData[i].first] = k
        locationData[priorityData[k].first] = i

        // Swap 2: prio data
        priorityData[i] = priorityData[k]
        priorityData[k] = tmp
    }
}
