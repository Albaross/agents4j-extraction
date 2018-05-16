package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.max;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class HierarchicalKnowledgeBase<A> implements KnowledgeBase<A> {

    private final TreeMap<Long, Set<Rule<A>>> base = new TreeMap<>();

    @Override
    public void add(Rule<A> rule) {
        long dim = rule.getPremise().size();
        Set<Rule<A>> ruleSet = base.get(dim);
        if (ruleSet == null)
            base.put(dim, ruleSet = new HashSet<>());

        ruleSet.add(rule);
    }

    @Override
    public void addAll(Collection<Rule<A>> rules) {
        long last = -1;
        Set<Rule<A>> ruleSet = null;

        for (Rule<A> r : rules) {
            long dim = r.getPremise().size();
            if (dim != last) {
                ruleSet = base.get(dim);
                if (ruleSet == null)
                    base.put(dim, ruleSet = new HashSet<>());

                last = dim;
            }

            ruleSet.add(r);
        }
    }

    @Override
    public void remove(Rule<A> rule) {
        long dim = rule.getPremise().size();
        Set<Rule<A>> ruleSet = base.get(dim);

        if (ruleSet != null) {
            ruleSet.remove(rule);

            if (ruleSet.isEmpty())
                base.remove(dim);
        }
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public Collection<Rule<A>> reasoning(Set<String> state) {
        // iterate over the rule-sets in descending order
        for (Set<Rule<A>> rules : base.descendingMap().values()) {
            Map<Double, List<Rule<A>>> grouped = rules.stream()
                    .filter(r -> state.containsAll(r.getPremise()))
                    .collect(groupingBy(Rule::getConfidence, toList()));

            return grouped.get(max(grouped.keySet()));
        }

        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");

        base.forEach((dim, ruleSet) -> {
            ruleSet.forEach(r -> r.append(sb).append("\n"));
            sb.append("--------------------\n");
        });

        return sb.toString();

    }
}
