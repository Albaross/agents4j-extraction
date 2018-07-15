package org.albaross.agents4j.extraction.collections

/**
 * Heavily influenced by MutableCollection
 * but provides default implementations and omits MutableIterator
 */
interface ChangeableCollection<T> : Collection<T> {

    override fun isEmpty() = (0 == size)

    override fun containsAll(elements: Collection<T>) = elements.all { it in this }

    fun add(element: T): Boolean

    fun addAll(elements: Collection<T>) = elements.map { this.add(it) }.any { it }

    fun clear()

    fun remove(element: T): Boolean

    fun removeAll(elements: Collection<T>) = elements.map { this.remove(it) }.any { it }

}