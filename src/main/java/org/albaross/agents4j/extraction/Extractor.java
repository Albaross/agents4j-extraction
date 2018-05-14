package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.data.Pair;

import java.util.Collection;
import java.util.function.Function;

public interface Extractor<A> extends Function<Collection<Pair<A>>, KnowledgeBase<A>> { }
