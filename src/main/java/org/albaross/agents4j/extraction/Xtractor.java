package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.bases.ModularKnowledgeBase;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

public interface Xtractor<A, C> extends Extractor<A> {

    @Override
    default KnowledgeBase<A> apply(Collection<Pair<A>> input) {
        KnowledgeBase<A> kb = knowledgeBase();
        if (input.isEmpty())
            return kb;

        Collection<C> items = initialize(kb, input);
        int k = 1, n = input.iterator().next().getState().size();

        while (!items.isEmpty()) {
            kb.addAll(create(items, input));
            items = (k < n) ? merge(items, input) : Collections.emptySet();
            k++;
        }

        return kb;
    }

    default KnowledgeBase<A> knowledgeBase() {
        return new ModularKnowledgeBase<>();
    }

    Collection<C> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> input);

    Collection<Rule<A>> create(Collection<C> items, Collection<Pair<A>> input);

    default Collection<C> merge(Collection<C> items, Collection<Pair<A>> input) {
        final Collection<C> merged = empty();
        final List<C> list = new ArrayList<>(items);

        // merge pairwise
        for (int i = 0; i < list.size(); i++) {
            for (int k = i + 1; k < list.size(); k++) {
                mergeItems(list.get(i), list.get(k), items, input).ifPresent(merged::add);
            }
        }

        return merged;
    }

    default Collection<C> empty() {
        return new ArrayList<>();
    }

    Optional<C> mergeItems(C item1, C item2, Collection<C> items, Collection<Pair<A>> input);

}
