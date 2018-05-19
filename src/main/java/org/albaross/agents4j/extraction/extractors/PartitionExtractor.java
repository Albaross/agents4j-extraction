package org.albaross.agents4j.extraction.extractors;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.Xtractor;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.data.Rule;
import org.albaross.agents4j.extraction.data.Tuple;

import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class PartitionExtractor<A> implements Xtractor<A, Tuple<A>> {

    @Override
    public Collection<Tuple<A>> initialize(KnowledgeBase<A> kb, Collection<Pair<A>> input) {
        final Map<A, Long> grouped = input.stream()
                .collect(groupingBy(Pair::getAction, counting()));

        final long max = grouped.values().stream().mapToLong(Long::longValue).max().getAsLong();

        // determine the actions with highest number of occurrences
        grouped.forEach((action, count) -> {
            if (count == max)
                kb.add(new Rule<>(Collections.emptySet(), action, (double) count / input.size()));
        });

        final Map<String, Multiset<Pair<A>>> map = new HashMap<>();
        input.forEach(pair -> {
            pair.getState().forEach(s -> {
                Multiset<Pair<A>> supp = map.get(s);
                if (supp == null) map.put(s, supp = HashMultiset.create());
                supp.add(pair);
            });
        });

        // create a list of premise-tuples for the collected literals
        final Collection<Tuple<A>> items = empty();
        map.forEach((s, supp) -> items.add(new Tuple<>(Collections.singleton(s), supp, Collections.emptyList())));

        return items;
    }

    @Override
    public Collection<Rule<A>> create(Collection<Tuple<A>> items, Collection<Pair<A>> input) {
        final List<Rule<A>> rules = new ArrayList<>();

        items.forEach(mu -> {
            final Map<A, Long> grouped = mu.getPairs().stream()
                    .collect(groupingBy(Pair::getAction, counting()));

            final long max = grouped.values().stream().mapToLong(Long::longValue).max().getAsLong();

            // determine the actions with highest number of occurrences
            grouped.forEach((action, count) -> {
                if (count == max)
                    rules.add(new Rule<>(mu.getState(), action, (double) count / mu.getPairs().size()));
            });
        });

        return rules;
    }

    @Override
    public Optional<Tuple<A>> mergeItems(Tuple<A> mu1, Tuple<A> mu2, Collection<Tuple<A>> items, Collection<Pair<A>> input) {
        Set<String> state1 = mu1.getState();
        Set<String> state2 = mu2.getState();

        if (state1.size() != state2.size())
            return Optional.empty();

        // check whether symbols 1 to n-1 matches
        int n = state1.size();
        Iterator<String> it1 = state1.iterator(), it2 = state2.iterator();

        for (int d = 0; d < n - 1; d++) {
            if (!it1.next().equals(it2.next()))
                return Optional.empty();
        }

        if (it1.next().equals(it2.next()))
            return Optional.empty();

        // merge states
        Set<String> union = new TreeSet<>();
        union.addAll(state1);
        union.addAll(state2);

        Multiset supp = Multisets.intersection(mu1.getPairs(), mu2.getPairs());
        if (supp.isEmpty())
            return Optional.empty();

        return Optional.of(new Tuple<>(union, supp, Collections.emptyList()));
    }
}
