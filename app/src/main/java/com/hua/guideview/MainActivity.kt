package com.hua.guideview

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hua.guide.GuideListener
import com.hua.guide.GuideView
import com.hua.guide.TapTarget
import com.hua.guide.showGuideView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var isShowGuide = false
        findViewById<View>(R.id.tv).setOnClickListener {
            isShowGuide = if (!isShowGuide) {
                showGuideView(
                    TapTarget(
                        it, "这是一个介绍，点击这里将会进入预览页面！"
                    )
                )
                true
            } else {
                Toast.makeText(this, "tv响应了", Toast.LENGTH_SHORT).show()
                false
            }
        }

        findViewById<View>(R.id.tv_dialog).setOnClickListener {
            val dialog = AlertDialog.Builder(this@MainActivity)
                .setTitle("Uh oh")
                .setMessage("You canceled the sequence")
                .setPositiveButton("Oops", null).show()
            dialog.showGuideView(
                TapTarget(
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE),
                    "Uh oh!",
                    "You canceled the sequence at step "
                )
                    .setCancelable(false)
                    .setGuideListener(object : GuideListener {
                        override fun clickTarget(view: GuideView) {
                            dialog.dismiss()
                            super.clickTarget(view)
                        }
                    })
            )
        }
    }
}