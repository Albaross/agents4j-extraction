package org.albaross.agents4j.extraction.utils

import org.albaross.agents4j.extraction.collections.ChangeableCollection

class Multiset<T> : ChangeableCollection<T> {

    private val backing = HashMap<T, Int>()

    override val size: Int
        get() = backing.values.sum()

    override fun contains(element: T) = backing.contains(element)

    override fun iterator(): Iterator<T> = NestedIterator(backing.entries.iterator()) { ItemIterator(it) }

    override fun add(element: T): Boolean {
        backing[element] = (backing[element] ?: 0) + 1
        return true
    }

    override fun remove(element: T): Boolean {
        val count = backing[element] ?: return false
        if (count > 1) backing[element] = count - 1 else backing.remove(element)
        return true
    }

    override fun clear() = backing.clear()

    override fun toString(): String {
        val builder = StringBuilder().append('[')
        builder.append(backing.entries, ", ",
                formatter = { (item, count) -> if (count > 1) "$item x $count" else item.toString() })
        return builder.append(']').toString()
    }

}