package org.albaross.agents4j.extraction.utils

import java.util.*

inline operator fun Collection<String>.times(other: Collection<String>): Collection<String>? {

    // states must have the same size
    if (this.size != other.size)
        return null

    val n = this.size

    val items1 = when (this) {
        is SortedSet -> this
        else -> this.sorted()
    }.iterator()

    val items2 = when (other) {
        is SortedSet -> other
        else -> other.sorted()
    }.iterator()

    // check whether symbols 1 to n-1 matches
    for (i in 0 until n - 1) {
        if (items1.next() != items2.next())
            return null
    }

    if (items1.next() == items2.next())
        return null

    // merge states
    return (this + other).toSortedSet()
}