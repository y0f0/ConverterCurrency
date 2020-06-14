package com.example.converter

import android.R.layout
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request


val GREY:Int = Color.parseColor("#E6736F6F")
val YELLOW:Int = Color.parseColor("#FFC107")
val DARK_GREY:Int = Color.parseColor("#403F3F")
val COLOR_ID_GREY = -428642449
val COLOR_ID_YELLOW = -16121
val MAX_LENGTH_INPUT:Int = 9
val MAX_LENGTH_GIVEN_COURSE:Int = 8
val currency = arrayOf("RUB", "USD", "EUR", "GBP")
val course = mutableMapOf(Pair("RUB", "USD") to 0, Pair("RUB", "EUR") to 0, Pair("RUB", "GBP") to 0, Pair("USD", "RUB") to 0, Pair("USD", "EUR") to 0,  Pair("USD", "GBP") to 0, Pair("EUR", "RUB") to 0, Pair("EUR", "USD") to 0, Pair("EUR", "GBP") to 0,  Pair("GBP", "RUB") to 0, Pair("GBP", "USD") to 0, Pair("GBP", "EUR") to 0)
//val url = "https://api.exchangeratesapi.io/latest?base="

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GivenCourse.setOnClickListener {GivenCourseChange()}
        GivenCourseText.setOnClickListener {GivenCourseChange()}
        TodaysCourse.setOnClickListener {TodaysCourseChange() }
        TodaysCourseText.setOnClickListener {TodaysCourseChange()}
        input.setOnClickListener {InputOutputChange("input")}
        output.setOnClickListener{InputOutputChange("output")}
        tvDelete.setOnClickListener{
            output.setText("0")
            input.setText("0")
        }
        tvUndo.setOnClickListener {
            val colorIdInput = (input.getBackground() as ColorDrawable).getColor()
            val colorIdOutput = (output.getBackground() as ColorDrawable).getColor()
            val colorIdGivenCourse = (GivenCourse.getBackground() as ColorDrawable).getColor()
            if (colorIdInput == YELLOW) {
                if (input.text.toString() != "0") {
                    val n: Int = (input.text.toString().toInt() / 10).toInt()
                    input.setText("${n}")
                    appendInputOutput(" ","input")
                }
            }
            if (colorIdOutput == YELLOW) {
                if (output.text.toString() != "0") {
                    val n: Int = (output.text.toString().toInt() / 10).toInt()
                    output.setText("${n}")
                    appendInputOutput(" ", "output")
                }
            }
            if (colorIdInput == DARK_GREY && colorIdOutput == DARK_GREY && colorIdGivenCourse == YELLOW) {
                if (GivenCourse.text.toString() != "0") {
                    val n: Int = (GivenCourse.text.toString().toInt() / 10).toInt()
                    GivenCourse.setText("${n}")
                    appendInputOutput(input.text.toString(), "input")
                }
            }
        }
        currency1.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivityForResult(intent, 1)
        }
        currency2.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        val position = data.getStringExtra("name")!!.toString().toInt()
        val colorIdGivenCourse = (GivenCourse.getBackground() as ColorDrawable).getColor()
        val colorIdTodaysCourse = (TodaysCourse.getBackground() as ColorDrawable).getColor()
        if (requestCode == 1) {
            currency1.setText(currency[position])
            if (colorIdGivenCourse == YELLOW) { //is active
                val pair = Pair(currency[position], currency2.text.toString())
                GivenCourse.setText("${course.get(pair)}")
                appendInputOutput(input.text.toString(), "output")
            }
        } else {
            currency2.setText(currency[position])
            if (colorIdGivenCourse == YELLOW) { //is active
                val pair = Pair(currency1.text.toString(), currency[position])
                GivenCourse.setText("${course.get(pair)}")
                appendInputOutput(output.text.toString(), "input")
            }
        }
    }
    fun appendInputOutput(number:String, type:String) {
        val colorIdGivenCourse = (GivenCourse.getBackground() as ColorDrawable).getColor()
        val colorIdTodaysCourse = (TodaysCourse.getBackground() as ColorDrawable).getColor()
        var str = if(type=="input") input.text.toString() else output.text.toString()
        val course:Int = GivenCourse.text.toString().toInt()
        if (colorIdGivenCourse == YELLOW) { //Given course is active
                if (number != " ") { //Input change
                    if (str == "0" && number == "0") {
                        str = number
                    } else if (str.length < MAX_LENGTH_INPUT && output.text.toString().length <= MAX_LENGTH_INPUT) {
                        str = if (str == "0") number else str + number
                        val n: Int = str.toInt()
                        val Text: String = if (type=="input") (n * course).toString() else (n / course).toString()
                        if (type == "input" && Text.length <= MAX_LENGTH_INPUT) {
                            output.setText(Text)
                            input.setText(str)
                        }
                        if (type == "output" && Text.length <= MAX_LENGTH_INPUT) {
                            output.setText(str)
                            input.setText(Text)
                        }
                    }
                }
                else { //just given couse change
                    val n: Int = str.toInt()
                    val Text:String = if (type=="input") (n * course).toString() else (n / course).toString()
                    if (type == "input" && Text.length <= MAX_LENGTH_INPUT) {
                        output.setText(Text)
                    }
                    if (type == "output" && Text.length <= MAX_LENGTH_INPUT) {
                        input.setText(Text)
                    }
                }
            }
    }
    fun appendGivenCourse(number:String, type:String) {
        var str = GivenCourse.text.toString()
        if (str == "0" && number == "0") {
            str = number
        } else if (str.length <= MAX_LENGTH_GIVEN_COURSE){
            str = if (str == "0") number else str + number
        }
        GivenCourse.setText(str)
        val pair = Pair(currency1.text.toString(), currency2.text.toString())
        course.put(pair, str.toInt())
        if (type != " ")
            appendInputOutput(" ", type) //uppdate input
    }
    fun InputOutputChange(type:String) {
        val colorIdInput = (input.getBackground() as ColorDrawable).getColor()
        val colorIdOutput = (output.getBackground() as ColorDrawable).getColor()
        if (type == "input" && colorIdInput == DARK_GREY){
            Log.d("TAG", "hello")
            input.setBackgroundColor(YELLOW)
            output.setBackgroundColor(DARK_GREY)
        } else if (type == "input" && colorIdInput == YELLOW){
            input.setBackgroundColor(DARK_GREY)
            output.setBackgroundColor(DARK_GREY)
        } else if (type == "output" && colorIdOutput == DARK_GREY) {
            output.setBackgroundColor(YELLOW)
            input.setBackgroundColor(DARK_GREY)
        }else if (type == "output" && colorIdOutput == YELLOW) {
            output.setBackgroundColor(DARK_GREY)
            input.setBackgroundColor(DARK_GREY)
        }
        tv0.setOnClickListener{appendInputOutput("0", type)}
        tv1.setOnClickListener{appendInputOutput("1", type)}
        tv2.setOnClickListener{appendInputOutput("2", type)}
        tv3.setOnClickListener{appendInputOutput("3", type)}
        tv4.setOnClickListener{appendInputOutput("4", type)}
        tv5.setOnClickListener{appendInputOutput("5", type)}
        tv6.setOnClickListener{appendInputOutput("6", type)}
        tv7.setOnClickListener{appendInputOutput("7", type)}
        tv8.setOnClickListener{appendInputOutput("8", type)}
        tv9.setOnClickListener{appendInputOutput("9", type)}
    }
    fun GivenCourseChange() {
        GivenCourseText.setBackgroundColor(YELLOW)
        GivenCourse.setBackgroundColor(YELLOW)
        TodaysCourse.setBackgroundColor(GREY)
        TodaysCourseText.setBackgroundColor(GREY)
        val colorIdInput = (GivenCourse.getBackground() as ColorDrawable).getColor()
        val colorIdOutput = (TodaysCourse.getBackground() as ColorDrawable).getColor()
        var type:String = " "
        if (colorIdInput == YELLOW) {
            type = "input"
        } else if (colorIdOutput == YELLOW) {
            type = "output"
        } else {
            type = " "
        }
        tv0.setOnClickListener{appendGivenCourse("0", type)}
        tv1.setOnClickListener{appendGivenCourse("1", type)}
        tv2.setOnClickListener{appendGivenCourse("2", type)}
        tv3.setOnClickListener{appendGivenCourse("3", type)}
        tv4.setOnClickListener{appendGivenCourse("4", type)}
        tv5.setOnClickListener{appendGivenCourse("5", type)}
        tv6.setOnClickListener{appendGivenCourse("6", type)}
        tv7.setOnClickListener{appendGivenCourse("7", type)}
        tv8.setOnClickListener{appendGivenCourse("8", type)}
        tv9.setOnClickListener{appendGivenCourse("9", type)}
    }

    fun TodaysCourseChange() {
        input.setBackgroundColor(DARK_GREY)
        GivenCourseText.setBackgroundColor(GREY)
        GivenCourse.setBackgroundColor(GREY)
        TodaysCourse.setBackgroundColor(YELLOW)
        TodaysCourseText.setBackgroundColor(YELLOW)
    }

}