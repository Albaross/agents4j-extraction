package org.albaross.agents4j.extraction.bases;

import lombok.RequiredArgsConstructor;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;
import java.util.stream.Collectors;

public class ModularKnowledgeBase<A> implements KnowledgeBase<A> {

    private final TreeMap<Long, TreeMap<Double, TreeSet<Rule<A>>>> base = new TreeMap<>(Comparator.reverseOrder());

    private int size = 0;

    @Override
    public void add(Rule<A> rule) {
        long dim = rule.getPremise().size();
        double conf = rule.getConfidence();

        TreeMap<Double, TreeSet<Rule<A>>> level = base.get(dim);
        if (level == null) base.put(dim, level = new TreeMap<>(Comparator.reverseOrder()));

        TreeSet<Rule<A>> module = level.get(conf);
        if (module == null) level.put(conf, module = new TreeSet<>());

        module.add(rule);
    }

    @Override
    public void remove(Rule<A> rule) {
        long dim = rule.getPremise().size();
        double conf = rule.getConfidence();

        TreeMap<Double, TreeSet<Rule<A>>> level = base.get(dim);
        if (level == null) return;

        Set<Rule<A>> module = level.get(conf);
        if (module == null) return;

        module.remove(rule);

        if (module.isEmpty())
            level.remove(conf);

        if (level.isEmpty())
            base.remove(dim);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Collection<Rule<A>> reasoning(Set<String> state) {
        for (TreeMap<Double, TreeSet<Rule<A>>> level : base.values()) {
            for (TreeSet<Rule<A>> module : level.values()) {

                List<Rule<A>> active = module.stream()
                        .filter(r -> state.containsAll(r.getPremise()))
                        .collect(Collectors.toList());

                if (!active.isEmpty())
                    return active;
            }
        }

        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");

        base.descendingMap().forEach((dim, level) -> {
            level.forEach((conf, module) -> module.forEach(r -> r.appendTo(sb).append("\n")));
            sb.append("--------------------\n");
        });

        return sb.toString();
    }

    @Override
    public Iterator<Collection<Rule<A>>> iterator() {
        return new ModularIterator<>(base.descendingMap().values().iterator());
    }

    @RequiredArgsConstructor
    private static class ModularIterator<A> implements Iterator<Collection<Rule<A>>> {

        private final Iterator<Collection<TreeSet<Rule<A>>>> iterator;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Collection<Rule<A>> next() {
            return iterator.next().stream()
                    .flatMap(module -> module.stream())
                    .collect(Collectors.toList());
        }
    }
}
