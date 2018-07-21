package org.albaross.agents4j.extraction.collections

/**
 * Heavily influenced by MutableCollection
 * but provides default implementations and omits MutableIterator
 */
interface ChangeableCollection<T> : Collection<T> {

    override fun isEmpty() = (0 == size)

    override fun containsAll(elements: Collection<T>) = elements.all { it in this }

    fun add(element: T)

    fun addAll(elements: Collection<T>) = elements.forEach { this.add(it) }

    fun delete(element: T)

    fun deleteAll(elements: Collection<T>) = elements.forEach { this.delete(it) }

    fun clear()

}