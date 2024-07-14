package com.example.jianming.services

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object ProcessCounter {
    private val counter:ConcurrentHashMap<Long, Counter> = ConcurrentHashMap<Long, Counter>()
    private val finishedCounter = mutableMapOf<Long, Counter>()

    fun initCounter(id: Long, max:Int): Counter? {
        return counter.putIfAbsent(id, Counter(max))
    }

    fun addCounter(id: Long): Counter? {
        return counter.computeIfPresent(id) { _, inCount ->
            inCount.add()
            inCount
        }
    }

    fun getCounter(id: Long): Counter? {
        synchronized(ProcessCounter) {
            val counter1 = counter[id]
            if (counter1 != null) {
                return counter1
            }
            return finishedCounter[id]
        }
    }

    fun remove(id: Long) {
        synchronized(ProcessCounter) {
            val counter1 = counter.remove(id)
            if (counter1 != null) {
                finishedCounter[id] = counter1
            }
        }
    }

    class Counter(val max: Int) {
        private val process: AtomicInteger = AtomicInteger(0)

        fun add() {
            process.incrementAndGet()
        }

        fun getProcess() = process.get()
    }
}