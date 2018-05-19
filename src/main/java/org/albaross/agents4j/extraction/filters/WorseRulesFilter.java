package org.albaross.agents4j.extraction.filters;

import org.albaross.agents4j.extraction.Filter;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;
import java.util.stream.Collectors;

public class WorseRulesFilter<A> implements Filter<A> {

    @Override
    public KnowledgeBase<A> apply(KnowledgeBase<A> kb) {
        final List<Rule<A>> worse = new ArrayList<>();

        kb.forEach(level -> {
            Map<Set<String>, List<Rule<A>>> grouped = level.stream()
                    .collect(Collectors.groupingBy(r -> r.getPremise(), Collectors.toList()));

            grouped.values().forEach(rules -> {
                OptionalDouble maxConf = rules.stream()
                        .mapToDouble(r -> r.getConfidence())
                        .max();

                maxConf.ifPresent(max -> {
                    rules.stream()
                            .filter(r -> r.getConfidence() != max)
                            .forEach(r -> worse.add(r));
                });
            });
        });

        kb.removeAll(worse);

        return kb;
    }
}
