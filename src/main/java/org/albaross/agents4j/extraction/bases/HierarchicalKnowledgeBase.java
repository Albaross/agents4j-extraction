package org.albaross.agents4j.extraction.bases;

import lombok.RequiredArgsConstructor;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.max;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class HierarchicalKnowledgeBase<A> implements KnowledgeBase<A> {

    private final TreeMap<Long, TreeSet<Rule<A>>> base = new TreeMap<>(Comparator.reverseOrder());

    private int size = 0;

    @Override
    public void add(Rule<A> rule) {
        long dim = rule.getPremise().size();

        TreeSet<Rule<A>> level = base.get(dim);
        if (level == null) base.put(dim, level = new TreeSet<>());

        if (level.add(rule)) size++;
    }

    @Override
    public void remove(Rule<A> rule) {
        long dim = rule.getPremise().size();

        TreeSet<Rule<A>> level = base.get(dim);
        if (level == null) return;

        if (level.remove(rule)) size--;

        if (level.isEmpty())
            base.remove(dim);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Collection<Rule<A>> reasoning(Set<String> state) {
        // iterate over the rule-sets in descending order
        for (Set<Rule<A>> rules : base.values()) {
            Map<Double, List<Rule<A>>> grouped = rules.stream()
                    .filter(r -> state.containsAll(r.getPremise()))
                    .collect(groupingBy(Rule::getConfidence, toList()));

            if (!grouped.isEmpty())
                return grouped.get(max(grouped.keySet()));
        }

        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");

        base.descendingMap().forEach((dim, level) -> {
            level.forEach(r -> r.appendTo(sb).append("\n"));
            sb.append("--------------------\n");
        });

        return sb.toString();

    }

    @Override
    public Iterator<Collection<Rule<A>>> iterator() {
        return new HierarchicalIterator<>(base.descendingMap().values().iterator());
    }

    @RequiredArgsConstructor
    private static class HierarchicalIterator<A> implements Iterator<Collection<Rule<A>>> {

        private final Iterator<TreeSet<Rule<A>>> iterator;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Collection<Rule<A>> next() {
            return iterator.next();
        }
    }
}
