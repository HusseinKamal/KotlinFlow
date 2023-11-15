package com.hussein.flowexamples.combineMergeZip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class CombineViewModel : ViewModel() {

    private val isAuthenticated = MutableStateFlow(false)

    private val user = MutableStateFlow<User?>(null)
    private val posts = MutableStateFlow(emptyList<Post>())

    private val _profileState = MutableStateFlow<ProfileState?>(null)
    val profileState =_profileState.asStateFlow()

    private val flow1= (1..10).asFlow().onEach { delay(1000L) }//make delay after each get of pairs in zip operation
    private val flow2= (10..20).asFlow().onEach { delay(300L) }

    var numberString by mutableStateOf("")
        private set

    var numberStringMerge by mutableStateOf("")
        private set

    init {
        isAuthenticated.combine(user){ isAuthenticated,user ->
            if (isAuthenticated) user else null
        }.combine(posts){ user,posts ->
            user?.let {
                _profileState.value = profileState.value?.copy(
                    profilePicUrl = user.profilePicUrl,
                    description = user.description,
                    username = user.username,
                    posts = posts
                )
            }

        }.launchIn(viewModelScope)

        flow1.zip(flow2){ number1 ,number2 ->
            numberString += "($number1,$number2)\n"
        }.launchIn(viewModelScope)

        merge(flow1,flow2).onEach {
            numberStringMerge += "$it\t\t"
        }.launchIn(viewModelScope)
        /*user.combine(posts){user,posts ->
            _profileState.value = profileState.value?.copy(
                profilePicUrl = user?.profilePicUrl,
                description = user?.description,
                username = user?.username,
                posts = posts
            )
            true
        }.combine(isAuthenticated).onEach {

        }.launchIn(viewModelScope)*/

      /*This is same as above code
      viewModelScope.launch {
            user.combine(posts){user,posts ->
                _profileState.value = profileState.value?.copy(
                    profilePicUrl = user?.profilePicUrl,
                    description = user?.description,
                    username = user?.username,
                    posts = posts
                )

            }.collect()
        }*/
    }
}