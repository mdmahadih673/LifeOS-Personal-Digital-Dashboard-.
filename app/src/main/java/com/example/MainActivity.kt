package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.LifeViewModel
import com.example.ui.screens.LifeOSMainApp

class MainActivity : ComponentActivity() {
  private val viewModel: LifeViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LifeOSMainApp(viewModel = viewModel)
    }
  }
}

