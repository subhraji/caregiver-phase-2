package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityAddBankAccountBinding
import com.example.caregiverphase2.databinding.ActivityEarningsBinding
import showKeyboard

class AddBankAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBankAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBankAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //validation
        routingNumberListener()
        accountNumberListener()
        conAccountNumberFocusListener()
        bankNameFocusListener()

        binding.clearBtn.setOnClickListener {
            finish()
        }

        binding.addBankBtn.setOnClickListener {
            val validRoute = binding.routingNumberTxtLay.helperText == null && binding.routingNumberTxt.text.toString().isNotEmpty()
            val validAccountNumber = binding.accountNumberTxtLay.helperText == null && binding.accountNumberTxt.text.toString().isNotEmpty()
            val validConAccount = binding.conAccountNumberTxtLay.helperText == null && binding.conAccountNumberTxt.text.toString().isNotEmpty()
            val validBankName = binding.bankNameTxtLay.helperText == null && binding.bankNameTxt.text.toString().isNotEmpty()
            if(validRoute){
                if(validAccountNumber){
                    if(validConAccount){
                        if(validBankName){
                            Toast.makeText(this,"Bank details added successfully.", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            if(binding.bankNameTxtLay.helperText == null) binding.bankNameTxtLay.helperText = "required"
                            Toast.makeText(this,binding.bankNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                            binding.bankNameTxt.showKeyboard()
                        }
                    }else{
                        if(binding.conAccountNumberTxtLay.helperText == null) binding.conAccountNumberTxtLay.helperText = "required"
                        Toast.makeText(this,binding.conAccountNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                        binding.conAccountNumberTxt.showKeyboard()
                    }
                }else{
                    if(binding.accountNumberTxtLay.helperText == null) binding.accountNumberTxtLay.helperText = "required"
                    Toast.makeText(this,binding.accountNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                    binding.accountNumberTxt.showKeyboard()
                }
            }else{
                if(binding.routingNumberTxtLay.helperText == null) binding.routingNumberTxtLay.helperText = "required"
                Toast.makeText(this,binding.routingNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.routingNumberTxt.showKeyboard()
            }
        }
    }

    private fun routingNumberListener(){
        binding.routingNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.routingNumberTxtLay.helperText = validRouteNumber()
        }
    }

    private fun validRouteNumber(): String? {
        val mobileText = binding.routingNumberTxt.text.toString()
        if(mobileText.length != 9){
            return "9 digit number required."
        }

        if(binding.routingNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid route number."
        }

        return  null
    }

    private fun accountNumberListener(){
        binding.accountNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.accountNumberTxtLay.helperText = validAccountNumber()
        }
    }

    private fun validAccountNumber(): String? {
        val mobileText = binding.accountNumberTxt.text.toString()
        if(mobileText.isEmpty()){
            return "required"
        }

        if(binding.accountNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid account number."
        }

        return  null
    }

    private fun conAccountNumberFocusListener() {
        binding.conAccountNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.conAccountNumberTxtLay.helperText = validConAccount()
        }
    }
    private fun validConAccount(): String? {
        val new_text = binding.accountNumberTxt.text.toString()
        val con_text = binding.conAccountNumberTxt.text.toString()

        if(con_text != new_text){
            return "Account number mismatch."
        }
        return  null
    }

    private fun bankNameFocusListener(){
        binding.bankNameTxt.doOnTextChanged { text, start, before, count ->
            binding.bankNameTxtLay.helperText = validBankName()
        }
    }

    private fun validBankName(): String? {
        val nameText = binding.bankNameTxt.text.toString()

        if(nameText.isEmpty()){
            return "Provide bank name."
        }
        return null
    }
}