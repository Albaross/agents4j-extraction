package org.albaross.agents4j.extraction;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.util.Locale;
import java.util.Set;

@Data
@Builder
public class Rule<A> {

    @Singular("state")
    @NonNull
    private final Set<String> premise;

    @NonNull
    private final A conclusion;
    private final double confidence;

    @Override
    public String toString() {
        return Utils.toString(premise) + " => " + conclusion + " [" + String.format(Locale.ENGLISH, "%.2f", confidence) + "]";
    }

}
