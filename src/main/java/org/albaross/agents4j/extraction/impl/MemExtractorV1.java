package org.albaross.agents4j.extraction.impl;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.Xtractor;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;
import org.albaross.agents4j.extraction.data.Tuple;

import java.util.Collection;
import java.util.List;

public class MemExtractorV1 implements Xtractor<String> {

    @Override
    public List<Tuple<String>> initialize(KnowledgeBase<String> kb, Collection<Pair<String>> pairs) {
        return null;
    }

    @Override
    public Collection<Rule<String>> create(List<Tuple<String>> tuples) {
        return null;
    }

    @Override
    public List<Tuple<String>> merge(List<Tuple<String>> tuples) {
        return null;
    }
}
