package org.albaross.agents4j.extraction

interface Extractor<A> {

    fun extract(input: Collection<Pair<State, A>>): KnowledgeBase<A>

}