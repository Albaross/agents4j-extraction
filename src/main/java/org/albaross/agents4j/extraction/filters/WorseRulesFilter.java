package org.albaross.agents4j.extraction.filters;

import org.albaross.agents4j.extraction.Filter;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

public class WorseRulesFilter<A> implements Filter<A> {

    @Override
    public KnowledgeBase<A> apply(KnowledgeBase<A> kb) {
        final List<Rule<A>> toBeRemoved = new ArrayList<>();

        // iterate over each level of rules
        kb.forEach(level -> {

            // group rules by premise
            Collection<List<Rule<A>>> groups = level.stream()
                    .collect(groupingBy(r -> r.getPremise()))
                    .values();

            // for each group of rules
            groups.stream()
                    .forEach(rules -> {

                        // determine max confidence
                        final double max = rules.stream()
                                .collect(reducing(0.0, r -> r.getConfidence(), Math::max));

                        // remove all rules not having max confidence
                        rules.stream()
                                .filter(r -> r.getConfidence() != max)
                                .forEach(r -> toBeRemoved.add(r));
                    });
        });

        kb.removeAll(toBeRemoved);
        return kb;
    }
}
