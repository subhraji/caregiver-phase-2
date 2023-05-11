package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityJobStartAlertBinding
import com.example.caregiverphase2.databinding.ActivityLocationConfirmBinding
import com.ncorti.slidetoact.SlideToActView

class LocationConfirmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLocationConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //swipe
        binding.slideToConfBtn.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                binding.slideToConfBtn.resetSlider()
            }
        })

        binding.slideToConfBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
            override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideCompleteAnimationEnded",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideCompleteAnimationStarted",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideResetAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@UpcommingJobDetailsActivity,"onSlideResetAnimationEnded",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideResetAnimationStarted(view: SlideToActView) {
                finish()
            }
        })
    }
}