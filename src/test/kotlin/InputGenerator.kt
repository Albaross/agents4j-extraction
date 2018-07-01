package org.albaross.agents4j.extraction

import org.albaross.agents4j.extraction.bases.MultiAbstractionLevelKB
import org.albaross.agents4j.extraction.data.Pair
import org.albaross.agents4j.extraction.extractors.MemExtractor
import org.albaross.agents4j.extraction.utils.format
import java.util.*
import java.util.Collections.emptySet
import java.util.function.Supplier

private val RND = Random()

fun <A> generate(count: Int, dimensions: List<String>, actions: List<A>): Collection<Pair<A>> {

    if (count <= 0 || dimensions.isEmpty() || actions.isEmpty())
        return emptySet()

    val range = Math.ceil(Math.pow(10 * count.toDouble(), 1.toDouble() / dimensions.size)).toInt()
    val pairs = TreeSet<Pair<A>>(kotlin.Comparator { o1, o2 -> o1.state.toString().compareTo(o2.state.toString()) })

    while (pairs.size < count) {
        val state = TreeSet<String>()
        for (s in dimensions) {
            state.add("${s}_${RND.nextInt(range)}")
        }

        val action = actions[RND.nextInt(actions.size)]
        pairs.add(Pair(state, action))
    }

    return pairs
}

fun main(args: Array<String>) {
/*    val pairs = generate(5000, listOf("s", "t", "u", "v"), listOf("north", "south", "east", "west"))
    println("generated pairs") */
    val pairs = listOf(Pair(setOf("x1", "y1"), "a"), Pair(setOf("x1", "y2"), "b"))
    val ext = MemExtractor<String>(supplier = Supplier { MultiAbstractionLevelKB<String>() }, minsupp = 0.6)
    val start = System.currentTimeMillis()
    val kb: KnowledgeBase<String> = ext.apply(pairs)
    val end = System.currentTimeMillis()
    println(kb.format())
    println((end - start).toString() + "ms")
}