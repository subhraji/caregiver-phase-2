package com.example.caregiverphase2.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityUpcommingJobDetailsBinding
import com.ncorti.slidetoact.SlideToActView
import gone
import visible


class UpcommingJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpcommingJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpcommingJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickJobOverview()

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }
        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        //swipe
        binding.slideToStartBtn.setOnClickListener {
        }


        binding.slideToStartBtn.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                binding.slideToStartBtn.resetSlider()
            }
        })

        binding.slideToStartBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
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
                Toast.makeText(this@UpcommingJobDetailsActivity,"Calling api...",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.theme_blue)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()

    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.theme_blue)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.theme_blue))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }

}