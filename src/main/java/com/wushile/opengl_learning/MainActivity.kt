package com.wushile.opengl_learning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wushile.opengl_learning.lesson1.Lesson1Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lesson1.setOnClickListener( View.OnClickListener {
            startActivity(Intent(MainActivity@this, Lesson1Activity::class.java))
        })
    }
}