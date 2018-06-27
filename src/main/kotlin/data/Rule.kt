package org.albaross.agents4j.extraction.data

import java.util.*

data class Rule<A>(val state: Collection<String>, val action: A, val weight: Double) : Comparable<Rule<A>> {

    override fun toString() = "$state => $action [$weight]"

    val signature: String
        get() = "${when (state) {
            is SortedSet -> state
            else -> state.sorted()
        }} => $action"

    override fun compareTo(other: Rule<A>) = this.signature.compareTo(other.signature)

}