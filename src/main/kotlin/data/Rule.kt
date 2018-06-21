package org.albaross.agents4j.extraction.data

data class Rule<A>(val state: Set<String>, val action: A, val confidence: Double) {

    override fun toString() = "${state.asConjunction()} => action [${"%.3f".format(confidence)}]"

}

fun Set<String>.asConjunction(): String {

    if (this.isEmpty())
        return "T"

    val builder = StringBuilder()

    for ((i, s_i) in this.withIndex()) {
        if (i > 0) builder.append(" ^ ")

        builder.append(s_i)
    }

    return builder.toString()
}