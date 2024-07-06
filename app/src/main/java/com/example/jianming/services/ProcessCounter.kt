package com.example.jianming.services

import java.util.concurrent.ConcurrentHashMap

object ProcessCounter {
    private val counter:ConcurrentHashMap<Long, Counter> = ConcurrentHashMap<Long, Counter>()

    public fun initCounter(id: Long, max:Int): Counter? {
        return counter.putIfAbsent(id, Counter(max))
    }

    public fun addCounter(id: Long): Counter? {
        return counter.computeIfPresent(id) { inId, inCount ->
            inCount.add()
            inCount
        }
    }

    public fun remove(id: Long) {
        counter.remove(id)
    }
}