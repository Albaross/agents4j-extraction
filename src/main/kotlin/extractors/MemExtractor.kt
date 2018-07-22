package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.*
import org.albaross.agents4j.extraction.bases.TreeMapKB
import org.albaross.agents4j.extraction.collections.Multiset
import java.util.*

typealias Triple<A> = kotlin.Triple<State, Collection<Pair<State, A>>, Collection<Rule<A>>>

class MemExtractor<A>(
        private val new: () -> KnowledgeBase<A> = { TreeMapKB() },
        private val order: Comparator<A>? = null,
        private val minsupp: Double = 0.0,
        private val minconf: Double = 0.0) : Extractor<A> {

    override fun extract(input: Collection<Pair<State, A>>): KnowledgeBase<A> {
        val kb = new()
        if (input.isEmpty())
            return kb

        val initial = create(emptySet(), input)
        kb.addAll(initial)
        var items = initialize(input, initial)

        for (k in 1..input.first().state.size) {
            if (k > 1)
                items = merge(items, input)

            if (items.isEmpty())
                break

            for (i in 0 until items.size) {
                val (state, pairs, rules) = items[i]
                val created = create(state, pairs, rules)

                if (!created.isEmpty()) {
                    items[i] = Triple(state, pairs, created)
                    kb.addAll(created)
                }
            }
        }

        return kb
    }

    private fun create(state: State, supporting: Collection<Pair<State, A>>,
                       existing: Collection<Rule<A>> = emptyList()): Collection<Rule<A>> {

        val grouped = supporting.groupBy { it.action }
        val maxCount = grouped.values.map { it.size }.max()
        val created = ArrayList<Rule<A>>()

        for ((action, pairs) in grouped) {
            if (maxCount == pairs.size) {
                val conf = pairs / supporting

                if (conf <= existing.weight(or = minconf)) continue
                if (!existing.isEmpty() && existing.all { action == it.action }) continue

                created.add(Rule(state, action, conf))
            }
        }

        if (order != null && created.size > 1) {
            val minAction = created.map { it.action }.minWith(order)
            return created.filter { minAction == it.action }
        }

        return created
    }

    private fun initialize(input: Collection<Pair<State, A>>, initial: Collection<Rule<A>>): MutableList<Triple<A>> {
        val partitions = HashMap<String, Multiset<Pair<State, A>>>()
        for (pair in input) {
            for (s in pair.state) {
                val supporting = partitions.getOrPut(s) { Multiset() }
                supporting.add(pair)
            }
        }

        val items = ArrayList<Triple<A>>()
        for ((s, supporting) in partitions) {
            if (minsupp > 0.0 && supporting / input <= minsupp) continue

            items.add(Triple(setOf(s), supporting, initial))
        }

        return items
    }

    private fun merge(items: Collection<Triple<A>>, input: Collection<Pair<State, A>>): MutableList<Triple<A>> {
        val merged = ArrayList<Triple<A>>()
        val list = items as List

        for (i in 0 until list.size) {
            for (k in (i + 1) until list.size) {
                val (state_i, pairs_i, rules_i) = list[i]
                val (state_k, pairs_k, rules_k) = list[k]

                val state = (state_i as SortedSet or state_k as SortedSet) ?: continue
                val pairs = (pairs_i as Multiset and pairs_k as Multiset) ?: continue
                if (minsupp > 0.0 && pairs / input <= minsupp) continue
                val rules = rules_i max rules_k

                merged.add(Triple(state, pairs, rules))
            }
        }

        return merged
    }

}