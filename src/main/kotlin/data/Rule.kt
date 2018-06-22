package org.albaross.agents4j.extraction.data

data class Rule<A>(val state: Set<String>, val action: A, val weight: Double) {

    override fun toString() = "${state.asConjunction()} => action [${"%.3f".format(weight)}]"

}

fun Set<String>.asConjunction(): String {
    if (this.isEmpty())
        return "T"

    return StringBuilder().appendState(this).toString()
}

fun StringBuilder.appendState(state: Collection<String>): StringBuilder {
    for ((i, s_i) in state.withIndex()) {
        if (i > 0) this.append(" ^ ")

        this.append(s_i)
    }
    return this
}