package org.albaross.agents4j.extraction

interface KnowledgeBase<A> : Collection<Rule<A>> {

    override fun isEmpty() = (0 == size)

    override fun containsAll(rules: Collection<Rule<A>>) = rules.all { rule -> rule in this }

    fun reason(state: State): Collection<Rule<A>>
}

interface MutableKnowledgeBase<A> : KnowledgeBase<A>, MutableCollection<Rule<A>> {

    override fun addAll(rules: Collection<Rule<A>>) = rules.map { rule -> this.add(rule) }.any { it }

    override fun removeAll(rules: Collection<Rule<A>>) = rules.map { rule -> this.remove(rule) }.any { it }

    override fun retainAll(rules: Collection<Rule<A>>) = this.removeIf { rule -> rule !in rules }
}

interface HierarchicalKnowledgeBase<A> : KnowledgeBase<A> {

    val levelCount: Int

    fun level(dim: Int): Collection<Rule<A>>

    fun levels(): Iterable<Collection<Rule<A>>>
}

fun <A> HierarchicalKnowledgeBase<A>.format(): String {

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