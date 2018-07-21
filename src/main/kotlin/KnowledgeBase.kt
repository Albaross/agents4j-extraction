package org.albaross.agents4j.extraction

import org.albaross.agents4j.extraction.collections.ChangeableCollection
import org.albaross.agents4j.extraction.utils.append

interface KnowledgeBase<A> : ChangeableCollection<Rule<A>> {

    fun reason(state: State): Collection<Rule<A>>

    val levelCount: Int

    fun level(dim: Int): Collection<Rule<A>>?

    fun levels(): Iterable<Collection<Rule<A>>>

}

val <A> Rule<A>.dim
    get() = this.state.size

fun <A> KnowledgeBase<A>.format(): String {

    val builder = StringBuilder().append("--------------------\n")

    for (level in this.levels()) {
        for ((state, action, weight) in level) {
            builder.append(state, " ^ ", "T").append(" => ").append(action).append(" [").append(weight, 3).append("]\n")
        }

        builder.append("--------------------\n")
    }

    builder.append(this.size).append(" rules\n")
    return builder.append("--------------------\n").toString()
}