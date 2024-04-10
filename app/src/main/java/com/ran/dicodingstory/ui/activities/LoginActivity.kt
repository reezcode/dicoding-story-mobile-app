package com.ran.dicodingstory.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ran.dicodingstory.R
import com.ran.dicodingstory.data.remote.response.LoginResult
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.databinding.ActivityLoginBinding
import com.ran.dicodingstory.ui.models.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        with(binding){
            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            btnLogin.setOnClickListener {
                proceedLogin(edLoginEmail.text.toString(), edLoginPassword.text.toString())
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun proceedLogin(email: String, password:String){
        if (email.isEmpty() || password.isEmpty()) {
            binding.edLoginEmail.error = getString(R.string.email_is_required)
            binding.edLoginPassword.error = getString(R.string.password_is_required)
            showToast(getString(R.string.please_fill_all_the_fields))
        } else {
            viewModel.login(email, password).observe(this@LoginActivity) {
                if (it != null) {
                    when(it) {
                        is Result.Error -> {
                            showToast(it.error)
                            showLoading(false)
                        }
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            val loginResult: LoginResult = it.data as LoginResult
                            loginResult.token?.let { token -> viewModel.saveToken(token) }
                            viewModel.saveSession(true)
                            showToast(getString(R.string.welcome))
                            showLoading(false)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
           if (backPressedOnce) {
                    finishAffinity()
                    exitProcess(0)
                } else {
                    backPressedOnce = true
                    Toast.makeText(this@LoginActivity,
                        getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show()
                    android.os.Handler().postDelayed({
                           backPressedOnce = false}, 2000)
                }
        }
    }
}