package org.albaross.agents4j.extraction.collections

class NestedIterator<T, U>(private val outer: Iterator<U>, private val iterator: (U) -> Iterator<T>) : Iterator<T> {

    private var inner: Iterator<T>? = null

    override fun hasNext() = outer.hasNext() || inner != null && inner!!.hasNext()

    override fun next(): T {
        while (inner == null || !inner!!.hasNext())
            inner = iterator(outer.next())

        return inner!!.next()
    }

}

class ItemIterator<T>(private val item: Map.Entry<T, Int>) : Iterator<T> {

    private var counter = 0

    override fun hasNext() = counter < item.value

    override fun next(): T {
        counter++
        return item.key
    }

}