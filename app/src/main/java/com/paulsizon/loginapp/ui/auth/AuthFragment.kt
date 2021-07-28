package com.paulsizon.loginapp.ui.auth

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.paulsizon.loginapp.R
import com.paulsizon.loginapp.other.Status
import com.paulsizon.loginapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        subscribeObservers()

        btnRegister.setOnClickListener {
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            val confirmedPassword = etRegisterPasswordConfirm.text.toString()
            viewModel.register(email, password, confirmedPassword)
        }


    }

    private fun subscribeObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully registered an account")
                    }
                    Status.ERROR -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}
