package org.albaross.agents4j.extraction.utils

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Rule
import java.util.*

fun <A> KnowledgeBase<A>.format(): String {
    val builder = StringBuilder()

    builder.append("--------------------\n")

    for (level in this) {
        for (rule in level)
            builder.appendRule(rule).append('\n')

        builder.append("--------------------\n")
    }

    builder.append(this.ruleCount).append(" rules\n")
    builder.append("--------------------\n")

    return builder.toString()
}

fun <A> StringBuilder.appendRule(rule: Rule<A>): StringBuilder {
    if (!rule.state.isEmpty()) {
        for ((i, s_i) in rule.state.withIndex()) {
            if (i > 0) this.append(" ^ ")
            this.append(s_i)
        }
    } else {
        this.append('T')
    }

    this.append(" => ")
    this.append(rule.action)
    this.append(" [")
    this.append(String.format(Locale.ENGLISH, "%.3f", rule.weight))
    this.append(']')

    return this
}