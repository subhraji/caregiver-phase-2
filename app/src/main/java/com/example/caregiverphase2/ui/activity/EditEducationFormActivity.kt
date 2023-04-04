package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityEditEducationBinding
import com.example.caregiverphase2.databinding.ActivityEditEducationFormBinding
import com.example.caregiverphase2.model.repository.Outcome
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.viewmodel.AddEducationViewModel
import com.example.caregiverphase2.viewmodel.EditEducationViewModel
import dagger.hilt.android.AndroidEntryPoint
import isConnectedToInternet
import loadingDialog
import showKeyboard

@AndroidEntryPoint
class EditEducationFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEducationFormBinding

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mEditEducationViewModel: EditEducationViewModel by viewModels()
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditEducationFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            val institute = intent?.getStringExtra("institute")!!
            val subject = intent?.getStringExtra("subject")!!
            val start_year = intent?.getStringExtra("start")!!
            val end_year = intent?.getStringExtra("end")!!
            id = intent?.getStringExtra("id")!!

            binding.schoolNameTxt.text = Editable.Factory.getInstance().newEditable(institute)
            binding.subjectTxt.text = Editable.Factory.getInstance().newEditable(subject)
            binding.startYearTxt.text = Editable.Factory.getInstance().newEditable(start_year)
            binding.endYearTxt.text = Editable.Factory.getInstance().newEditable(end_year)
        }

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        //validation
        schoolNameFocusListener()
        subjectNameFocusListener()
        startYearFocusListener()
        endYearFocusListener()

        //observe
        editEduObserver()

        binding.addBtn.setOnClickListener {
            val validSchoolName = binding.schoolNameTxtLay.helperText == null && binding.schoolNameTxt.text.toString().isNotEmpty()
            val validDegreeName = binding.subjectTxtLay.helperText == null && binding.subjectTxt.text.toString().isNotEmpty()
            val validStartYear = binding.startYearTxtLay.helperText == null && binding.startYearTxt.text.toString().isNotEmpty()
            val validEndYear = binding.endYearTxtLay.helperText == null && binding.endYearTxt.text.toString().isNotEmpty()

            if(validSchoolName){
                if(validDegreeName){
                    if(validStartYear){
                        if(validEndYear){
                            if(binding.endYearTxt.text.toString().toInt() - binding.startYearTxt.text.toString().toInt() >= 0){
                                if(isConnectedToInternet()){
                                    mEditEducationViewModel.editEducation(
                                        binding.schoolNameTxt.text.toString(),
                                        binding.subjectTxt.text.toString(),
                                        binding.startYearTxt.text.toString(),
                                        binding.endYearTxt.text.toString(),
                                        id!!,
                                        accessToken
                                    )
                                    loader.show()
                                }else{
                                    Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"Invalid end year.", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            if(binding.endYearTxtLay.helperText == null) binding.endYearTxtLay.helperText = "required"
                            binding.endYearTxt.showKeyboard()
                        }
                    }else{
                        if(binding.startYearTxtLay.helperText == null) binding.startYearTxtLay.helperText = "required"
                        binding.startYearTxt.showKeyboard()
                    }
                }else{
                    if(binding.subjectTxtLay.helperText == null) binding.subjectTxtLay.helperText = "required"
                    binding.subjectTxt.showKeyboard()
                }
            }else{
                if(binding.schoolNameTxtLay.helperText == null) binding.schoolNameTxtLay.helperText = "required"
                binding.schoolNameTxt.showKeyboard()
            }

        }

    }

    private fun schoolNameFocusListener(){
        binding.schoolNameTxt.doOnTextChanged { text, start, before, count ->
            binding.schoolNameTxtLay.helperText = validSchoolName()
        }
    }

    private fun validSchoolName(): String? {
        val nameText = binding.schoolNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide school name."
        }
        return null
    }

    private fun subjectNameFocusListener(){
        binding.subjectTxt.doOnTextChanged { text, start, before, count ->
            binding.subjectTxtLay.helperText = validSubjectName()
        }
    }

    private fun validSubjectName(): String? {
        val nameText = binding.subjectTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide subject name."
        }
        return null
    }

    private fun startYearFocusListener(){
        binding.startYearTxt.doOnTextChanged { text, start, before, count ->
            binding.startYearTxtLay.helperText = validStartYear()
        }
    }

    private fun validStartYear(): String? {
        val nameText = binding.startYearTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide start year."
        }
        return null
    }

    private fun endYearFocusListener(){
        binding.endYearTxt.doOnTextChanged { text, start, before, count ->
            binding.endYearTxtLay.helperText = validEndYear()
        }
    }

    private fun validEndYear(): String? {
        val nameText = binding.endYearTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide end year."
        }
        return null
    }

    private fun editEduObserver(){
        mEditEducationViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_LONG).show()
                        mEditEducationViewModel.navigationComplete()
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

}