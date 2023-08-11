package com.hua.guideview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hua.guide.TapTarget
import com.hua.guide.showGuideView

class MainActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv).setOnClickListener {
            showGuideView(
                TapTarget(
                    it, "这是一个介绍，点击这里将会进入预览页面！"
                )
            )
        }
    }

}