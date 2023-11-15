package com.hussein.flowexamples

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherInterface {
    val main :CoroutineDispatcher
    val io :CoroutineDispatcher
    val default:CoroutineDispatcher
}

class DefaultDispatcher:DispatcherInterface{
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default

}