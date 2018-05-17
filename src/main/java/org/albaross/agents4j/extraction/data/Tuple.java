package org.albaross.agents4j.extraction.data;

import com.google.common.collect.Multiset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.Set;

import static org.albaross.agents4j.extraction.data.Rule.valueOf;

@Data
@AllArgsConstructor
public class Tuple<A> {

    @NonNull
    private final Set<String> state;
    @NonNull
    private final Multiset<Pair<A>> pairs;
    private Collection<Rule<A>> rules;

    @Override
    public String toString() {
        return "(" + valueOf(state) + "," + pairs + "," + rules + ")";
    }

}
