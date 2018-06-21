package org.albaross.agents4j.extraction

import org.albaross.agents4j.extraction.data.Rule
import java.util.stream.StreamSupport

interface KnowledgeBase<A> : Iterable<Collection<Rule<A>>> {

    fun ruleCount(): Long

    fun isEmpty() = (0L == ruleCount())

    fun reason(state: Set<String>): Collection<Rule<A>>

    fun actions(state: Set<String>) = reason(state).map { it.action }

    fun add(rule: Rule<A>)

    fun remove(rule: Rule<A>)

    fun addAll(rules: Collection<Rule<A>>) = rules.forEach { add(it) }

    fun removeAll(rules: Collection<Rule<A>>) = rules.forEach { remove(it) }

    fun levelCount(): Int

    fun stream() = StreamSupport.stream(this.spliterator(), false)

    fun clear()

}