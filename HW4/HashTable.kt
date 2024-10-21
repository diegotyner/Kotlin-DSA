package edu.ucdavis.cs.ecs036c

import kotlin.math.absoluteValue


class HashTable<K, V>(var initialCapacity: Int = 8) {
    data class HashTableEntry<K, V>(val key: K, var value: V, var deleted : Boolean = false);
    // The number of elements in the storage that exist, whether or not they are marked deleted
    internal var occupied = 0

    // The number of non-deleted elements.
    internal var privateSize = 0

    // And the internal storage array
    internal var storage: Array<HashTableEntry<K, V>?> = arrayOfNulls(initialCapacity)

    val size: Int
        get() = privateSize


    // An iterator of key/value pairs, done by using a sequence and calling yield
    // on each pair that is in the table and VALID
    operator fun iterator() : Iterator<Pair<K, V>> =
        sequence<Pair<K, V>> {
            for (i in 0..<storage.size) {
                if (storage[i] != null && !storage[i]!!.deleted) {
                    yield(Pair(storage[i]!!.key, storage[i]!!.value))
                }
            }
    }.iterator()

    override fun toString() : String = this.iterator().asSequence().joinToString(prefix="{", postfix="}",
        limit = 200) { "[${it.first}/${it.second}]" }


    // Internal resize function.  It should copy all the
    // valid entries but ignore the deleted entries.
    private fun resize(){
        val newStor: Array<HashTableEntry<K, V>?> = arrayOfNulls(storage.size*2)
        for (x in this) {
            val (newKey, newVal) = x
            val crypt = (newKey.hashCode().absoluteValue) % newStor.size
            var i = 0
            while (newStor[(crypt + i + newStor.size) % newStor.size] != null) { i++ } // Find open spot
            newStor[(crypt + i + newStor.size) % newStor.size] = HashTableEntry(newKey, newVal)
        }
        occupied = privateSize
        storage = newStor
    }

    operator fun contains(key: K): Boolean {
        val crypt = (key.hashCode().absoluteValue) % storage.size
        var i = 0
        var cur = storage[crypt]
        while (cur != null) {
            if (!cur.deleted && cur.key == key) {
                return true
            }
            i++
            cur = storage[(crypt + i + storage.size) % storage.size]
        }
        return false
    }

    // Get returns null if the key doesn't exist
    operator fun get(key: K): V? {
        if (key !in this) {
            return null
        } else {
            // find val
            val crypt = (key.hashCode().absoluteValue) % storage.size
            var i = 0
            var cur = storage[crypt]
            while (cur != null) {
                if (!cur.deleted && cur.key == key) {
                    return cur.value
                }
                i++
                cur = storage[(crypt + i + storage.size) % storage.size]
            }
            throw UnsupportedOperationException("Unreachable Code, should have found it")
        }
    }

    // IF the key exists just update the corresponding data.
    // If the key doesn't exist, find a spot to insert it.
    // If you need to insert into a NEW entry, resize if
    // the occupancy (active & deleted entries) is >75%
    operator fun set(key: K, value: V) {
        var crypt = (key.hashCode().absoluteValue) % storage.size
        var i = 0
        var cur = storage[crypt]
        if (key in this) {
            while (cur != null) {
                if (!cur.deleted && cur.key == key) {
                    cur.value = value
                    return
                }
                i++
                cur = storage[(crypt + i + storage.size) % storage.size]
            }
            throw UnsupportedOperationException("Unreachable Code, should have been set")
        } else {
            if (occupied >= 0.75*storage.size) { // If resize necessary
                resize()
                crypt = (key.hashCode().absoluteValue) % storage.size
                cur = storage[crypt]
            }

            // Get to open slot
            while (cur != null && !cur.deleted) {
                i++
                cur = storage[(crypt + i + storage.size) % storage.size]
            }

            // Check whether open is null, or deleted
            if (cur == null) {
                storage[(crypt + i + storage.size) % storage.size] = HashTableEntry(key,value)
                occupied++
            } else {
                storage[(crypt + i + storage.size) % storage.size] = HashTableEntry(key,value)
            }
            privateSize++
            return
        }
    }

    // If the key doesn't exist remove does nothing
    fun remove(key: K) {
        if (key !in this) {
            return
        } else {
            val crypt = (key.hashCode().absoluteValue) % storage.size
            var i = 0
            var cur = storage[crypt]
            while (cur != null) {
                if (!cur.deleted && cur.key == key) {
                    cur.deleted = true
                    privateSize--
                    return
                }
                i++
                cur = storage[(crypt + i + storage.size) % storage.size]
            }
            throw UnsupportedOperationException("Unreachable Code, should have found it")
        }
    }

}