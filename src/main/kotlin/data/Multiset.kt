package org.albaross.agents4j.extraction.data

class Multiset<T> : Collection<T> {

    private val backing = HashMap<T, Int>()
    private var count = 0L

    override val size: Int
        get() = if (count < Int.MAX_VALUE) count.toInt() else Int.MAX_VALUE

    override fun contains(element: T) = backing.containsKey(element)

    override fun containsAll(elements: Collection<T>) = backing.keys.containsAll(elements)

    override fun isEmpty() = backing.isEmpty()

    fun add(element: T) {
        backing[element] = (backing[element] ?: 0) + 1
        count++
    }

    override fun iterator(): Iterator<T> = MultisetIterator(backing.entries.iterator())

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append('[')

        var i = 0
        for ((item, count) in backing) {
            if (i > 0) builder.append(", ")
            builder.append(item)
            if (count > 1) builder.append(" x ").append(count)
            i++
        }

        builder.append(']')
        return builder.toString()
    }
}

private class MultisetIterator<T>(private val backing: Iterator<Map.Entry<T, Int>>) : Iterator<T> {

    private var item: T? = null
    private var count = 0
    private var indicator = 0

    override fun hasNext() = backing.hasNext() || indicator < count

    override fun next(): T {
        if (indicator >= count) {
            val entry = backing.next()
            item = entry.key
            count = entry.value
            indicator = 0
        }

        indicator++
        return item!!
    }
}