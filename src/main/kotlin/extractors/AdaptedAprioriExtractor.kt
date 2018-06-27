package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.Extractor
import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.data.Rule
import org.albaross.agents4j.extraction.utils.div
import org.albaross.agents4j.extraction.utils.times
import java.util.Collections.emptySet
import java.util.function.Supplier
import kotlin.collections.component1
import kotlin.collections.component2

class AdaptedAprioriExtractor<A>(private val supplier: Supplier<KnowledgeBase<A>>,
                                 private val minsupp: Double = 0.0,
                                 private val minconf: Double = 0.0) : Extractor<A> {

    override fun apply(input: Collection<Pair<A>>): KnowledgeBase<A> {
        val kb = supplier.get()
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

    private fun initialize(kb: KnowledgeBase<A>, input: Collection<Pair<A>>): Collection<Collection<String>> {
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

    private fun create(items: Collection<Collection<String>>, input: Collection<Pair<A>>): Collection<Rule<A>> {
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

    private fun merge(items: Collection<Collection<String>>, input: Collection<Pair<A>>): Collection<Collection<String>> {
        val merged = ArrayList<Collection<String>>()
        val list = when (items) {
            is List -> items
            else -> items.toList()
        }

        // merge pairwise
        for (i in 0 until list.size) {
            for (k in (i + 1) until list.size) {

                val combined = (list[i] * list[k]) ?: continue

                val itemSet = when (items) {
                    is Set -> items
                    else -> items.toSet()
                }

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