package org.albaross.agents4j.extraction.bases;

import lombok.RequiredArgsConstructor;
import org.albaross.agents4j.extraction.KnowledgeBase;
import org.albaross.agents4j.extraction.data.Rule;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

public class ModularKnowledgeBase<A> implements KnowledgeBase<A> {

    private final TreeMap<Long, TreeMap<Double, TreeSet<Rule<A>>>> base = new TreeMap<>(reverseOrder());
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public Collection<Rule<A>> reason(Set<String> state) {
        if (base.isEmpty())
            return emptyList();

        for (TreeMap<Double, TreeSet<Rule<A>>> level : base.values()) {
            for (TreeSet<Rule<A>> module : level.values()) {

                List<Rule<A>> active = module.stream()
                        .filter(r -> state.containsAll(r.getPremise()))
                        .collect(toList());

                if (!active.isEmpty())
                    return active;
            }
        }

        return emptyList();
    }

    @Override
    public void add(Rule<A> rule) {
        long dim = rule.getPremise().size();
        double conf = rule.getConfidence();

        TreeMap<Double, TreeSet<Rule<A>>> level = base.get(dim);
        if (level == null) base.put(dim, level = new TreeMap<>(reverseOrder()));

        TreeSet<Rule<A>> module = level.get(conf);
        if (module == null) level.put(conf, module = new TreeSet<>());

        if (module.add(rule)) size++;
    }

    @Override
    public void remove(Rule<A> rule) {
        long dim = rule.getPremise().size();
        double conf = rule.getConfidence();

        TreeMap<Double, TreeSet<Rule<A>>> level = base.get(dim);
        if (level == null) return;

        Set<Rule<A>> module = level.get(conf);
        if (module == null) return;

        if (module.remove(rule)) size--;

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
    public Iterator<Collection<Rule<A>>> iterator() {
        return new ModularIterator<>(base.descendingMap().values().iterator());
    }

    @RequiredArgsConstructor
    private static class ModularIterator<A> implements Iterator<Collection<Rule<A>>> {

        private final Iterator<TreeMap<Double, TreeSet<Rule<A>>>> iterator;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Collection<Rule<A>> next() {
            return iterator.next().values().stream()
                    .flatMap(Collection::stream)
                    .collect(toList());
        }
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

}
