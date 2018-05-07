package org.albaross.agents4j.extraction;

import java.util.Collection;

public interface KnowledgeBase<S, A> {

    Collection<Rule<A>> reasoning(S state);

}
