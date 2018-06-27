package org.albaross.agents4j.extraction.utils

import org.albaross.agents4j.extraction.data.Multiset
import org.albaross.agents4j.extraction.data.Pair

infix operator fun <A> Collection<out Pair<A>>.times(other: Collection<out Pair<A>>): Collection<out Pair<A>>? {
    if (other.size < this.size)
        return other * this

    val intersection = Multiset<Pair<A>>()
    for (item in this) {
        if (item in other)
            intersection.add(item)
    }

    if (intersection.isEmpty())
        return null

    return intersection
}

infix operator fun <A> Collection<out Pair<A>>.div(other: Collection<out Pair<A>>) = this.size.toDouble() / other.size