package org.albaross.agents4j.extraction.data

data class Pair<A>(val state: Collection<String>, val action: A) {

    override fun toString() = "($state : $action)"

}