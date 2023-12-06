package com.example.caregiverphase2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caregiverphase2.model.pojo.mark_read_notification.MarkReadNotificationResponse
import com.example.caregiverphase2.model.repository.MarkReadNotificationRepository
import com.example.caregiverphase2.model.repository.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkReadNotificationViewModel @Inject constructor(private val repository: MarkReadNotificationRepository) : ViewModel() {
    private var _response = MutableLiveData<Outcome<MarkReadNotificationResponse?>?>()
    val response: LiveData<Outcome<MarkReadNotificationResponse?>?> = _response

    fun markReadNotification(
        notification_id: String,
        token: String
    ) = viewModelScope.launch {
        repository.login(notification_id, token).onStart {
            _response.value = Outcome.loading(true)
        }.catch {
            _response.value = Outcome.Failure(it)
        }.collect {
            _response.value = Outcome.success(it)
        }
    }
    fun navigationComplete(){
        _response.value = null
    }
}