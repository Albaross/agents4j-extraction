package org.albaross.agents4j.extraction.utils

import org.albaross.agents4j.extraction.data.Tuple


operator fun <A> Tuple<A>.times(other: Tuple<A>): Tuple<A>? {
    val state = (this.state * other.state) ?: return null
    val pairs = (this.pairs * other.pairs) ?: return null
    val rules = this.rules * other.rules

    return Tuple(state, pairs, rules)
}