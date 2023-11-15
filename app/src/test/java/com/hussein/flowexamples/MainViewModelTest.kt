package com.hussein.flowexamples

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = TestDispatcher()
        viewModel = MainViewModel(testDispatcher)

    }

    @Test
    fun `countDownFlow, properly counts down from 5 to 0`() = runBlocking{
        viewModel.countDownFlow.test{
            for (i in 5 downTo  0)
            {
                testDispatcher.testDispatchers.advanceTimeBy(1000L)
                val emission = awaitItem()
                //assertThat(emission).isEqualTo(i)
                assertEquals(emission,i)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test fun `squareNumber , number properly squared`()= runBlocking {
        val job = launch {
            viewModel.sharedflow.test {
                val emission = awaitItem()
                assertEquals(emission,9)
                //assertThat(emission).isEqualTo(9)
                cancelAndConsumeRemainingEvents()
            }
        }
        viewModel.squareNumber(3)
        job.join()
        job.cancel()
    }
}