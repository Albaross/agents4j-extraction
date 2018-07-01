package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.Extractor
import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.bases.MultiAbstractionLevelKB
import org.albaross.agents4j.extraction.data.Multiset
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.data.Rule
import org.albaross.agents4j.extraction.data.Tuple
import org.albaross.agents4j.extraction.utils.div
import org.albaross.agents4j.extraction.utils.times
import org.albaross.agents4j.extraction.utils.weight
import java.util.*
import java.util.Collections.emptySet
import kotlin.collections.ArrayList
import kotlin.collections.component1
import kotlin.collections.component2

class MemExtractor<A>(val minsupp: Double = 0.0, val minconf: Double = 0.0) : Extractor<A> {

    override fun apply(input: Collection<Pair<A>>): KnowledgeBase<A> {
        val kb = MultiAbstractionLevelKB<A>()
        if (input.isEmpty())
            return kb

        var items = initialize(kb, input)
        val n = input.first().state.size

        for (k in 1..n) {
            if (k > 1)
                items = merge(items, input)

            if (items.isEmpty())
                break

            for (mu in items) {
                val rules = create(mu.state, mu.pairs, mu.rules)

                if (rules.isEmpty()) {
                    mu.rules = rules
                    kb.addAll(rules)
                }
            }
        }

        return kb
    }

    private fun initialize(kb: KnowledgeBase<A>, input: Collection<Pair<A>>): Collection<Tuple<A>> {
        // create rules with empty premise for all actions
        val rules = create(emptySet(), input)
        kb.addAll(rules)

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
            // check for support
            if (minsupp > 0.0 && supp / input <= minsupp)
                continue

            items.add(Tuple(setOf(s), supp, rules))
        }

        return items
    }

    private fun create(state: Collection<String>, supp: Collection<Pair<A>>,
                       existing: Collection<Rule<A>> = emptyList()): Collection<Rule<A>> {

        val grouped = supp.groupBy { it.action }
        val max = grouped.values.map { it.size }.max()
        val created = ArrayList<Rule<A>>()

        for ((action, pairs) in grouped) {
            if (pairs.size == max) {
                val conf = pairs / supp

                if (conf <= existing.weight(minconf)) continue
                if (!existing.isEmpty() && existing.all { it.action == action }) continue

                created.add(Rule(state, action, conf))
            }
        }

        return created
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

                val combined = (list[i] * list[k]) ?: continue

                // check for support
                if (minsupp > 0.0 && combined.pairs / input <= minsupp)
                    continue

                merged.add(combined)
            }
        }

        return merged
    }

}