package com.example.caregiverphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caregiverphase2.R
import com.example.caregiverphase2.adapter.BulletPointAdapter
import com.example.caregiverphase2.adapter.CheckListAdapter
import com.example.caregiverphase2.databinding.ActivityOnGoingJobDetailsBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.ui.fragment.AgencyFragment
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.AddReviewViewModel
import com.example.caregiverphase2.viewmodel.CompleteJobViewModel
import com.example.caregiverphase2.viewmodel.GetOngoingJobViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ncorti.slidetoact.SlideToActView
import dagger.hilt.android.AndroidEntryPoint
import gone
import isConnectedToInternet
import loadingDialog
import visible
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
class OnGoingJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnGoingJobDetailsBinding
    private val mGetOngoingJobViewModel: GetOngoingJobViewModel by viewModels()
    private val mCompleteJobViewModel: CompleteJobViewModel by viewModels()
    private val mAddReviewViewModel: AddReviewViewModel by viewModels()

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private var checkList:MutableList<String> = mutableListOf()
    private var job_id: Int = 0
    private var lat: String = ""
    private var long: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOnGoingJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.medicalRecycler.gone()
        binding.medicalHisHtv.gone()
        binding.jobExpRecycler.gone()
        binding.jobExpHtv.gone()
        binding.otherReqRecycler.gone()
        binding.otherReqHtv.gone()
        binding.noCheckListTv.gone()
        binding.checkListRecycler.gone()
        binding.getDirectionBtn.gone()

        clickJobOverview()

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }
        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.viewProfileHtv.setOnClickListener {
            /*val intent = Intent(this,AgencyProfileActivity::class.java)
            intent.putExtra("id",job_id.toString())
            startActivity(intent)*/

            val addPhotoBottomDialogFragment: AgencyFragment =
                AgencyFragment.newInstance()
            val bundle = Bundle()
            bundle.putString("id", job_id.toString())
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(
                supportFragmentManager,
                "agency_profile_fragment"
            )
        }

        /*binding.rewardsRelay.setOnClickListener {
            showReviewDialog()
        }*/

        //swipe
        binding.slideToCompleteBtn.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                binding.slideToCompleteBtn.resetSlider()
            }
        })

        binding.slideToCompleteBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
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
                if(!checkList.isEmpty()){
                    showCheckListBottomsheet()
                }else{
                    // complete api call
                    mCompleteJobViewModel.completeJob(job_id,accessToken)
                    loader.show()
                }
            }

        })

        //observer
        getOngoingJobObserver()
        completeJobObserver()
        addReviewObserver()

        if(isConnectedToInternet()){
            binding.mainLay.gone()
            binding.detailsShimmerView.visible()
            binding.detailsShimmerView.startShimmer()
            mGetOngoingJobViewModel.getOngoingJob(accessToken)
        }else{
            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
        }

        binding.getDirectionBtn.setOnClickListener {
            if(!lat.isEmpty() && !long.isEmpty()){
                getDirection(lat, long)
            }else{
                Toast.makeText(this,"Could not fetch the location ${lat}...${long}",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getOngoingJobObserver(){
        mGetOngoingJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data!!.data.isNotEmpty()){
                            binding.mainLay.visible()
                            binding.detailsShimmerView.gone()
                            binding.detailsShimmerView.stopShimmer()

                            job_id = outcome.data!!.data[0].job_id

                            var gen = ""
                            for(i in outcome.data!!.data[0].care_items){
                                if(gen.isEmpty()){
                                    gen = i.gender+": "+i.age
                                }else{
                                    gen = gen+", "+i.gender+": "+i.age
                                }
                            }
                            binding.ageTv.text = gen
                            binding.titleTv.text = outcome.data!!.data[0].title
                            binding.careTypeTv.text = outcome.data!!.data[0].care_type
                            binding.locTv.text = outcome.data!!.data[0].address
                            binding.dateTv.text = outcome.data!!.data[0].start_date.toString()+" to "+outcome.data!!.data[0].end_date.toString()
                            binding.timeTv.text = outcome.data!!.data[0].start_time.toString()+" - "+outcome.data!!.data[0].end_time.toString()
                            binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                            binding.agencyNameTv.text = outcome.data!!.data[0].agency_name.toString()
                            binding.jobDescTv.text = outcome.data!!.data[0].description.toString()

                            Glide.with(this)
                                .load(Constants.PUBLIC_URL+outcome.data!!.data[0].agency_photo) // image url
                                .placeholder(R.color.dash_yellow) // any placeholder to load at start
                                .centerCrop()
                                .into(binding.agencyImgView)

                            if(outcome.data!!.data[0].medical_history.isNotEmpty()){
                                binding.medicalRecycler.visible()
                                binding.medicalHisHtv.visible()
                                medicalHistoryFillRecycler(outcome.data!!.data[0].medical_history.toMutableList())
                            }
                            outcome.data!!.data[0].expertise?.let {
                                if(outcome.data!!.data[0].expertise.isNotEmpty()){
                                    binding.jobExpRecycler.visible()
                                    binding.jobExpHtv.visible()
                                    jobExpFillRecycler(outcome.data!!.data[0].expertise.toMutableList())
                                }
                            }
                            if(outcome.data!!.data[0].other_requirements.isNotEmpty()){
                                binding.otherReqRecycler.visible()
                                binding.otherReqHtv.visible()
                                otherFillRecycler(outcome.data!!.data[0].other_requirements.toMutableList())
                            }
                            if(outcome.data!!.data[0].check_list.isNotEmpty()){
                                binding.checkListRecycler.visible()
                                binding.noCheckListTv.gone()
                                checkListFillRecycler(outcome.data!!.data[0].check_list.toMutableList())
                                checkList = outcome.data!!.data[0].check_list.toMutableList()
                            }else{
                                binding.noCheckListTv.visible()
                            }

                            outcome.data!!.data[0].lat?.let {
                                outcome.data!!.data[0].long?.let {
                                    binding.getDirectionBtn.visible()
                                    lat = outcome.data!!.data[0].lat
                                    long = outcome.data!!.data[0].long
                                }
                            }

                        }else{
                            binding.mainLay.gone()
                            binding.detailsShimmerView.visible()
                            binding.detailsShimmerView.startShimmer()
                        }
                        mGetOngoingJobViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }
    private fun medicalHistoryFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.medicalRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@OnGoingJobDetailsActivity)
        }
    }

    private fun showCheckListBottomsheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.check_list_bottom_sheet_layout, null)

        val checkRecycler = view.findViewById<RecyclerView>(R.id.check_recycler)
        val submit = view.findViewById<CardView>(R.id.submit_btn)

        submit.setOnClickListener {
            mCompleteJobViewModel.completeJob(job_id,accessToken)
            loader.show()
            dialog.dismiss()
        }

        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        checkRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = CheckListAdapter(checkList,this@OnGoingJobDetailsActivity)
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showCompleteDialog(title: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.job_accept_success_dialog_layout)

        val okay = dialog.findViewById<TextView>(R.id.ok_btn)
        val title_tv = dialog.findViewById<TextView>(R.id.text_view_2)

        title_tv.text = title
        /*Handler(Looper.getMainLooper()).postDelayed({

        }, 2500)*/
        okay.setOnClickListener {
            showReviewDialog()
            dialog.dismiss()
            //finish()
        }

        dialog.show()
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

    private fun completeJobObserver(){
        mCompleteJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        showCompleteDialog("Your job has been completed successfully, You can find this on the completed job section.")
                        mCompleteJobViewModel.navigationComplete()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun addReviewObserver(){
        mAddReviewViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mAddReviewViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getDirection(lat: String, long: String){
        val gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+long+ "&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun getDurationHour(startDateTime: String, endDateTime: String): Long {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var durationTotalMin = 0

        try {
            val d1: Date = sdf.parse(startDateTime)
            val d2: Date = sdf.parse(endDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            val durationDay = difference_In_Days.toInt()
            val durationHour = difference_In_Hours.toInt()

            durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()
        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }

        return durationTotalMin.toLong()*60*1000
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd h:mm a"
        val outputPattern = "dd-MM-yyyy HH:mm:ss"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    private fun setTimer(duration: Long){
        object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var millisUntilFinished = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val elapsedHours = millisUntilFinished / hoursInMilli
                millisUntilFinished = millisUntilFinished % hoursInMilli
                val elapsedMinutes = millisUntilFinished / minutesInMilli
                millisUntilFinished = millisUntilFinished % minutesInMilli
                val elapsedSeconds = millisUntilFinished / secondsInMilli
                val yy = String.format("%02d:%02d:%2d", elapsedHours, elapsedMinutes, elapsedSeconds)
                binding.timeTv.setText(yy)
            }

            override fun onFinish() {
                binding.timeTv.setText("00:00:00")
            }
        }.start()
    }

    private fun showReviewDialog(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.add_review_bottomsheet_layout, null)

        val submit = view.findViewById<TextView>(R.id.submit_btn)
        val reviewTxt = view.findViewById<EditText>(R.id.review_txt)
        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)

        submit.setOnClickListener {
            val rating = ratingBar.rating.toString()
            val review = reviewTxt.text.toString()
            if(!rating.isEmpty()){
                if(!review.isEmpty()){
                    mAddReviewViewModel.addReview(
                        job_id.toString(),
                        rating.toString(),
                        review.toString(),
                        accessToken
                    )
                    loader.show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(this,"Please provide your review.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Rating is missing.",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

}