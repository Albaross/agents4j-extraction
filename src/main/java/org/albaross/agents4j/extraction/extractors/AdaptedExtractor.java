package org.albaross.agents4j.extraction.extractors;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.Xtractor;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

public class AdaptedExtractor<A> implements Xtractor<A, Set<String>> {

    @Override
    public Collection<Set<String>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> input) {
        // create rules with empty premise for all actions
        Map<A, Long> grouped = input.stream()
                .collect(groupingBy(Pair::getAction, counting()));

        grouped.forEach((action, count) -> kb.add(new Rule<>(emptySet(), action, (double) count / input.size())));

        // collect all literals
        return input.stream()
                .flatMap(p -> p.getState().stream())
                .distinct()
                .map(Collections::singleton)
                .collect(toSet());
    }

    @Override
    public Collection<Rule<A>> create(Collection<Set<String>> items, Collection<Pair<A>> input) {
        final List<Rule<A>> rules = new ArrayList<>();

        items.forEach(state -> {
            // determine support for current state
            Collection<Pair<A>> supp = input.stream()
                    .filter(p -> p.getState().containsAll(state))
                    .collect(toList());

            // create rules with current state for all action
            Map<A, Long> grouped = supp.stream()
                    .collect(groupingBy(Pair::getAction, counting()));

            grouped.forEach((action, count) -> rules.add(new Rule<>(state, action, (double) count / supp.size())));
        });

        return rules;
    }

    @Override
    public Collection<Set<String>> empty() {
        return new HashSet<>();
    }

    @Override
    public Optional<Set<String>> mergeItems(Set<String> item1, Set<String> item2, Collection<Set<String>> items, Collection<Pair<A>> input) {
        final Set<String> state1 = item1;
        final Set<String> state2 = item2;

        if (state1.size() != state2.size())
            return Optional.empty();

        // check whether symbols 1 to n-1 matches
        int n = state1.size();
        Iterator<String> it1 = state1.iterator(), it2 = state2.iterator();

        for (int i = 0; i < n - 1; i++) {
            if (!it1.next().equals(it2.next()))
                return Optional.empty();
        }

        if (it1.next().equals(it2.next()))
            return Optional.empty();

        // merge states
        Set<String> merged = new TreeSet<>();
        merged.addAll(state1);
        merged.addAll(state2);

        // subset check
        for (String s : merged) {
            Set<String> subset = new HashSet<>(merged);
            subset.remove(s);

            if (!items.contains(subset))
                return Optional.empty();
        }

        // check for any support
        if (input.stream().noneMatch(p -> p.getState().containsAll(merged)))
            return Optional.empty();

        // provide merged state
        return Optional.of(merged);

    }
}
