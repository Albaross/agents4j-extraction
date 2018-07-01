package org.albaross.agents4j.extraction.filters

import org.albaross.agents4j.extraction.KnowledgeBase
import org.albaross.agents4j.extraction.data.Pair
import java.util.function.BiConsumer

class RemoveUnusedFilter<A>(pairs: Collection<Pair<A>>) : BiConsumer<KnowledgeBase<A>, Collection<Pair<A>>> {

    override fun accept(kb: KnowledgeBase<A>, pairs: Collection<Pair<A>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}