package org.albaross.agents4j.extraction

import java.util.*

typealias State = Collection<String>

data class Rule<A>(val state: State, val action: A, val weight: Double) {
    override fun toString() = "$state => $action [$weight]"
}

// operators on rules

inline fun <A> Collection<Rule<A>>.weight(or: Double = 0.0) = this.map { it.weight }.max() ?: or

inline infix fun <A> Collection<Rule<A>>.max(other: Collection<Rule<A>>): Collection<Rule<A>> {
    if (this === other) return this
    if (this.weight() > other.weight()) return this
    if (this.weight() < other.weight()) return other
    return this + other
}

// operations on state action pairs

inline val <A> Pair<State, A>.state: State
    get() = this.first

inline val <A> Pair<State, A>.action: A
    get() = this.second

inline operator fun <A> Collection<Pair<State, A>>.div(other: Collection<Pair<State, A>>) = this.size.toDouble() / other.size

// operations on triples

inline infix fun SortedSet<String>.or(other: SortedSet<String>): SortedSet<String>? {

    // states must have the same dimension
    if (this.size != other.size) return null

    val items1 = this.iterator()
    val items2 = other.iterator()

    // check whether symboles 1 to n-1 match
    for (i in 0 until this.size - 1)
        if (items1.next() != items2.next()) return null

    if (items1.next() == items2.next()) return null

    // merge states
    return (this + other).toSortedSet()
}