package com.asepssp.storyapps.ui.login

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
import com.asepssp.storyapps.data.remote.response.LoginResponse
import com.asepssp.storyapps.databinding.ActivityLoginBinding
import com.asepssp.storyapps.pref.ViewModelFactory
import com.asepssp.storyapps.ui.main.MainActivity
import com.asepssp.storyapps.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        playAnimation()
        actionRegisterAccount()
        actionLoginUser()
        setupBar()

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoText, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val title =
            ObjectAnimator.ofFloat(binding.tvTitleLogin, View.ALPHA, 1f).setDuration(300)
        val message =
            ObjectAnimator.ofFloat(binding.tvDescLogin, View.ALPHA, 1f).setDuration(300)
        val email =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val etlEmail =
            ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val etlPassword =
            ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(300)
        val question =
            ObjectAnimator.ofFloat(binding.tvQuestion, View.ALPHA, 1f).setDuration(300)
        val createAccount =
            ObjectAnimator.ofFloat(binding.tvCreateAccount, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                email,
                etlEmail,
                password,
                etlPassword,
                login,
                question,
                createAccount
            )
            start()
        }
    }

    private fun actionRegisterAccount() {
        binding.apply {
            tvCreateAccount.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun actionLoginUser() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.loginAccount(email, password)
            viewModel.loginResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }

                    is Result.Success -> {
                        isLoading(false)
                        val response: LoginResponse = result.data
                        AlertDialog.Builder(this).apply {
                            setMessage(response.message)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
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
                            show()
                            create()
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupBar() {
        supportActionBar?.apply {
            title = getString(R.string.bar_login_title)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#FEC10E")))
        }
    }
}