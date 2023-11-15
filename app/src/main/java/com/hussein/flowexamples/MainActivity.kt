package com.hussein.flowexamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hussein.flowexamples.combineMergeZip.CombineViewModel
import com.hussein.flowexamples.ui.theme.FlowExamplesTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    //private val viewModel : MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This for binding value in XML
       /* collectLatestLifecycleFlow(viewModel.stateflow){number ->
            //binding.tvCounter.text = number.toString()
        }*/
       /* lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateflow.collectLatest { number ->
                    binding.tvCounter.text = number.toString()
                }
            }
        }*/
        setContent {
            FlowExamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<MainViewModel>()
                    val combineViewModel = viewModel<CombineViewModel>()
                    val time = viewModel.countDownFlow.collectAsState(initial = 10)
                    val count = viewModel.stateflow.collectAsState(initial = 0) //state flow will keep value of counter on rotate screen
                    //viewModel.stateflow.value = 10//This immutable object UI can't change immutable statflow object
                    
                    LaunchedEffect(key1 = true){
                        viewModel.sharedflow.collect{ number->


                        }
                    }
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                                Text(
                                    text = time.value.toString(),
                                    fontSize = 30.sp,
                                    //modifier = Modifier.align(Alignment.Center)
                                )

                                Button(onClick = { viewModel.incrementCounter() }) {
                                    Text(
                                        text = "Counter ${count.value}",
                                        fontSize = 30.sp,
                                    )
                                }

                                Text(
                                    text = combineViewModel.numberString,
                                    fontSize = 30.sp,
                                    //modifier = Modifier.align(Alignment.Center)
                                )
                                Text(
                                    text = combineViewModel.numberStringMerge,
                                    fontSize = 30.sp,
                                    //modifier = Modifier.align(Alignment.Center)
                                )
                        }
                    }
                }
            }
        }
    }
}

//This used for State Flow
fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>,collect: suspend (T) ->Unit){
    lifecycleScope.launch {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
}

//This used for Shared Flow
fun <T> ComponentActivity.collectLifecycleFlow(flow: Flow<T>,collect: suspend (T) ->Unit){
    lifecycleScope.launch {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collect)
            }
        }
    }
}