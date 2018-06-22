package org.albaross.agents4j.extraction.bases

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Rule
import org.albaross.agents4j.extraction.data.appendState
import java.util.*

/**
 * The class implements a multi-abstraction-level knowledge base.
 *
 * @author Daan Apeldoorn
 * @author Manuel Barbi
 */
class MultiAbstractionLevelKB<A> : KnowledgeBase<A> {

    private val ruleLists = LinkedList<LinkedList<Rule<A>>>()
    private var ruleCount = 0L

    override fun ruleCount() = ruleCount

    override fun reason(state: Set<String>): Collection<Rule<A>> {
        val potentialFiringRules = LinkedList<Rule<A>>()
        var i = levelCount() - 1
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
        while (ruleLists.size <= rule.state.size) {
            ruleLists.add(LinkedList())
        }
        ruleLists[rule.state.size].add(rule)
    }

    override fun remove(rule: Rule<A>) {
        ruleLists[rule.state.size].remove(rule)
        while (!ruleLists.isEmpty() && ruleLists.last.isEmpty()) {
            ruleLists.removeLast()
        }
    }

    override fun levelCount() = ruleLists.size

    override fun iterator() = ruleLists.iterator()

    override fun clear() = ruleLists.clear()

    override fun toString(): String {
        val result = StringBuilder()
        for (list in ruleLists) {
            for (rule in list) {
                if (!rule.state.isEmpty()) {
                    result.appendState(rule.state)
                            .append(" => ")
                }

                result.append(rule.action)
                        .append(" [")
                        .append(rule.weight)
                        .append(']')
            }
            result.append('\n')
        }

        return result.toString()
    }
}