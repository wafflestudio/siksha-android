package com.wafflestudio.siksha2.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <T1, T2> Flow<T1>.combines(flow: Flow<T2>): Flow<Pair<T1, T2>> {
    return this.combine(flow) { a, b ->
        Pair(a, b)
    }
}

fun <T1, T2, T3> Flow<T1>.combines(flow1: Flow<T2>, flow2: Flow<T3>): Flow<Triple<T1, T2, T3>> {
    return this.combine(flow1) { a, b ->
        Pair(a, b)
    }.combine(flow2) { (a, b), c ->
        Triple(a, b, c)
    }
}

object FlowUtil {
    fun <T1, T2, T3> combines(
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>
    ): Flow<Triple<T1, T2, T3>> {
        return flow1.combine(flow2) { a, b ->
            Pair(a, b)
        }.combine(flow3) { (a, b), c ->
            Triple(a, b, c)
        }
    }
}
