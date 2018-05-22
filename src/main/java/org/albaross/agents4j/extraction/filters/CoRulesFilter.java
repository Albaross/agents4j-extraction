package org.albaross.agents4j.extraction.filters;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Pair;

import java.util.Collection;
import java.util.function.BiConsumer;

public class CoRulesFilter<A> implements BiConsumer<KnowledgeBase<A>, Collection<Pair<A>>> {
    
    @Override
    public void accept(KnowledgeBase<A> kb, Collection<Pair<A>> pairs) {

    }
}
