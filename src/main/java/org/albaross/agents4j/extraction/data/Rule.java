package org.albaross.agents4j.extraction.data;

import lombok.*;

import java.util.*;

@Data
@Builder
@RequiredArgsConstructor
public class Rule<A> implements Comparable<Rule<A>> {

    @Singular("symbol")
    @NonNull
    private final Set<String> premise;

    @NonNull
    private final A conclusion;
    private final double confidence;

    @Override
    public int compareTo(Rule<A> other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return appendTo(new StringBuilder()).toString();
    }

    public static final String valueOf(Set<String> premise) {
        return appendTo(new StringBuilder(), premise).toString();
    }

    public StringBuilder appendTo(StringBuilder sb) {
        appendTo(sb, premise);
        sb.append(" => ");
        sb.append(conclusion);
        sb.append(" [");
        sb.append(String.format(Locale.ENGLISH, "%.3f", confidence));
        sb.append("]");
        return sb;
    }

    public static final StringBuilder appendTo(StringBuilder sb, Set<String> premise) {
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
