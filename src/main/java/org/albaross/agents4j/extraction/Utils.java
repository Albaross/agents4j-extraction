package org.albaross.agents4j.extraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Utils {

    public static String toString(Set<String> premise) {
        if (premise.isEmpty()) return "T";

        List<String> temp = new ArrayList<>(premise);
        Collections.sort(temp);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s_i : temp) {
            if (i > 0)
                sb.append(' ').append('^').append(' ');

            sb.append(s_i);
            i++;
        }

        return sb.toString();
    }

}
