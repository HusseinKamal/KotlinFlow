package com.hussein.flowexamples

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

/**Version 1 ,2 means that I added many updates in collectFlow() but save old codes in collectFlowVersion1(), collectFlowVersion2() ,collectFlowVersion3(),collectFlowVersion4() etc*/
class MainViewModel(private val dispatcherInterface: DispatcherInterface= DefaultDispatcher()) : ViewModel(){
    val countDownFlow = flow<Int>{
        val startingValue = 5
        var currentValue = startingValue
        emit(currentValue)
        while (currentValue > 0){
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }.flowOn(dispatcherInterface.main)

    //State Flow
    private val _stateflow = MutableStateFlow(0)
    val stateflow = _stateflow.asStateFlow()

    //Shared Flow
    private val _sharedflow = MutableSharedFlow<Int>(replay = 5) //Reply mean cache five times of collectors
    val sharedflow  = _sharedflow.asSharedFlow()


    init {
        collectFlow()

        //--------------State Flow
        //squareNumber(3) // This added for get cached values after first run
        viewModelScope.launch(dispatcherInterface.main) {
            sharedflow.collect{
                delay(2000L)
                println("FIRST FLOW: The received number is $it ")
            }
        }
        viewModelScope.launch(dispatcherInterface.main) {
            sharedflow.collect{
                delay(3000L)
                println("SECOND FLOW: The received number is $it ")
            }
        }
        squareNumber(3)
        //Output
        //FIRST FLOW: The received number is 9
        //SECOND FLOW: The received number is 9
        //---------------------------
    }

    fun squareNumber(number: Int){
        viewModelScope.launch(dispatcherInterface.main) {
            _sharedflow.emit(number * number)
        }
    }
    fun incrementCounter(){
        _stateflow.value += 1
    }
    private fun collectFlow(){
        val flow1 = flow {
            emit(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main Dish")
            delay(1000L)
            emit("Desert")
        }
        viewModelScope.launch {
            flow1.onEach {
                println("FLOW : $it is delivered")
            }.conflate()//run collector in separate coroutine
                // and collector get most recent value
                // never suspended due to a slow collector
                .collectLatest{
                    println("Now : Now eating $it")
                    delay(1500L)
                    println("FLOW : Finish eating $it")
                }
        }
    }
    private fun collectFlowVersion6(){
        val flow1 = flow {
            emit(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main Dish")
            delay(1000L)
            emit("Desert")
        }
        viewModelScope.launch {
            flow1.onEach {
                println("FLOW : $it is delivered")
            }.buffer()//run collector in separate coroutine
                .collect{
                println("Now : Now eating $it")
                delay(1500L)
                println("FLOW : Finish eating $it")
            }
        }
    }
    private fun collectFlowVersion5(){
        val flow1 = flow {
            emit(1)
            delay(500L)
            emit(2)
        }
        val flow2 = flow {
            emit(1)
            delay(500L)
            emit(2)
        }
        viewModelScope.launch {
            flow1.flatMapConcat {value ->
                flow {
                    emit(value+1)
                    delay(500L)
                    emit(value+2)
                }
            }.collect{value ->
                println("The value is $value")
                //Output:
                // The value is 2
                //The value is 3
                //The value is 3
                //The value is 4
            }
        }
    }
    private fun collectFlowVersion4(){
        viewModelScope.launch {
            val reduceResult = countDownFlow.fold(100){accumulator, value -> //initial value 100 start with it
                accumulator + value
            }
            println("The count is $reduceResult") //Output The count is 115 --> 100+1+2+3+4+5
        }
    }
    private fun collectFlowVersion3(){
        viewModelScope.launch {
            val reduceResult = countDownFlow.reduce{accumulator, value ->
                accumulator + value
            }
            println("The count is $reduceResult") //Output The count is 15 --> 1+2+3+4+5
        }
    }

    private fun collectFlowVersion2(){
        viewModelScope.launch {
            val count = countDownFlow
                .filter {time -> //This is Operator flow
                    time % 2 == 0 //Show even time only and Output result 10 , 8 ,6 ,4 ,3,0

                }
                .map {time ->
                    time * time // Multiply time value by itself and Output result 100 , 64 , 36 ,16 ,4 ,0 only even time values
                }
                .onEach{time ->
                    println(time)
                }
                .count {
                    it % 2 == 0
                }
            println("The count is $count") //Output The count is 6
        }
    }

    private fun collectFlowVersion1(){
        viewModelScope.launch {
            countDownFlow
                .filter {time -> //This is Operator flow
                    time % 2 == 0 //Show even time only and Output result 10 , 8 ,6 ,4 ,3,0

                }
                .map {time ->
                    time * time // Multiply time value by itself and Output result 100 , 64 , 36 ,16 ,4 ,0 only even time values
                }
                .onEach{time ->
                    println(time)
                }
                .collect { time ->
                    delay(1500L)
                    println("The current time is $time")

                }
        }
    }
}