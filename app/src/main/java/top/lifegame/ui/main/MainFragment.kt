package top.lifegame.ui.main

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import top.lifegame.R
import top.lifegame.databinding.MainFragmentBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var mainFragmentBinding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )
        mainFragmentBinding.mainFragment = this
        return mainFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(viewModel)
        viewModel.birthDay.observe(viewLifecycleOwner, Observer {
            //mainFragmentBinding.llBattery.visibility = if (it == 0L) View.GONE else View.VISIBLE
            onBirthDayOrLifeSpanChange(it, viewModel.lifeSpan.value ?: -1)
        })
        viewModel.lifeSpan.observe(viewLifecycleOwner, Observer {
            onBirthDayOrLifeSpanChange(viewModel.birthDay.value ?: -1, it)
        })
    }

    private fun onBirthDayOrLifeSpanChange(birthDay: Long, value: Int) {
        if (birthDay != -1L) {
            val birthDayFormat =
                SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(Date(birthDay))
            Logger.d("birth_day:${birthDayFormat}")
            val timePassed = (System.currentTimeMillis() - birthDay) / 1000 / 60 / 60 / 24
            val timeLeft = (Calendar.getInstance()
                .apply { set(2080, 0, 0) }
                .timeInMillis - System.currentTimeMillis()) / 1000 / 60 / 60 / 24
            val birthDayBigDecimal =
                BigDecimal(timeLeft * 10000 / (timePassed + timeLeft)).divide(
                    BigDecimal(100),
                    2, RoundingMode.HALF_DOWN
                )
            ValueAnimator.ofInt(
                mainFragmentBinding.wvBattery.heightDp,
                birthDayBigDecimal.multiply(BigDecimal(3)).toInt()
            ).apply {
                duration = 2000
                addUpdateListener { animation ->
                    val animatedValue: Int = animation.animatedValue as Int
                    mainFragmentBinding.wvBattery.heightDp = animatedValue
                }
                start()
            }
            mainFragmentBinding.tvBattery.text = "${birthDayBigDecimal.toPlainString()}%"
        } else {
            mainFragmentBinding.wvBattery.heightDp = 300
            Logger.d("birth_day:-1")
        }
    }

    fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        Logger.d("onTextChanged:$charSequence")
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ll_battery -> {
                context?.apply {
                    val now = Calendar.getInstance()
                    val value = viewModel.birthDay.value ?: System.currentTimeMillis()
                    now.timeInMillis = if (value == -1L) System.currentTimeMillis() else value
                    DatePickerDialog(
                        this,
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val instance = Calendar.getInstance()
                            instance.set(year, month, dayOfMonth)
                            viewModel.birthDay.value = instance.timeInMillis
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        setMessage("设置出生日期")
                        show()
                    }
                }
            }
        }
    }
}