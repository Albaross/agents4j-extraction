package org.albaross.agents4j.extraction.bases;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class HierarchicalKnowledgeBase<A> implements KnowledgeBase<A> {

    private final TreeMap<Long, Collection<Rule<A>>> base = new TreeMap<>(reverseOrder());
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public Collection<Rule<A>> reason(Set<String> state) {
        if (base.isEmpty())
            return emptyList();

        // iterate over the rule-sets in descending order
        for (Collection<Rule<A>> rules : base.values()) {
            final Map<Double, List<Rule<A>>> grouped = rules.stream()
                    .filter(r -> state.containsAll(r.getPremise()))
                    .collect(groupingBy(Rule::getConfidence, TreeMap::new, toList()))
                    .descendingMap();

            if (!grouped.isEmpty()) {
                return grouped.values().stream()
                        .findFirst().get();
            }
        }

        return emptyList();
    }

    @Override
    public void add(Rule<A> rule) {
        long dim = rule.getPremise().size();

        Collection<Rule<A>> level = base.get(dim);
        if (level == null) base.put(dim, level = new TreeSet<>());

        if (level.add(rule)) size++;
    }

    @Override
    public void remove(Rule<A> rule) {
        long dim = rule.getPremise().size();

        Collection<Rule<A>> level = base.get(dim);
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
    public Iterator<Collection<Rule<A>>> iterator() {
        return base.descendingMap().values().iterator();
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

}
