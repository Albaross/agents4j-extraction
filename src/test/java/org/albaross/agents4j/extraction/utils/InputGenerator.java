package org.albaross.agents4j.extraction.utils;

import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Pair;
import org.albaross.agents4j.extraction.extractors.AdaptedExtractor;

import java.util.*;

public class InputGenerator {

    private static final Random RND = new Random();

    public static final <A> Collection<Pair<A>> generate(int count, List<String> dimensions, List<A> actions) {

        if (count <= 0 || dimensions.isEmpty() || actions.isEmpty())
            return Collections.emptySet();

        int range = (int) Math.ceil(Math.pow(50 * count, (double) 1 / dimensions.size()));
        Set<Pair<A>> pairs = new HashSet<>(count);

        while (pairs.size() < count) {
            Pair.PairBuilder<A> builder = Pair.<A>builder();
            for (String s : dimensions)
                builder.symbol(s + "_" + RND.nextInt(range));

            builder.action(actions.get(RND.nextInt(actions.size())));
            pairs.add(builder.build());
        }

        return pairs;
    }

    public static void main(String... args) {
        Collection<Pair<String>> pairs = InputGenerator.generate(1000, Arrays.asList("u", "t", "x", "y", "z"), Arrays.asList("north", "south", "east", "west"));
        System.out.println("generated pairs");
        AdaptedExtractor<String> ext = new AdaptedExtractor<>();
        long start, end;
        KnowledgeBase<String> kb;
        start = System.currentTimeMillis();
        kb = ext.apply(pairs);
        end = System.currentTimeMillis();
        System.out.println(kb);
        System.out.println((end - start) + "ms");
    }

}
