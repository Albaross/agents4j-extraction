package org.albaross.agents4j.extraction.data

data class Tuple<A>(val state: Set<String>, val pairs: Collection<Pair<A>>, var rules: Collection<Rule<A>>) {

    override fun toString() = "($state, $pairs, $rules)"

}