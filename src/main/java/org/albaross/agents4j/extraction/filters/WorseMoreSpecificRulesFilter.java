package org.albaross.agents4j.extraction.filters;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class WorseMoreSpecificRulesFilter<A> implements Consumer<KnowledgeBase<A>> {

    @Override
    public void accept(KnowledgeBase<A> kb) {
        final List<Rule<A>> toBeRemoved = new ArrayList<>();
        final List<Rule<A>> lowerLevels = new ArrayList<>();

        Function<Rule<A>, Double> max = r -> r.getConfidence();

        // iterate over all levels of rules
        kb.forEach(level -> {

            // determine max confidence for subset of premise
            final Map<Set<String>, Double> grouped = lowerLevels.stream()
                    .collect(groupingBy(Rule::getPremise, reducing(0.0, Rule::getConfidence, Math::max)));

            final List<Rule<A>> worse = level.stream()
                    .filter(r -> !grouped.containsKey(r.getPremise()))
                    .filter(r -> r.getConfidence() <= grouped.get(r.getPremise()))
                    .collect(toList());

            toBeRemoved.addAll(worse);
            level.removeAll(worse);
            lowerLevels.addAll(level);
        });

        kb.removeAll(toBeRemoved);
    }

}
