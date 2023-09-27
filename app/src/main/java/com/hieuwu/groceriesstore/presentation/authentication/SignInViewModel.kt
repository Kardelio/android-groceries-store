package com.hieuwu.groceriesstore.presentation.authentication

import androidx.compose.runtime.MutableState
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.hieuwu.groceriesstore.BR
import com.hieuwu.groceriesstore.domain.usecases.SignInUseCase
import com.hieuwu.groceriesstore.presentation.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ObservableViewModel() {

    private val _isSignUpSuccessful = MutableSharedFlow<Boolean?>()
    val isSignUpSuccessful: SharedFlow<Boolean?> = _isSignUpSuccessful

    private val _showAccountNotExistedError = MutableSharedFlow<Boolean>()
    val showAccountNotExistedError: SharedFlow<Boolean> = _showAccountNotExistedError


    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onRemoveText() {
        _email.value = ""
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
    fun signIn() {
        viewModelScope.launch {
            val res = signInUseCase.execute(
                SignInUseCase.Input(
                    email = _email.value,
                    password = _password.value
                )
            )

            when (res) {
                is SignInUseCase.Output.Error -> {
                    //TODO Handle show general error
                }
                is SignInUseCase.Output.AccountNotExistedError -> {
                    _showAccountNotExistedError.emit(true)
                    _isSignUpSuccessful.emit(false)
                }
                else -> {
                    _isSignUpSuccessful.emit(true)
                }
            }

        }
    }
}
