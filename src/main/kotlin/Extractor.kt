package org.albaross.agents4j.extraction

import org.albaross.agents4j.extraction.data.Pair
import java.util.function.Function

interface Extractor<A> : Function<Collection<Pair<A>>, KnowledgeBase<A>>