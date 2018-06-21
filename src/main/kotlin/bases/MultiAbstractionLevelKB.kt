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

    private val ruleLists = ArrayList<ArrayList<Rule<A>>>()
    private var ruleCount = 0L

    override fun ruleCount() = ruleCount

    override fun reason(state: Set<String>): Collection<Rule<A>> {
        val potentialFiringRules = ArrayList<Rule<A>>()
        var i = levelCount() - 1

        var maxWeight = Double.NEGATIVE_INFINITY
        while (potentialFiringRules.isEmpty() && (i >= 0)) {
            for (rule in ruleLists[i]) {
                if (state.containsAll(rule.state)) {
                    potentialFiringRules.add(rule)

                    if (maxWeight < rule.confidence)
                        maxWeight = rule.confidence
                }
            }

            i--
        }

        val firingRules = ArrayList<Rule<A>>(potentialFiringRules.size)

        for (rule in potentialFiringRules) {
            if (maxWeight == rule.confidence)
                firingRules.add(rule)
        }

        return firingRules
    }

    override fun add(rule: Rule<A>) {
        while (ruleLists.size < rule.state.size + 1) {
            ruleLists.add(ArrayList())
        }
        ruleLists[rule.state.size].add(rule)
    }

    override fun remove(rule: Rule<A>) {
        ruleLists[rule.state.size].remove(rule)
        while (ruleLists[ruleLists.size - 1].isEmpty()) {
            ruleLists.removeAt(ruleLists.size - 1)
        }
    }

    override fun levelCount() = ruleLists.size

    override fun iterator() = ruleLists.iterator()

    override fun clear() = ruleLists.clear()

    override fun toString(): String {
        val builder = StringBuilder()
        for (list in ruleLists) {
            for (rule in list) {
                builder.append(rule).append('\n')
            }
            builder.append('\n')
        }
        return builder.toString()
    }
}