package org.albaross.agents4j.extraction.collections

import org.albaross.agents4j.extraction.HierarchicalKnowledgeBase
import org.albaross.agents4j.extraction.Rule
import org.albaross.agents4j.extraction.State
import org.albaross.agents4j.extraction.dim
import org.albaross.agents4j.extraction.utils.NestedIterator
import java.util.*

class MultiLevelKB<A> : HierarchicalKnowledgeBase<A>, ChangeableCollection<Rule<A>> {

    private val backing = TreeMap<Int, ArrayList<Rule<A>>>()

    override val size: Int
        get() = backing.values.map { it.size }.sum()

    override fun contains(rule: Rule<A>) = backing[rule.dim]?.contains(rule) ?: false

    override fun iterator(): Iterator<Rule<A>> = NestedIterator(backing.values.iterator()) { it.iterator() }

    override fun add(rule: Rule<A>) = backing.getOrPut(rule.dim) { ArrayList() }.add(rule)

    override fun remove(rule: Rule<A>): Boolean {
        val dim = rule.dim
        val level = backing[dim] ?: return false
        if (!level.remove(rule)) return false
        if (level.isEmpty()) backing.remove(dim)
        return true
    }

    override fun clear() = backing.clear()

    override fun reason(state: State): Collection<Rule<A>> {
        for ((_, level) in backing.descendingMap()) {
            val sorted = level.sortedByDescending { it.weight }
            val active = ArrayList<Rule<A>>()
            var maxWeight = 0.0

            for (rule in sorted) {
                if (rule.weight < maxWeight) break

                if (rule.state.containsAll(state)) {
                    maxWeight = rule.weight
                    active.add(rule)
                }
            }

            if (!active.isEmpty()) return active
        }

        return emptyList()
    }

    override val levelCount: Int
        get() = backing.keys.lastOrNull() ?: 0

    override fun level(dim: Int): Collection<Rule<A>> = backing[dim]!!

    override fun levels(): Iterable<Collection<Rule<A>>> = backing.values

}