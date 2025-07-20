package com.example.bunkerpro_10

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvResult = findViewById<TextView>(R.id.tvresult)
        val etTotal =  findViewById<EditText>(R.id.etTotal)
        val etPresent = findViewById<EditText>(R.id.etPresent)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        val percent = listOf("Select Percentage","60","65","70","75","80","85","90","95","100")
        spinner.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,percent)

        btnCalculate.setOnClickListener {
            val present = etPresent.text.toString().toIntOrNull()
            val total = etTotal.text.toString().toIntOrNull()
            val attenPercentstr = spinner.selectedItem.toString()

            if (attenPercentstr == "Select Percentage"){
                tvResult.text = "please select a valid percentage"
                return@setOnClickListener
            }

            val attenPercent = attenPercentstr.toIntOrNull()

            if (present == null || total == null || attenPercent == null){
                tvResult.text = "Proper values please"
                return@setOnClickListener
            }
            if (present < 0 || total <= 0 || present > total){
                tvResult.text = "Proper values please"
                return@setOnClickListener
            }

            val currentAttendance = (present.toDouble() / total) * 100

            if(currentAttendance >= attenPercent){
                val  daysAvailable = noOfBunks(present,total,attenPercent)
                tvResult.text = daysToBunkText(daysAvailable,present,total)
            }else{
                val needed = attendanceReq(present,total,attenPercent)
                tvResult.text = daysToAttendText(needed,present,total,attenPercent)
            }

        }

    }

    private fun attendanceReq(present : Int, total : Int, percentage : Int):Int{
        return ceil((percentage * total - 100.0 / present) / (100 - percentage)).toInt()
    }

    private fun noOfBunks (present: Int, total: Int ,percentage: Int):Int{
        return  floor((100.0 * present - percentage * total) / percentage).toInt()
    }

    private  fun daysToBunkText (days : Int, present: Int, total: Int):String{
        val newTotal = total + days
        val currentPresent = (present * 100.0) / total
        val newPresent = (present * 100.0) / newTotal

        return "You can bunk $days more days.\n"+
                "Current: $present/$total - ${currentPresent.toInt()}%\n"+
                "After bunking: $present/$newTotal - ${newPresent.toInt()}%"
    }

    private fun daysToAttendText (needed : Int, present: Int, total: Int, percentage: Int):String{
        val newPresent = present + needed
        val newTotal = total + needed
        val currentPercent = (present * 100.0) / total
        val newPercent = (present * 100.0 ) / newTotal

        return "You need to attend $needed more class to reach $percentage%. \n"+
                "Current: $present/$total - ${currentPercent.toInt()}%"+
                "After attending: $newPercent/$newTotal - ${newPresent.toInt()}%"
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}