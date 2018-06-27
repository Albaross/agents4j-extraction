package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.Extractor
import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Multiset
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.data.Rule
import org.albaross.agents4j.extraction.data.Tuple
import org.albaross.agents4j.extraction.utils.div
import org.albaross.agents4j.extraction.utils.times
import java.util.*
import java.util.Collections.emptySet
import java.util.function.Supplier
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2

class PartitionPairsExtractor<A>(private val supplier: Supplier<KnowledgeBase<A>>,
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

            kb.addAll(create(items))
        }

        return kb
    }

    private fun initialize(kb: KnowledgeBase<A>, input: Collection<Pair<A>>): Collection<Tuple<A>> {
        // create rules with empty premise for all actions
        val grouped = input.groupBy { it.action }
        val max = grouped.values.map { it.size }.max()

        // determine the actions with highest number of occurrences
        for ((action, pairs) in grouped) {
            if (pairs.size == max) {
                val conf = pairs / input
                if (conf > minconf)
                    kb.add(Rule(emptySet(), action, conf))
            }
        }

        val map = HashMap<String, Multiset<Pair<A>>>()
        for (pair in input) {
            for (s in pair.state) {
                val supp = map.getOrPut(s) { Multiset() }
                supp.add(pair)
            }
        }

        // create a list of premise-tuples for the collected literals
        val items = ArrayList<Tuple<A>>()
        for ((s, supp) in map) {
            items.add(Tuple(setOf(s), supp, emptyList()))
        }

        return items
    }

    private fun create(items: Collection<Tuple<A>>): Collection<Rule<A>> {
        val rules = ArrayList<Rule<A>>()

        for (mu in items) {
            val supp = mu.pairs
            val grouped = supp.groupBy { it.action }

            for ((action, pairs) in grouped) {
                val conf = pairs / supp
                if (conf > minconf)
                    rules.add(Rule(mu.state, action, conf))
            }
        }

        return rules
    }

    private fun merge(items: Collection<Tuple<A>>, input: Collection<Pair<A>>): Collection<Tuple<A>> {
        val merged = ArrayList<Tuple<A>>()
        val list = when (items) {
            is List -> items
            else -> items.toList()
        }

        // merge pairwise
        for (i in 0 until list.size) {
            for (k in (i + 1) until list.size) {

                val combined = (list[i].state * list[k].state) ?: continue
                val supp = (list[i].pairs * list[k].pairs) ?: continue

                // check for support
                if (minsupp > 0.0 && supp / input <= minsupp)
                    continue

                merged.add(Tuple(combined, supp, emptyList()))
            }
        }

        return merged
    }

}

