package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.Extractor
import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.data.Rule
import java.util.Collections.emptySet
import java.util.HashSet
import java.util.TreeSet
import java.util.function.Supplier
import kotlin.collections.ArrayList
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

    private fun initialize(kb: KnowledgeBase<A>, input: Collection<Pair<A>>): Collection<Set<String>> {
        // create rules with empty premise for all actions
        val grouped = input.groupBy { it.action }

        for ((action, pairs) in grouped) {
            val conf = pairs.size.toDouble() / input.size
            if (conf > minconf)
                kb.add(Rule(emptySet(), action, conf))
        }

        // collect all literals
        return input.flatMap { it.state.toList() }
                .distinct()
                .map { setOf(it) }
    }

    private fun create(items: Collection<Set<String>>, input: Collection<Pair<A>>): Collection<Rule<A>> {
        val rules = ArrayList<Rule<A>>()

        for (item in items) {
            val supp = input.filter { it.state.containsAll(item) }
            val grouped = supp.groupBy { it.action }

            for ((action, pairs) in grouped) {
                val conf = pairs.size.toDouble() / supp.size
                if (conf > minconf)
                    rules.add(Rule(item, action, conf))
            }
        }

        return rules
    }

    private fun merge(items: Collection<Set<String>>, input: Collection<Pair<A>>): Collection<Set<String>> {
        val merged = ArrayList<Set<String>>()
        val list = ArrayList(items)

        // merge pairwise
        for (i in 0 until list.size) {
            for (k in (i + 1) until list.size) {
                mergeItems(list[i], list[k], items, input)?.let { merged.add(it) }
            }
        }

        return merged
    }

    private fun mergeItems(item1: Set<String>, item2: Set<String>,
                           items: Collection<Set<String>>, input: Collection<Pair<A>>): Set<String>? {

        if (item1.size != item2.size)
            return null

        // check whether symbols 1 to n-1 matches
        val n = item1.size
        val it1 = item1.iterator()
        val it2 = item2.iterator()

        for (i in 0 until n - 1) {
            if (it1.next() != it2.next())
                return null
        }

        if (it1.next() == it2.next())
            return null

        // merge states
        val merged = TreeSet<String>()
        merged.addAll(item1)
        merged.addAll(item2)

        // subset check
        for (s in merged) {
            val subset = HashSet(merged)
            subset.remove(s)

            if (!items.contains(subset))
                return null
        }

        // check for support
        val supp = input.filter { it.state.containsAll(merged) }
        if (supp.isEmpty() || supp.size.toDouble() / input.size <= minsupp)
            return null

        // provide merged state
        return merged
    }

}