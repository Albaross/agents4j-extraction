package org.albaross.agents4j.extraction.utils

class Multiset<T> : MutableCollection<T> {

    private val backing = HashMap<T, Int>()

    override val size: Int
        get() = backing.values.sum()

    override fun contains(element: T) = backing.contains(element)

    override fun containsAll(elements: Collection<T>) = backing.keys.containsAll(elements)

    override fun isEmpty() = backing.isEmpty()

    override fun add(element: T): Boolean {
        backing[element] = (backing[element] ?: 0) + 1
        return true
    }

    override fun addAll(elements: Collection<T>) = elements.map { this.add(it) }.any { it }

    override fun clear() = backing.clear()

    override fun iterator(): MutableIterator<T> = TODO("implement later")

    override fun remove(element: T): Boolean {
        val count = backing[element] ?: return false
        if (count > 1) backing[element] = count - 1 else backing.remove(element)
        return true
    }

    override fun removeAll(elements: Collection<T>) = elements.map { this.remove(it) }.any { it }

    override fun retainAll(elements: Collection<T>) = TODO("implement later")

    override fun toString(): String {
        val builder = StringBuilder().append('[')
        builder.append(backing.entries, ", ",
                formatter = { (item, count) -> if (count > 1) "$item x $count" else item.toString() })
        return builder.append(']').toString()
    }

}