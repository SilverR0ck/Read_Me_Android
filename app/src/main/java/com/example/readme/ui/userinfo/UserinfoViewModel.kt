//package com.example.readme.ui.userinfo
//
//import android.util.Log
//import android.widget.Toast
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.readme.data.entities.UserData
//import com.example.readme.LoginRepository
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//
//class UserinfoViewModel : ViewModel() {
//
//    private val repository = LoginRepository()
//
//
//    private val _nicknameError = MutableLiveData(false)
//    val nicknameError: LiveData<Boolean> get() = _nicknameError
//
//    private val _idError = MutableLiveData(false)
//    val idError: LiveData<Boolean> get() = _idError
//
//    fun validateNickname(nickname: String) {
//        _nicknameError.value = nickname.length > 12
//    }
//
//    fun validateId(accountId: String) {
//        _idError.value = accountId.length > 30
//    }
//
//    fun sendSignUpInfo(user: UserData) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                Log.d("UserinfoViewModel", "Sign up request: $user")
//                val response = repository.sendSignUpInfo(user)
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful) {
//                        Log.d("UserinfoViewModel", "Sign up response: ${response.body()}")
//                    } else {
//                        Log.e("UserinfoViewModel", "Sign up failed: ${response.errorBody()}")
//
//
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("UserinfoViewModel", "Error sending sign up info", e)
//            }
//        }
//    }
//}
package com.example.readme.ui.userinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readme.data.entities.UserData
import com.example.readme.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserinfoViewModel : ViewModel() {

    private val repository = LoginRepository()
    val signResponse = MutableLiveData<UserinfoResponse>()

    private val _nicknameError = MutableLiveData(false)
    val nicknameError: LiveData<Boolean> get() = _nicknameError

    private val _idError = MutableLiveData(false)
    val idError: LiveData<Boolean> get() = _idError

    private val _member4005Error = MutableLiveData<Boolean>()
    val member4005Error: LiveData<Boolean> get() = _member4005Error

    fun validateNickname(nickname: String) {
        _nicknameError.value = nickname.length > 12
    }

    fun validateId(accountId: String) {
        _idError.value = accountId.length > 30
    }

    fun sendSignUpInfo(user: UserData) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("UserinfoViewModel", "Sign up request: $user")
                val response = repository.sendSignUpInfo(user)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("UserinfoViewModel", "Sign up response: ${response.body()}")
                        _member4005Error.value = false // 성공적인 경우에는 4005 오류 리셋
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Log.e("UserinfoViewModel", "Sign up failed: $errorMessage")
                        if (response.code() == 400 && errorMessage?.contains("MEMBER4005") == true) {
                            _member4005Error.value = true // MEMBER4005 오류 발생 시
                        } else {
                            _member4005Error.value = false // 다른 오류는 4005 오류 리셋
                            // MainActivity로 이동하는 로직을 추가할 수 있음

                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserinfoViewModel", "Error sending sign up info", e)
            }
        }
    }
}
