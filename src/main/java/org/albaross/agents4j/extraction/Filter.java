package org.albaross.agents4j.extraction;

import org.albaross.agents4j.extraction.KnowledgeBase;

import java.util.function.Function;

public interface Filter<A> extends Function<KnowledgeBase<A>, KnowledgeBase<A>> { }
