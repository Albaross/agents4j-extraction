package org.albaross.agents4j.extraction.bases

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Rule
import java.util.*

/**
 * The class implements a multi-abstraction-level knowledge base.
 *
 * @author Daan Apeldoorn
 * @author Manuel Barbi
 */
class MultiAbstractionLevelKB<A> : KnowledgeBase<A> {

    private val ruleLists = LinkedList<LinkedList<Rule<A>>>()
    private var count = 0L

    override val ruleCount: Long
        get() = count

    override val levelCount: Int
        get() = ruleLists.size

    override fun reason(state: Set<String>): Collection<Rule<A>> {
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

    override fun add(rule: Rule<A>) {
        while (ruleLists.size <= rule.state.size)
            ruleLists.add(LinkedList())

        if (ruleLists[rule.state.size].add(rule)) count++
    }

    override fun remove(rule: Rule<A>) {
        if (ruleLists[rule.state.size].remove(rule)) count--
        while (!ruleLists.isEmpty() && ruleLists.last.isEmpty())
            ruleLists.removeLast()
    }

    fun level(i: Int) = ruleLists[i]

    override fun iterator() = ruleLists.iterator()

    override fun clear() {
        ruleLists.clear()
        count = 0L
    }

    override fun toString() = ruleLists.toString()

}