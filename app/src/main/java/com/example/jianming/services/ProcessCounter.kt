package com.example.jianming.services

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object ProcessCounter {
    private val counter:ConcurrentHashMap<Long, Counter> = ConcurrentHashMap<Long, Counter>()
    private val finishedCounter = mutableMapOf<Long, Counter>()

    public fun initCounter(id: Long, max:Int): Counter? {
        return counter.putIfAbsent(id, Counter(max))
    }

    public fun addCounter(id: Long): Counter? {
        return counter.computeIfPresent(id) { inId, inCount ->
            inCount.add()
            inCount
        }
    }

    public fun getCounter(id: Long): Counter? {
        val counter1 = counter[id]
        if (counter1 != null) {
            return counter1
        }
        return finishedCounter[id]
    }

    public fun remove(id: Long) {
        val counter1 = counter.remove(id)
        if (counter1 != null) {
            finishedCounter[id] = counter1
        }
    }

    class Counter(val max: Int) {
        private val process: AtomicInteger = AtomicInteger(0)

        fun setProcess(value: Int) {
            process.set(value)
        }

        fun add() {
            process.incrementAndGet()
        }

        fun getProcess() = process.get()
    }
}