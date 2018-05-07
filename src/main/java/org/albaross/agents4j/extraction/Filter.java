package org.albaross.agents4j.extraction;

import java.util.function.Function;

public interface Filter<KB extends KnowledgeBase> extends Function<KB,KB> { }
