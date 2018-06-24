package org.albaross.agents4j.extraction.bases

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Rule
import java.util.*

class ModularKnowledgeBase<A> : KnowledgeBase<A> {

    private val base = TreeMap<Int, TreeMap<Double, TreeSet<Rule<A>>>>()
    private var count = 0L

    override val ruleCount: Long
        get() = count

    override val levelCount: Int
        get() = base.keys.firstOrNull() ?: 0

    override fun reason(state: Set<String>): Collection<Rule<A>> {
        for ((_, level) in base) {
            for ((_, module) in level) {
                // find all matching rules in this module
                val active = module.filter { state.containsAll(it.state) }

                // return in case any rules are matching
                if (!active.isEmpty())
                    return active
            }
        }

        return emptyList()
    }

    override fun add(rule: Rule<A>) {
        val dim = rule.state.size
        val conf = rule.weight

        // find matching module
        val level = base.getOrPut(dim) { TreeMap(reverseOrder()) }
        val module = level.getOrPut(conf) { TreeSet() }

        // add rule to module
        if (module.add(rule)) count++
    }

    override fun remove(rule: Rule<A>) {
        val dim = rule.state.size
        val conf = rule.weight

        // find matching module
        val level = base[dim] ?: return
        val module = level[conf] ?: return

        // remove rule from module
        if (module.remove(rule)) count--

        // clean up empty modules and levels
        if (module.isEmpty()) level.remove(conf)
        if (level.isEmpty()) base.remove(dim)
    }

    override fun iterator(): Iterator<Collection<Rule<A>>> = ModularIterator(base.values.iterator())

    override fun clear() {
        base.clear()
        count = 0L
    }

    override fun toString() = base.toString()

}

private class ModularIterator<A>(private val backing: Iterator<Map<*, Collection<Rule<A>>>>) : Iterator<Collection<Rule<A>>> {

    override fun hasNext() = backing.hasNext()

    override fun next() = backing.next().values.flatMap { it.toList() }

}