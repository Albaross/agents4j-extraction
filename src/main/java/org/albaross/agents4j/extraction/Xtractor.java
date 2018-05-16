package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public interface Xtractor<A> extends Extractor<A> {

    @Override
    default KnowledgeBase<A> apply(Collection<Pair<A>> input) {
        KnowledgeBase<A> kb = new HierarchicalKnowledgeBase<>();
        if (input.isEmpty())
            return kb;

        Set<Set<String>> items = initialize(kb, input);
        int k = 1, n = input.iterator().next().getState().size();

        while (!items.isEmpty()) {
            kb.addAll(create(items, input));
            items = (k < n) ? merge(items, input) : Collections.emptySet();
            k++;
        }

        return kb;
    }

    Set<Set<String>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> input);

    Collection<Rule<A>> create(Set<Set<String>> items, Collection<Pair<A>> input);

    Set<Set<String>> merge(Set<Set<String>> items, Collection<Pair<A>> input);

}
