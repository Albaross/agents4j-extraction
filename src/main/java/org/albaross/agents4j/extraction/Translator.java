package org.albaross.agents4j.extraction;

import java.util.Set;
import java.util.function.Function;

public interface Translator<S> extends Function<S, Set<String>> { }
