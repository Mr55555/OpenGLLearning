package com.wushile.opengl_learning.lesson1

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.wushile.opengl_learning.R
import kotlinx.android.synthetic.main.activity_lesson1.*

class Lesson1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson1)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            val x = event.x
            val xx = x * 2 / 3840 - 1
            val y = event.y
            val yy = -y * 2 / 2160 + 1
            my_texture_view.setXY(xx, yy)
        }
        return super.onTouchEvent(event)
    }
}