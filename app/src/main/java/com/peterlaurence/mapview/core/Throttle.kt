package com.peterlaurence.mapview.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Limit the rate a which a [block] is called given the last [T] value sent to the returned
 * [SendChannel].
 *
 * @author peterLaurence
 */
fun <T> CoroutineScope.throttle(wait: Long = 15,
                                block: (T) -> Unit): SendChannel<T> {

    val channel = Channel<T>(capacity = Channel.CONFLATED)

    launch {
        var nextTime = Long.MIN_VALUE
        for (elem in channel) {
            val curTime = System.nanoTime() / 1000000
            if (curTime < nextTime) {
                delay(nextTime - curTime)
                var mostRecent = elem
                while (!channel.isEmpty) {  // take the most recently sent without waiting
                    mostRecent = channel.receive()
                }
                nextTime += wait  // maintain strict time interval between sends
                block(mostRecent)
            } else {
                nextTime = curTime + wait
                block(elem)
            }
        }
    }

    return channel
}