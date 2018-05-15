package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.Collection;
import java.util.Set;

public interface Xtractor<A> extends Extractor<A> {

    @Override
    default KnowledgeBase<A> apply(Collection<Pair<A>> input) {
        KnowledgeBase<A> kb = null;
        Set<Set<String>> items = initialize(kb, input);

        while (!input.isEmpty()) {
            kb.addAll(create(items, input));
            items = merge(items, input);
        }

        return kb;
    }

    Set<Set<String>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> input);

    Collection<Rule<A>> create(Set<Set<String>> items, Collection<Pair<A>> input);

    Set<Set<String>> merge(Set<Set<String>> items, Collection<Pair<A>> input);

}
