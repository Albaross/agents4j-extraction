package org.albaross.agents4j.extraction.impl;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.Xtractor;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

public class AdaptedExtractorV1 implements Xtractor<Integer> {

    @Override
    public Set<Set<String>> initialize(KnowledgeBase<Integer> kb, Collection<Pair<Integer>> input) {

        // create rules with empty premise for all actions
        Map<Integer, Long> grouped = input.stream()
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
    public Collection<Rule<Integer>> create(Set<Set<String>> items, Collection<Pair<Integer>> input) {
        final List<Rule<Integer>> rules = new ArrayList<>();

        items.forEach(state -> {
            // determine support for current state
            Collection<Pair<Integer>> supp = input.stream()
                    .filter(p -> p.getState().containsAll(state))
                    .collect(toList());

            // create rules with current state for all action
            Map<Integer, Long> grouped = supp.stream()
                    .collect(groupingBy(Pair::getAction, counting()));

            grouped.forEach((action, count) -> rules.add(new Rule<>(state, action, (double) count / supp.size())));
        });

        return rules;
    }

    @Override
    public Set<Set<String>> merge(Set<Set<String>> items, Collection<Pair<Integer>> input) {
        final Set<Set<String>> merged = new HashSet<>();
        final List<Set<String>> itemList = new ArrayList(items);

        // merge pairwise
        for (int i = 0; i < itemList.size(); i++) {
            for (int k = i + 1; k < itemList.size(); k++) {
                mergeStates(itemList.get(i), itemList.get(k), items, input)
                        .ifPresent(merged::add);
            }
        }

        return merged;
    }

    public static final Optional<Set<String>> mergeStates(Set<String> state1, Set<String> state2,
                                                          Set<Set<String>> items,
                                                          Collection<Pair<Integer>> input) {
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

