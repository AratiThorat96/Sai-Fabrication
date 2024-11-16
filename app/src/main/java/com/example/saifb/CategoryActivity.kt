package com.example.saifb

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Find GridView from layout
        val gridView: GridView = findViewById(R.id.myGrid)

        // Set adapter
        gridView.adapter = ImageAdapter(this)
    }
}
