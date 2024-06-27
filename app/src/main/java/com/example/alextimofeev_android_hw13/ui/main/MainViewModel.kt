package com.example.alextimofeev_android_hw13.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    private var searchInWork: Job? = null

    private val _isSearchProcess = MutableStateFlow(false)
    val isSearchProcess:StateFlow<Boolean> = _isSearchProcess

   //Для отражения результата
    private val _showResult = MutableStateFlow("")
    val showResult: StateFlow<String> = _showResult

    //С целью, чтобы поисковая строка не только отображала данные из вьюмодели, но и отправляла их ей
    private val _searchRequest = MutableStateFlow("")
    var searchRequest: String
        get() = _searchRequest.value
        set(value) {
            _searchRequest.value = value
        }


    init {
        _isSearchProcess.value = false
        _showResult.value="Здесь будет отображаться результат"

        //Реализация запроса с задержкой
        _searchRequest.debounce(300).onEach { request ->
                searchInWork?.cancel()
                searchInWork = viewModelScope.launch {
                    realizeSearch(request)
                }
            }
            .launchIn(viewModelScope)
    }

    // С учетом проверки на валидность
    private fun realizeSearch(searchText: String) {
        if (searchText.length >= 3) {
            _isSearchProcess.value = true
            viewModelScope.launch {
                delay(300)
                searchingProcess(searchText)
            }
        }
    }
    private fun searchingProcess(inputText: String) {
        viewModelScope.launch {
            delay(5_000)
            _showResult.value = "По запросу $inputText ничего не найдено"
            _isSearchProcess.value = false

        }
    }

}