package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Rule;

import java.util.Collection;
import java.util.Set;

public interface KnowledgeBase<A> {

    void add(Rule<A> rule);

    void addAll(Collection<Rule<A>> rules);

    Collection<Rule<A>> reasoning(Set<String> state);

}
