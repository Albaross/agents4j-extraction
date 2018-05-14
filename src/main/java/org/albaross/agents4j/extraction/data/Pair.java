package org.albaross.agents4j.extraction.data;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
public class Pair<A> {

    @NonNull
    private final Set<String> state;
    @NonNull
    private final A action;

    @Override
    public String toString() {
        return "(" + state + "," + action + ")";
    }

}
