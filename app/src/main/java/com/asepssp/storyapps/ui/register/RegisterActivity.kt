package com.asepssp.storyapps.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.asepssp.storyapps.R
import com.asepssp.storyapps.data.database.repository.Result
import com.asepssp.storyapps.pref.ViewModelFactory
import com.asepssp.storyapps.data.remote.response.RegisterResponse
import com.asepssp.storyapps.databinding.ActivityRegisterBinding
import com.asepssp.storyapps.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        actionLoginAccount()
        actionRegisterUser()
        setupBar()

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoText, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvTitleRegis, View.ALPHA, 1f).setDuration(300)
        val desc = ObjectAnimator.ofFloat(binding.tvDescRegis, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(300)
        val etlName =
            ObjectAnimator.ofFloat(binding.inputName, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val etlEmail =
            ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val etlPassword =
            ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(300)
        val question = ObjectAnimator.ofFloat(binding.tvQuestion, View.ALPHA, 1f).setDuration(300)
        val loginAccount =
            ObjectAnimator.ofFloat(binding.tvLoginAccount, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                desc,
                name,
                etlName,
                email,
                etlEmail,
                password,
                etlPassword,
                register,
                question,
                loginAccount
            )
            start()
        }
    }

    private fun actionLoginAccount() {
        binding.apply {
            tvLoginAccount.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }
    }

    private fun actionRegisterUser() {
        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.registerAccount(name, email, password)
            viewModel.registrationResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)
                        val response: RegisterResponse = result.data
                        AlertDialog.Builder(this).apply {
                            setTitle(getText(R.string.message_register_account))
                            setMessage(response.message)
                            setPositiveButton(getText(R.string.text_ok)) { _, _ ->
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        isLoading(false)
                        val errorMessage: String = result.error
                        AlertDialog.Builder(this).apply {
                            setTitle(getText(R.string.message_error))
                            setMessage(errorMessage)
                            setPositiveButton(getText(R.string.text_ok)) { _, _ ->
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBarRegis.visibility = View.VISIBLE
            } else {
                progressBarRegis.visibility = View.GONE
            }
        }
    }

    private fun setupBar() {
        supportActionBar?.apply {
            title = getString(R.string.bar_register_title)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEC10E")))
        }
    }
}