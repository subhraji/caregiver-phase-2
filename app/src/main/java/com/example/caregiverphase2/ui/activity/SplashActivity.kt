package com.example.caregiverphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivitySplashBinding
import com.example.caregiverphase2.utils.PrefManager
import isConnectedToInternet
import lightStatusBar

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lightStatusBar(this, R.color.theme_blue)

        /*val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())*/

        checkInternet()
    }

    private fun checkInternet(){

        Handler(Looper.getMainLooper()).postDelayed({

            if(isConnectedToInternet()){
                if(PrefManager.getLogInStatus() == true){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, ChooseLoginRegActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else{
                /*val intent = Intent(this, NoInternetActivity::class.java)
                startActivity(intent)
                finish()*/
                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show()
            } }, 3000)

    }
}