package org.albaross.agents4j.extraction.data

data class Rule<A>(val state: Set<String>, val action: A, val weight: Double) {

    override fun toString() = "$state => $action"

}