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
import io.keepcoding.eh_ho.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private val vm: LoginViewModel by activityViewModels()

    lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSignInBinding.inflate(inflater, container, false).apply {

        binding = this

        labelCreateAccount.setOnClickListener {
            vm.moveToSignUp()
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
                is LoginViewModel.LoginErrorEvent.ClearError -> clearError()
                else -> {
                }
            }
        }
        vm.signInLoadingEvent.observe(viewLifecycleOwner) {
            when(it) {
                is LoginViewModel.SignInLoadingEvent.StartLoadingEvent -> progress.isVisible = true
                is LoginViewModel.SignInLoadingEvent.StopLoadingEvent -> progress.isVisible = false
            }
        }
        vm.requestErrorEvent.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.RequestErrorEvent.SignInErrorEvent -> showToastError()
                else -> {
                }
            }
        }
        vm.signInData.observe(viewLifecycleOwner) {
            inputUsername.apply {
                setText(it.userName)
                setSelection(it.userName.length)

            }
            inputPassword.apply {
                setText(it.password)
                setSelection(it.password.length)
            }
        }
        vm.signInEnabled.observe(viewLifecycleOwner) {
            buttonLogin.isEnabled = it
        }
        inputUsername.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignInUserName))
        }
        inputPassword.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignInPassword))
        }
        buttonLogin.setOnClickListener {
            vm.signIn()
        }
    }.root

    companion object {
        fun newInstance(): SignInFragment = SignInFragment()
    }

    private fun setError(layout: TextInputLayout, text: String) {
        binding.progress.isVisible = false
        layout.error = text
    }

    private fun clearError() {
        binding.usernameLayout.error = null
        binding.passLayout.error = null
    }

    private fun showToastError() {
        binding.progress.isVisible = false
        Toast.makeText(requireContext(), getString(R.string.request_error_text), Toast.LENGTH_LONG)
            .show()
    }
}