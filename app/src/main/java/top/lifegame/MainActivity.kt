package top.lifegame

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.gyf.immersionbar.ktx.immersionBar
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
        val btnButton = findViewById<Button>(R.id.btn_button)
        ValueAnimator.ofInt(
            0,
            100000
        ).apply {
            duration = 100000000
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val animatedValue: Int = animation.animatedValue as Int
                btnButton.rotation = -(animatedValue * 6).toFloat()
            }
            start()
        }
    }
}