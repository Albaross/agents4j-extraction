package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Rule;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public interface KnowledgeBase<A> {

    void add(Rule<A> rule);

    default void addAll(Collection<Rule<A>> rules) {
        rules.forEach(this::add);
    }

    void remove(Rule<A> rule);

    default void removeAll(Collection<Rule<A>> rules) {
        rules.forEach(this::remove);
    }

    void clear();

    Collection<Rule<A>> reasoning(Set<String> state);

    default Set<A> actions(Set<String> state) {
        return reasoning(state).stream()
                .map(Rule::getConclusion)
                .collect(Collectors.toSet());
    }

}
