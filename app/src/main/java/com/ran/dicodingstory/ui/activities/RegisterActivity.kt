package com.ran.dicodingstory.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.ran.dicodingstory.R
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.databinding.ActivityRegisterBinding
import com.ran.dicodingstory.ui.models.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by  lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        with(binding) {
            tvLogin.setOnClickListener {
                finish()
            }
            rsButton.setOnClickListener {
                val email = edRegisterEmail.text.toString()
                val name = edRegisterName.text.toString()
                val password = edRegisterPassword.text.toString()
                if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    edRegisterEmail.error = getString(R.string.email_is_required)
                    edRegisterName.error = getString(R.string.name_is_required)
                    edRegisterPassword.error = getString(R.string.password_is_required)
                    Toast.makeText(this@RegisterActivity, getString(R.string.please_fill_all_the_fields), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.registerUser(email, name, password).observe(this@RegisterActivity) {
                        if (it != null) {
                            when(it) {
                                is Result.Error -> {
                                    Toast.makeText(this@RegisterActivity, it.error, Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE
                                }
                                is Result.Loading -> {
                                    progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    Toast.makeText(this@RegisterActivity, it.data.toString(), Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}