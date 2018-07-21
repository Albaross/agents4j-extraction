package org.albaross.agents4j.extraction.collections

import org.albaross.agents4j.extraction.utils.append
import kotlin.math.min


class Multiset<T> : ChangeableCollection<T> {

    private val backing = HashMap<T, Int>()

    override val size: Int
        get() = backing.values.sum()

    override fun contains(element: T) = backing.contains(element)

    override fun iterator(): Iterator<T> = NestedIterator(backing.entries.iterator()) { ItemIterator(it) }

    override fun add(element: T) {
        backing[element] = (backing[element] ?: 0) + 1
    }

    override fun delete(element: T) {
        val count = backing[element] ?: return
        if (count > 1) backing[element] = count - 1 else backing.remove(element)
    }

    override fun clear() = backing.clear()

    override fun toString() = StringBuilder().append('[')
            .append(backing.entries, ", ",
                    formatter = { (item, count) -> if (count > 1) "$item x $count" else item.toString() })
            .append(']').toString()

    infix fun and(other: Multiset<T>): Multiset<T>? {

        if (this.backing.size > other.backing.size)
            return other and this

        val intersection = Multiset<T>()
        for ((item, count) in this.backing) {
            val min = min(count, other.backing[item] ?: 0)
            if (min > 0) intersection.backing[item] = min
        }

        return intersection
    }

}