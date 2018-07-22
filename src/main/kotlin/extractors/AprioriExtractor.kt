package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.*
import org.albaross.agents4j.extraction.bases.MultiAbstractionLevelKB
import java.util.*

class AprioriExtractor<A>(
        private val new: () -> KnowledgeBase<A> = { MultiAbstractionLevelKB() },
        private val minsupp: Double = 0.0,
        private val minconf: Double = 0.0) : Extractor<A> {

    override fun extract(input: Collection<Pair<State, A>>): KnowledgeBase<A> {
        val kb = new()
        if (input.isEmpty())
            return kb

        var items = initialize(kb, input)
        val n = input.first().state.size

        for (k in 1..n) {
            if (k > 1)
                items = merge(items, input)

            if (items.isEmpty())
                break

            kb.addAll(create(items, input))
        }

        return kb
    }

    private fun initialize(kb: KnowledgeBase<A>, input: Collection<Pair<State, A>>): List<State> {
        // create rules with empty premise for all actions
        val grouped = input.groupBy { it.action }

        for ((action, pairs) in grouped) {
            val conf = pairs / input
            if (conf > minconf)
                kb.add(Rule(emptySet(), action, conf))
        }

        // collect all literals
        return input.flatMap { it.state.toList() }
                .distinct()
                .map { setOf(it) }
    }

    private fun create(items: Collection<State>, input: Collection<Pair<State, A>>): Collection<Rule<A>> {
        val rules = ArrayList<Rule<A>>()

        for (item in items) {
            val supp = input.filter { it.state.containsAll(item) }
            val grouped = supp.groupBy { it.action }

            for ((action, pairs) in grouped) {
                val conf = pairs / supp
                if (conf > minconf)
                    rules.add(Rule(item, action, conf))
            }
        }

        return rules
    }

    private fun merge(items: List<State>, input: Collection<Pair<State, A>>): List<State> {
        val merged = ArrayList<State>()
        val itemSet = items.toSet()

        // merge pairwise
        for (i in 0 until items.size) {
            for (k in (i + 1) until items.size) {

                val combined = (items[i] as SortedSet or items[k] as SortedSet) ?: continue

                // subset check
                for (s in combined) {
                    val subset = combined.toMutableSet()
                    subset.remove(s)

                    if (subset !in itemSet)
                        continue
                }

                // check for support
                if (minsupp == 0.0) {
                    // shortcut
                    if (input.none { it.state.containsAll(combined) })
                        continue
                } else {
                    val supp = input.filter { it.state.containsAll(combined) }
                    if (supp.isEmpty() || supp / input <= minsupp)
                        continue
                }

                // provide merged state
                merged.add(combined)
            }
        }

        return merged
    }
}