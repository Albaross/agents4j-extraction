package org.albaross.agents4j.extraction.extractors

import org.albaross.agents4j.extraction.Extractor
import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Multiset
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.data.Rule
import org.albaross.agents4j.extraction.data.Tuple
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

        for (k in 1 until n) {
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
                val conf = pairs.size.toDouble() / input.size
                if (conf > minconf)
                    kb.add(Rule(emptySet(), action, conf))
            }
        }

        val map = HashMap<String, Multiset<A>>()
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
                val conf = pairs.size.toDouble() / supp.size
                if (conf > minconf)
                    rules.add(Rule(mu.state, action, conf))
            }
        }

        return rules
    }

    private fun merge(items: Collection<Tuple<A>>, input: Collection<Pair<A>>): Collection<Tuple<A>> {
        val merged = ArrayList<Tuple<A>>()
        val list = ArrayList(items)

        // merge pairwise
        for (i in 1 until list.size) {
            for (k in (i + 1) until list.size) {
                mergeItems(list[i], list[k], input)?.let { merged.add(it) }
            }
        }

        return merged
    }

    private fun mergeItems(item1: Tuple<A>, item2: Tuple<A>, input: Collection<Pair<A>>): Tuple<A>? {

        if (item1.state.size != item2.state.size)
            return null

        // check whether symbols 1 to n-1 matches
        val n = item1.state.size
        val it1 = item1.state.iterator()
        val it2 = item2.state.iterator()

        for (i in 0 until n - 1) {
            if (it1.next() != it2.next())
                return null
        }

        if (it1.next() == it2.next())
            return null

        // merge states
        val union = TreeSet<String>()
        union.addAll(item1.state)
        union.addAll(item2.state)

        // check for support
        val supp = item1.pairs intersect item2.pairs
        if (supp.isEmpty() || supp.size.toDouble() / input.size <= minsupp)
            return null

        return Tuple(union, supp, emptyList())
    }

}

infix fun <A> Collection<Pair<A>>.intersect(other: Collection<Pair<A>>): Collection<Pair<A>> {
    if (other.size < this.size)
        return other intersect this

    val intersection = Multiset<A>()
    for (item in this) {
        if (other.contains(item))
            intersection.add(item)
    }

    return intersection
}