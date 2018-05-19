package org.albaross.agents4j.extraction.data;

import lombok.*;

import java.util.Set;

import static org.albaross.agents4j.extraction.data.Rule.valueOf;

@Data
@Builder
@RequiredArgsConstructor
public class Pair<A> {

    @Singular("symbol")
    @NonNull
    private final Set<String> state;
    @NonNull
    private final A action;

    @Override
    public String toString() {
        return "(" + valueOf(state) + "," + action + ")";
    }

}
