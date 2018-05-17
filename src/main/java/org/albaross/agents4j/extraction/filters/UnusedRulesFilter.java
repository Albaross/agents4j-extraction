package org.albaross.agents4j.extraction.filters;

import org.albaross.agents4j.extraction.Filter;
import org.albaross.agents4j.extraction.KnowledgeBase;

public class UnusedRulesFilter<A> implements Filter<A> {

    @Override
    public KnowledgeBase<A> apply(KnowledgeBase<A> kb) {
        return kb;
    }
}
