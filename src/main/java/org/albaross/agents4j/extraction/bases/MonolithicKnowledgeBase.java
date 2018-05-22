package org.albaross.agents4j.extraction.bases;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

public class MonolithicKnowledgeBase<A> implements KnowledgeBase<A> {

    private final Set<Rule<A>> base = new TreeSet<>();

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public Collection<Rule<A>> reason(Set<String> state) {
        if (base.isEmpty())
            return emptyList();

        Map<Double, List<Rule<A>>> grouped = base.stream()
                .filter(r -> state.containsAll(r.getPremise()))
                .collect(groupingBy(Rule::getConfidence, toList()));

        return emptyList();
    }

    @Override
    public void add(Rule<A> rule) {
        base.add(rule);
    }

    @Override
    public void remove(Rule<A> rule) {
        base.remove(rule);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public Iterator<Collection<Rule<A>>> iterator() {
        final Map<Integer, Collection<Rule<A>>> grouped = base.stream()
                .collect(groupingBy(r -> r.getPremise().size(), TreeMap::new, toCollection(ArrayList::new)));

        return grouped.values().iterator();
    }

    @Override
    public String toString() {
        return null;
    }

}
