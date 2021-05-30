package io.keepcoding.eh_ho.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.repository.Repository
import java.util.regex.Pattern


class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _state: MutableLiveData<State> =
        MutableLiveData<State>().apply { postValue(State.SignIn) }
    private val _signInData = MutableLiveData<SignInData>().apply { postValue(SignInData("", "")) }
    private val _signUpData =
        MutableLiveData<SignUpData>().apply { postValue(SignUpData("", "", "", "")) }
    private val _loginErrorEvent = MutableLiveData<LoginErrorEvent>()
    private val _requestErrorEvent = MutableLiveData<RequestErrorEvent>()
    private val _signInLoadingEvent = MutableLiveData<SignInLoadingEvent>()
    private val _signUpLoadingEvent = MutableLiveData<SignUpLoadingEvent>()


    val state: LiveData<State> = _state
    val signInData: LiveData<SignInData> = _signInData
    val signUpData: LiveData<SignUpData> = _signUpData
    val loginErrorEvent: LiveData<LoginErrorEvent> = _loginErrorEvent
    val requestErrorEvent: LiveData<RequestErrorEvent> = _requestErrorEvent
    val signInLoadingEvent: LiveData<SignInLoadingEvent> = _signInLoadingEvent
    val signUpLoadingEvent: LiveData<SignUpLoadingEvent> = _signUpLoadingEvent


    val signInEnabled: LiveData<Boolean> =
        Transformations.map(_signInData) { it?.isValid() ?: false }
    val signUpEnabled: LiveData<Boolean> =
        Transformations.map(_signUpData) { it?.isValid() ?: false }
    val loading: LiveData<Boolean> = Transformations.map(_state) {
        when (it) {
            State.SignIn,
            State.SignedIn,
            State.SignUp,
            State.SignedUp -> false
            State.SigningIn,
            State.SigningUp -> true
        }
    }

    fun onNewSignInUserName(userName: String) {
        onNewSignInData(_signInData.value?.copy(userName = userName))
    }

    fun onNewSignInPassword(password: String) {
        onNewSignInData(_signInData.value?.copy(password = password))
    }

    fun onNewSignUpUserName(userName: String) {
        onNewSignUpData(_signUpData.value?.copy(userName = userName))
    }

    fun onNewSignUpEmail(email: String) {
        onNewSignUpData(_signUpData.value?.copy(email = email))
    }

    fun onNewSignUpPassword(password: String) {
        onNewSignUpData(_signUpData.value?.copy(password = password))
    }

    fun onNewSignUpConfirmPassword(confirmPassword: String) {
        onNewSignUpData(_signUpData.value?.copy(confirmPassword = confirmPassword))
    }

    private fun onNewSignInData(signInData: SignInData?) {
        signInData?.takeUnless { it == _signInData.value }?.let(_signInData::postValue)
    }

    private fun onNewSignUpData(signUpData: SignUpData?) {
        signUpData?.takeUnless { it == _signUpData.value }?.let(_signUpData::postValue)
    }

    fun moveToSignIn() {
        _state.postValue(State.SignIn)
    }

    fun moveToSignUp() {
        _state.postValue(State.SignUp)
    }


    //Controlar errores
    fun signIn() {
        signInData.value?.takeIf { it.isValid() }?.let {
            _signInLoadingEvent.postValue(SignInLoadingEvent.StartLoadingEvent)

            // Validacion de tamaño de nombre de usuario
            if (validateUserNameLength(it.userName)) {
                _loginErrorEvent.postValue(LoginErrorEvent.UsernameError)
                return
            }

            // Validacion de alfanumericos de nombre de usuario
            if (!validateUserNamePattern(it.userName)) {
                _loginErrorEvent.postValue(LoginErrorEvent.AlphanumericError)
                return
            }

            // Validacion de tamaño de contraseña
            if (validatePasswordLength(it.password)) {
                _loginErrorEvent.postValue(LoginErrorEvent.PasswordError)
                return
            }

            // Validacion de Dígitos, minúsculas y mayúsculas y símbolos de password
            if (!validatePasswordPattern(it.password)) {
                Log.d("password", it.password)
                _loginErrorEvent.postValue(LoginErrorEvent.PasswordCharacterTypeError)
                return
            }

            _loginErrorEvent.postValue(LoginErrorEvent.ClearError)

            repository.signIn(it.userName, it.password) { login ->
                _signInLoadingEvent.postValue(SignInLoadingEvent.StopLoadingEvent)
                if (login is LogIn.Success) {
                    _state.postValue(State.SignedIn)
                } else {
                    _requestErrorEvent.postValue(RequestErrorEvent.SignInErrorEvent)
                }
            }
        }
    }

    fun signUp() {

        signUpData.value?.takeIf { it.isValid() }?.let {
            _signUpLoadingEvent.postValue(SignUpLoadingEvent.StartLoadingEvent)

            // Validacion de email
            if (!validateEmail(it.email)) {
                _loginErrorEvent.postValue(LoginErrorEvent.EmailError)
                return
            }

            // Validacion de tamaño de nombre de usuario
            if (validateUserNameLength(it.userName)) {
                _loginErrorEvent.postValue(LoginErrorEvent.UsernameError)
                return
            }

            // Validacion de alfanumericos de nombre de usuario
            if (!validateUserNamePattern(it.userName)) {
                _loginErrorEvent.postValue(LoginErrorEvent.AlphanumericError)
                return
            }

            // Validacion de tamaño de contraseña
            if (validatePasswordLength(it.password)) {
                _loginErrorEvent.postValue(LoginErrorEvent.PasswordError)
                return
            }

            // Validacion de Dígitos, minúsculas y mayúsculas y símbolos de password
            if (!validatePasswordPattern(it.password)) {
                _loginErrorEvent.postValue(LoginErrorEvent.PasswordCharacterTypeError)
                return
            }

            // Validacion confirmar contraseña
            if (it.password != it.confirmPassword) {
                _loginErrorEvent.postValue(LoginErrorEvent.ConfirmPasswordError)
                return
            }

            _loginErrorEvent.postValue(LoginErrorEvent.ClearError)

            repository.signup(it.userName, it.email, it.password) { login ->
                _signUpLoadingEvent.postValue(SignUpLoadingEvent.StopLoadingEvent)
                if (login is LogIn.Success) {
                    _state.postValue(State.SignedUp)
                } else {
                    _requestErrorEvent.postValue(RequestErrorEvent.SignUpnErrorEvent)
                }
            }
        }
    }

    private fun validateUserNameLength(userName: String): Boolean = userName.length <= 5
    private fun validateUserNamePattern(userName: String): Boolean =
        Pattern.matches("^[a-zA-Z]*$", userName)

    private fun validatePasswordLength(password: String): Boolean = password.length <= 8
    private fun validatePasswordPattern(password: String): Boolean {
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
        return Pattern.matches(passwordRegex, password)
    }

    private fun validateEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches();

    sealed class State {
        object SignIn : State()
        object SigningIn : State()
        object SignedIn : State()
        object SignUp : State()
        object SigningUp : State()
        object SignedUp : State()
    }

    sealed class SignInLoadingEvent {
        object StartLoadingEvent : SignInLoadingEvent()
        object StopLoadingEvent : SignInLoadingEvent()
    }

    sealed class SignUpLoadingEvent {
        object StartLoadingEvent : SignUpLoadingEvent()
        object StopLoadingEvent : SignUpLoadingEvent()
    }

    sealed class LoginErrorEvent {
        object UsernameError : LoginErrorEvent()
        object AlphanumericError : LoginErrorEvent()
        object PasswordError : LoginErrorEvent()
        object PasswordCharacterTypeError : LoginErrorEvent()
        object EmailError : LoginErrorEvent()
        object ConfirmPasswordError : LoginErrorEvent()
        object ClearError : LoginErrorEvent()
    }

    sealed class RequestErrorEvent {
        object SignInErrorEvent : RequestErrorEvent()
        object SignUpnErrorEvent : RequestErrorEvent()
    }

    data class SignInData(
        val userName: String,
        val password: String,
    )

    data class SignUpData(
        val email: String,
        val userName: String,
        val password: String,
        val confirmPassword: String,
    )

    class LoginViewModelProviderFactory(private val repository: Repository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            else -> throw IllegalArgumentException("LoginViewModelFactory can only create instances of the LoginViewModel")
        }
    }
}

private fun LoginViewModel.SignInData.isValid(): Boolean =
    userName.isNotBlank() && password.isNotBlank()

private fun LoginViewModel.SignUpData.isValid(): Boolean = userName.isNotBlank() &&
        email.isNotBlank() &&
        password == confirmPassword &&
        password.isNotBlank()
