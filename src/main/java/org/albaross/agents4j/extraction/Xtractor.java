package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.Extractor;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;
import org.albaross.agents4j.extraction.data.Tuple;

import java.util.Collection;
import java.util.List;

public interface Xtractor<A> extends Extractor<A> {

    @Override
    default KnowledgeBase<A> apply(Collection<Pair<A>> pairs) {
        KnowledgeBase<A> kb = null;
        List<Tuple<A>> tuples = initialize(kb, pairs);

        while (!pairs.isEmpty()) {
            kb.addAll(create(tuples));
            tuples = merge(tuples);
        }

        return kb;
    }

    List<Tuple<A>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> pairs);

    Collection<Rule<A>> create(List<Tuple<A>> tuples);

    List<Tuple<A>> merge(List<Tuple<A>> tuples);

}
