package org.albaross.agents4j.extraction.data;

import lombok.Data;
import lombok.NonNull;

import java.util.*;

@Data
public class Rule<A> {

    @NonNull
    private final Set<String> premise;

    @NonNull
    private final A conclusion;
    private final double confidence;

    @Override
    public String toString() {
        return toString(premise) + " => " + conclusion + " [" + String.format(Locale.ENGLISH, "%.2f", confidence) + "]";
    }

    public static final String toString(Set<String> premise) {
        if (premise.isEmpty()) return "T";

        List<String> temp = new ArrayList<>(premise);
        Collections.sort(temp);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s_i : temp) {
            if (i > 0)
                sb.append(" ^ ");

            sb.append(s_i);
            i++;
        }

        return sb.toString();
    }

}
