package com.ran.dicodingstory.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ran.dicodingstory.R
import com.ran.dicodingstory.databinding.ActivityMainBinding
import com.ran.dicodingstory.ui.adapter.StoryAdapter
import com.ran.dicodingstory.ui.models.MainViewModel
import com.ran.dicodingstory.ui.models.events.MainEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initData()
        with(binding){
            btnAdd.setOnClickListener {
                startActivity(Intent(this@MainActivity, CreateActivity::class.java))
            }
            btnTryAgain.setOnClickListener {
                errorView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                initData()
            }
            btnSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))

            }
        }
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST
            )
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun initData() {
        with(binding) {
            adapter = StoryAdapter()
            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.layoutManager = layoutManager
            rvStory.adapter = adapter
            lifecycleScope.launch {
                viewModel.getStories().collect {event ->
                    when(event){
                        is MainEvent.Loading -> {
                            Toast.makeText(this@MainActivity,
                                getString(R.string.loading), Toast.LENGTH_SHORT).show()
                            viewModel.isFailed.observe(this@MainActivity) {
                                if (it) {
                                    progressBar.visibility = View.GONE
                                    errorView.visibility = View.VISIBLE
                                }
                            }
                        }
                        is MainEvent.Success -> {
                            val data = event.data
                            adapter.submitData(data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (backPressedOnce) {
                finishAffinity()
                exitProcess(0)
            } else {
                backPressedOnce = true
                Toast.makeText(this@MainActivity, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show()
                android.os.Handler().postDelayed({
                    backPressedOnce = false}, 2000)
            }
        }
    }

    companion object {
        private const val LOCATION_REQUEST = 99
    }
}