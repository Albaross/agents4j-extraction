package org.albaross.agents4j.extraction.collections

/**
 * Heavily influenced by MutableCollection
 * but provides default implementations and omits MutableIterator
 */
interface ChangeableCollection<T> : Collection<T> {

    override val size: Int

    override fun isEmpty() = (0 == size)

    override fun contains(element: T): Boolean

    override fun containsAll(elements: Collection<T>) = elements.all { it in this }

    override fun iterator(): Iterator<T>

    fun add(element: T): Boolean

    fun addAll(elements: Collection<T>) = elements.map { this.add(it) }.any { it }

    fun remove(element: T): Boolean

    fun removeAll(elements: Collection<T>) = elements.map { this.remove(it) }.any { it }

    fun clear()
    
}