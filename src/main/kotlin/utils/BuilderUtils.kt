package org.albaross.agents4j.extraction.utils

import java.util.Locale.ENGLISH

fun <T> StringBuilder.append(items: Collection<T>, separator: String, empty: String = "",
                             formatter: (T) -> String = { it.toString() }): StringBuilder {

    if (items.isEmpty())
        return this.append(empty)

    for ((i, item) in items.withIndex()) {
        if (i > 0) this.append(separator)
        this.append(formatter(item))
    }

    return this
}

inline fun StringBuilder.append(num: Double, places: Int) = this.append(String.format(ENGLISH, "%.$places", num))