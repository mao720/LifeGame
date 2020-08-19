package top.lifegame

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.gyf.immersionbar.ktx.immersionBar
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import top.lifegame.ui.main.MainFragment
import top.lifegame.ui.main.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        immersionBar {
            statusBarDarkFont(true)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        //ticker()
        startAnimation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startAnimation() {
        val cvButton = findViewById<CardView>(R.id.cv_button)
        val btnButton = findViewById<Button>(R.id.btn_button)
        val clockValueAnimator = ValueAnimator.ofInt(0, 1000)
            .apply {
                duration = 1000000
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener { animation ->
                    val animatedValue: Int = animation.animatedValue as Int
                    btnButton.rotation = -animatedValue.toFloat() * 6
                }
                start()
            }
        val accValueAnimator = ValueAnimator.ofInt(0, 100000)
            .apply {
                duration = 1000000
                interpolator = LinearInterpolator()
                addUpdateListener {
                    Log.d("MMM", it.animatedValue.toString())
                    val animatedValue: Int = it.animatedValue as Int
                    val accValue = if (animatedValue >= 2650) 2650 else animatedValue
                    btnButton.rotation = -animatedValue.toFloat() * accValue / 30
                    cvButton.scaleX = 1F + accValue / 2650F
                    cvButton.scaleY = 1F + accValue / 2650F
                }
            }
        btnButton.apply {
            setOnClickListener {
                //Snackbar.make(this, "打开设置界面", Snackbar.LENGTH_LONG).show()
                QMUITipDialog.Builder(this@MainActivity)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                    .setTipWord("打开设置界面")
                    .create().apply {
                        show()
                        postDelayed({ this.dismiss() }, 2000)
                    }
            }
            setOnLongClickListener {
                clockValueAnimator.pause()
                accValueAnimator.start()
                true
            }
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        accValueAnimator.cancel()
                        cvButton.scaleX = 1f
                        cvButton.scaleY = 1f
                        if (clockValueAnimator.isPaused) clockValueAnimator.resume()
                    }
                }
                false
            }
        }
    }
}