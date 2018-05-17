package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.bases.ModularKnowledgeBase;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;
import org.albaross.agents4j.extraction.data.Tuple;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface Xt2actor<A> extends Extractor<A> {

    @Override
    default KnowledgeBase<A> apply(Collection<Pair<A>> input) {
        KnowledgeBase<A> kb = knowledgeBase();
        if (input.isEmpty())
            return kb;

        List<Tuple<A>> tuples = initialize(kb, input);
        int k = 1, n = input.iterator().next().getState().size();

        while (!tuples.isEmpty()) {
            kb.addAll(create(tuples));
            tuples = (k < n) ? merge(tuples) : Collections.emptyList();
            k++;
        }

        return kb;
    }

    default KnowledgeBase<A> knowledgeBase() {
        return new ModularKnowledgeBase<>();
    }

    List<Tuple<A>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> pairs);

    Collection<Rule<A>> create(List<Tuple<A>> tuples);

    List<Tuple<A>> merge(List<Tuple<A>> tuples);

}
