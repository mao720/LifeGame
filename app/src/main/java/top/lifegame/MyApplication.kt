package top.lifegame

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.mmkv.MMKV


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(object : AndroidLogAdapter(
            PrettyFormatStrategy.newBuilder().tag(getString(R.string.app_name)).build()
        ) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        val rootDir = MMKV.initialize(this)
        Logger.d("mmkv root: $rootDir")

        DoraemonKit.install(this, "d02bce6a10045cbc58c5e311157bc360")
    }
}