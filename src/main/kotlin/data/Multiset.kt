package org.albaross.agents4j.extraction.data

class Multiset<A> : Collection<Pair<A>> {

    private val backing = HashMap<Pair<A>, Int>()
    private var pairCount = 0L

    override val size: Int
        get() = if (pairCount < Int.MAX_VALUE) pairCount.toInt() else Int.MAX_VALUE

    override fun contains(element: Pair<A>) = backing.containsKey(element)

    override fun containsAll(elements: Collection<Pair<A>>) = backing.keys.containsAll(elements)

    override fun isEmpty() = backing.isEmpty()

    fun add(element: Pair<A>) {
        backing[element] = backing.getOrDefault(element, 0) + 1
        pairCount++
    }

    override fun iterator(): Iterator<Pair<A>> = MultisetIterator(backing.entries.iterator())

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

private class MultisetIterator<A>(private val backing: Iterator<Map.Entry<Pair<A>, Int>>) : Iterator<Pair<A>> {

    private var item: Pair<A>? = null
    private var count = 0
    private var indicator = 0

    override fun hasNext() = backing.hasNext() || indicator < count

    override fun next(): Pair<A> {
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

infix fun <A> Collection<Pair<A>>.intersect(other: Collection<Pair<A>>): Collection<Pair<A>> {
    if (other.size < this.size)
        return other intersect this

    val intersection = Multiset<A>()
    for (item in this) {
        if (other.contains(item))
            intersection.add(item)
    }

    return intersection
}