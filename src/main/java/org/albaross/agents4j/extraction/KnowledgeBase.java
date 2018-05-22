package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Rule;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

public interface KnowledgeBase<A> extends Iterable<Collection<Rule<A>>> {

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    Collection<Rule<A>> reason(Set<String> state);

    default Set<A> actions(Set<String> state) {
        return reason(state).stream()
                .map(Rule::getConclusion)
                .collect(toSet());
    }

    void add(Rule<A> rule);

    void remove(Rule<A> rule);

    default void addAll(Collection<Rule<A>> rules) {
        rules.forEach(this::add);
    }

    default void removeAll(Collection<Rule<A>> rules) {
        rules.forEach(this::remove);
    }

    default KnowledgeBase<A> filter(Consumer<KnowledgeBase<A>> filter) {
        filter.accept(this);
        return this;
    }

    default <T> KnowledgeBase<A> filter(BiConsumer<KnowledgeBase<A>, T> filter, T arg) {
        filter.accept(this, arg);
        return this;
    }

    void clear();

    default Stream<Collection<Rule<A>>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}
