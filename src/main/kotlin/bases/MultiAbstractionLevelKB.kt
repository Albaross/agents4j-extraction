package org.albaross.agents4j.extraction.bases

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.Rule
import org.albaross.agents4j.extraction.State
import org.albaross.agents4j.extraction.collections.NestedIterator
import org.albaross.agents4j.extraction.dim
import java.util.*

/*
 * The class implements a multi-abstraction-level knowledge base.
 *
 * @author Daan Apeldoorn
 * @author Manuel Barbi
 */
class MultiAbstractionLevelKB<A> : KnowledgeBase<A> {

    private val ruleLists = LinkedList<LinkedList<Rule<A>>>()

    override val size: Int
        get() = ruleLists.map { it.size }.sum()

    override fun contains(rule: Rule<A>) = rule.dim < ruleLists.size && ruleLists[rule.dim].contains(rule)

    override fun iterator() = NestedIterator(ruleLists.iterator()) { it.iterator() }

    override fun add(rule: Rule<A>) {
        while (ruleLists.size <= rule.state.size)
            ruleLists.add(LinkedList())

        ruleLists[rule.state.size].add(rule)
    }

    override fun delete(rule: Rule<A>) {
        ruleLists[rule.state.size].remove(rule)
        while (!ruleLists.isEmpty() && ruleLists.last.isEmpty())
            ruleLists.removeLast()
    }

    override fun clear() = ruleLists.clear()

    override fun reason(state: State): Collection<Rule<A>> {
        val potentialFiringRules = LinkedList<Rule<A>>()
        var i = ruleLists.size - 1
        var maxWeight = Double.NEGATIVE_INFINITY

        while (potentialFiringRules.isEmpty() && i >= 0) {
            for (rule in ruleLists[i]) {
                if (state.containsAll(rule.state)) {
                    potentialFiringRules.add(rule)

                    if (maxWeight < rule.weight)
                        maxWeight = rule.weight
                }
            }
            i--
        }

        val conclusions = LinkedList<Rule<A>>()
        for (rule in potentialFiringRules) {
            if (rule.weight == maxWeight)
                conclusions.add(rule)
        }

        return conclusions
    }

    override val levelCount: Int
        get() = ruleLists.size

    override fun level(dim: Int) = if (dim < ruleLists.size) ruleLists[dim] else null

    override fun levels() = ruleLists

    override fun toString() = ruleLists.toString()

}