package edu.ucdavis.cs.ecs036c

import kotlin.random.Random

/*
 * This is declaring an "Extension Function", basically we are
 * creating a NEW method for Array<T> items.  this will
 * refer to the Array<T> it is called on.
 *
 * We allow an optional comparison function, and this does NOT NEED TO BE
 * a stable sort.
 */
fun <T: Comparable<T>> Array<T>.selectionSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.selectionSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.selectionSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}

fun <T: Comparable<T>> Array<T>.selectionSortWith(comp: Comparator<in T>) : Array<T> {
    for(i in 0..<size){
        var min = this[i]
        var minAt = i
        for(j in i..<size){
            val compare = comp.compare(this[j], min)
            if(compare < 0){
                min = this[j]
                minAt = j
            }
        }
        this[minAt] = this[i]
        this[i] = min
    }
    return this
}

fun <T: Comparable<T>> Array<T>.quickSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.quickSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.quickSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}


/*
 * Here is the QuickSort function you need to implement
 */
fun <T> Array<T>.quickSortWith( comp: Comparator<in T>) : Array<T> {
    fun quicksort_internal(start : Int,end : Int) {
        if (end <= start) { return } // Base case
        var pivot = end // Random.nextInt(start, end) // Should generate random int
        var p1 = start // Swap, slow pointer
        var p2 = start // Examine, fast pointer
        val rand = Random.nextInt(start, end+1)
        var tmp = this[rand] // Pivot number ends up at slow pointer
        this[rand] = this[pivot]
        this[pivot] = tmp
        while (p2 < pivot) {
            val compare = comp.compare(this[p2], this[pivot])
            if ( (compare < 0 ) || (compare == 0 && Random.nextBoolean())) {
                tmp = this[p1]
                this[p1] = this[p2]
                this[p2] = tmp
                p1++
                p2++
            } else {
                p2++
            }
        }
        tmp = this[p1] // Pivot number ends up at slow pointer
        this[p1] = this[pivot]
        this[pivot] = tmp
        quicksort_internal(start, p1 - 1)
        quicksort_internal(p1 + 1, end)
    }
    quicksort_internal(0, this.size - 1)
    return this
}
