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
        return append(new StringBuilder()).toString();
    }

    public static final String toString(Set<String> premise) {
        return append(new StringBuilder(), premise).toString();
    }

    public StringBuilder append(StringBuilder sb) {
        append(sb, premise);
        sb.append(" => ");
        sb.append(conclusion);
        sb.append(" [");
        sb.append(String.format(Locale.ENGLISH, "%.2f", confidence));
        sb.append("]");
        return sb;
    }

    public static final StringBuilder append(StringBuilder sb, Set<String> premise) {
        if (premise.isEmpty())
            return sb.append("T");

        List<String> temp = new ArrayList<>(premise);
        Collections.sort(temp);

        int i = 0;
        for (String s_i : temp) {
            if (i > 0)
                sb.append(" ^ ");

            sb.append(s_i);
            i++;
        }

        return sb;
    }

}
