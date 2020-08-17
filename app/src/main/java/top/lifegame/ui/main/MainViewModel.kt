package top.lifegame.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import top.lifegame.ConstMMKV
import top.lifegame.MmkvRepository
import top.lifegame.base.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val mmkvRepository = MmkvRepository.getInstance()

    val birthDay: MutableLiveData<Long> by lazy {
        mmkvRepository.getLong(lifecycleOwner, ConstMMKV.SETTING_BIRTH_DAY, -1L)
    }
}