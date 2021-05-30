package io.keepcoding.eh_ho.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.common.TextChangedWatcher
import io.keepcoding.eh_ho.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private val vm: LoginViewModel by activityViewModels()
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSignUpBinding.inflate(inflater, container, false).apply {
        binding = this
        labelSignIn.setOnClickListener {
            vm.moveToSignIn()
        }

        vm.loginErrorEvent.observe(viewLifecycleOwner) {
            clearError()
            when (it) {
                is LoginViewModel.LoginErrorEvent.UsernameError -> setError(
                    usernameLayout,
                    getString(R.string.username_character_error)
                )
                is LoginViewModel.LoginErrorEvent.PasswordError -> setError(
                    passLayout,
                    getString(R.string.password_character_error)
                )
                is LoginViewModel.LoginErrorEvent.AlphanumericError -> setError(
                    usernameLayout,
                    getString(R.string.username_alphanumeric_error)
                )
                is LoginViewModel.LoginErrorEvent.PasswordCharacterTypeError -> setError(
                    passLayout,
                    getString(R.string.password_character_type_error)
                )
                is LoginViewModel.LoginErrorEvent.EmailError -> setError(
                    emailLayout,
                    getString(R.string.email_error)
                )
                is LoginViewModel.LoginErrorEvent.ConfirmPasswordError -> setError(
                    confirmPassLayout,
                    getString(R.string.confirm_pass_error)
                )
                LoginViewModel.LoginErrorEvent.ClearError -> clearError()
            }
        }

        vm.requestErrorEvent.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.RequestErrorEvent.SignUpnErrorEvent -> showToastError()
                else -> {
                }
            }
        }

        vm.signUpData.observe(viewLifecycleOwner) {
            inputEmail.apply {
                setText(it.email)
                setSelection(it.email.length)
            }
            inputUsername.apply {
                setText(it.userName)
                setSelection(it.userName.length)
            }
            inputPassword.apply {
                setText(it.password)
                setSelection(it.password.length)
            }
            inputConfirmPassword.apply {
                setText(it.confirmPassword)
                setSelection(it.confirmPassword.length)
            }
        }
        vm.signUpLoadingEvent.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.SignUpLoadingEvent.StartLoadingEvent -> progress.isVisible = true
                is LoginViewModel.SignUpLoadingEvent.StopLoadingEvent -> progress.isVisible = false
            }

        }
        vm.signUpEnabled.observe(viewLifecycleOwner) {
            buttonSignUp.isEnabled = it
        }
        inputEmail.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpEmail))
        }
        inputUsername.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpUserName))
        }
        inputPassword.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpPassword))
        }
        inputConfirmPassword.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpConfirmPassword))
        }
        buttonSignUp.setOnClickListener {
            println("JcLog: clicking signup button")
            vm.signUp()
        }
    }.root

    companion object {
        fun newInstance(): SignUpFragment = SignUpFragment()
    }

    private fun setError(layout: TextInputLayout, text: String) {
        binding.progress.isVisible = false
        layout.error = text
    }

    private fun clearError() {
        binding.usernameLayout.error = null
        binding.passLayout.error = null
        binding.emailLayout.error = null
        binding.confirmPassLayout.error = null
    }

    private fun showToastError() {
        binding.progress.isVisible = false
        Toast.makeText(requireContext(), getString(R.string.request_error_text), Toast.LENGTH_LONG)
            .show()
    }
}