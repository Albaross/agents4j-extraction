package org.albaross.agents4j.extraction

typealias State = Collection<String>

data class Rule<A>(val state: State, val action: A, val weight: Double) {
    override fun toString() = "$state => $action [$weight]"
}

// operations on state action pairs

inline val <A> Pair<State, A>.state: State
    get() = this.first

inline val <A> Pair<State, A>.action: A
    get() = this.second

// operations on item triples

inline val <A> Triple<State, Collection<Pair<State, A>>, Collection<Rule<A>>>.state: State
    get() = this.first

inline val <A> Triple<State, Collection<Pair<State, A>>, Collection<Rule<A>>>.pairs: Collection<Pair<State, A>>
    get() = this.second

inline val <A> Triple<State, Collection<Pair<State, A>>, Collection<Rule<A>>>.rules: Collection<Rule<A>>
    get() = this.third