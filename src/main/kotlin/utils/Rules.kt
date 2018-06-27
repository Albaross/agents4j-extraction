package org.albaross.agents4j.extraction.utils

import org.albaross.agents4j.extraction.data.Rule

operator fun <A> Collection<Rule<A>>.times(other: Collection<Rule<A>>): Collection<Rule<A>> {
    if (this === other)
        return this

    if (this.weight > other.weight)
        return this

    if (this.weight < other.weight)
        return other

    return this + other
}

val <A> Collection<Rule<A>>.weight
    get() = this.firstOrNull()?.weight ?: 0.0