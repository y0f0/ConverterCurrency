package com.example.converter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val dataArray = arrayOf("RUB", "USD", "EUR", "GBP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataArray)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent()
            intent.putExtra("name", "${position}")
            setResult(1, intent)
            finish()
        }
    }
}