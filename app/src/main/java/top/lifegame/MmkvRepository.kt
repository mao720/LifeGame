package top.lifegame

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV

/**
 * Description:
 * 对MMKV进行封装，以支持LiveData
 *
 * 2020/8/14 (https://github.com/mao720)
 */
class MmkvRepository {

    companion object {
        const val TYPE_BYTES = "type_bytes"
        const val TYPE_INT = "type_int"
        const val TYPE_STRING = "type_string"
        const val TYPE_LONG = "type_long"
        const val TYPE_BOOLEAN = "type_boolean"
        const val TYPE_FLOAT = "type_float"
        const val TYPE_STRING_SET = "type_string_set"

        @Volatile
        private var instance: MmkvRepository? = null
        fun getInstance(): MmkvRepository =
            instance ?: synchronized(this) {
                instance ?: MmkvRepository().also { instance = it }
            }
    }

    /**
     * 数据第一次使用
     * 1. 当有数据被使用时，根据type + key生成一个对应的LiveData，保存到Map中
     * 2. 使用参数LifecycleOwner注册LiveData的监听，在此生命周期内，LiveData发生变化则直接保存到MMKV中
     * 3. 取出MMKV中的数据，赋值给LiveData（LiveData值第一次被改变）
     * 4. 返回该LiveData给外部使用（外部注册后会立即收到初始的值）
     *
     * 同一个数据再次被使用
     * 5. 使用参数LifecycleOwner注册LiveData的监听（LiveData已经存在，MMKV不用再取值赋值）
     */
    private val dataMap: HashMap<String, MutableLiveData<Any>> = HashMap()

    fun getBytes(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: ByteArray = byteArrayOf()
    ): MutableLiveData<ByteArray> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_BYTES
        )
    }

    fun getInt(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Int = 0
    ): MutableLiveData<Int> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_INT
        )
    }

    fun getString(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: String = ""
    ): MutableLiveData<String> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_STRING
        )
    }

    fun getLong(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Long = 0L
    ): MutableLiveData<Long> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_LONG
        )
    }

    fun getBoolean(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Boolean = false
    ): MutableLiveData<Boolean> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_BOOLEAN
        )
    }

    fun getFloat(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Float = 0F
    ): MutableLiveData<Float> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_FLOAT
        )
    }

    fun getStringSet(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Set<String> = setOf()
    ): MutableLiveData<Set<String>> {
        return getLiveData(
            lifecycleOwner,
            key,
            defValue,
            TYPE_STRING_SET
        )
    }

    private fun <T> getLiveData(
        lifecycleOwner: LifecycleOwner,
        key: String,
        defValue: Any,
        type: String
    ): MutableLiveData<T> {
        val typeKey = type + "_" + key
        (dataMap[typeKey] == null).also {
            dataMap[typeKey] = dataMap[typeKey] ?: MutableLiveData<T>() as MutableLiveData<Any>
            return dataMap[typeKey]?.apply {
                when (type) {
                    TYPE_BYTES -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putBytes(typeKey, it as ByteArray)
                        })
                        if (it) value = MMKV.defaultMMKV().getBytes(typeKey, defValue as ByteArray)
                    }
                    TYPE_INT -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putInt(typeKey, it as Int)
                        })
                        if (it) value = MMKV.defaultMMKV().getInt(typeKey, defValue as Int)
                    }
                    TYPE_STRING -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putString(typeKey, it as String)
                        })
                        if (it) value = MMKV.defaultMMKV().getString(typeKey, defValue as String)
                    }
                    TYPE_LONG -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putLong(typeKey, it as Long)
                        })
                        if (it) value = MMKV.defaultMMKV().getLong(typeKey, defValue as Long)
                    }
                    TYPE_BOOLEAN -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putBoolean(typeKey, it as Boolean)
                        })
                        if (it) value = MMKV.defaultMMKV().getBoolean(typeKey, defValue as Boolean)
                    }
                    TYPE_FLOAT -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putFloat(typeKey, it as Float)
                        })
                        if (it) value = MMKV.defaultMMKV().getFloat(typeKey, defValue as Float)
                    }
                    TYPE_STRING_SET -> {
                        observe(lifecycleOwner, Observer {
                            Logger.d("MMKV live data changed:$it")
                            MMKV.defaultMMKV().putStringSet(typeKey, it as Set<String>)
                        })
                        if (it) value =
                            MMKV.defaultMMKV().getStringSet(typeKey, defValue as Set<String>)
                    }
                    else -> {
                        throw  RuntimeException("MmkvRepository:Live data type not support(类型不支持)")
                    }
                }
            } as MutableLiveData<T>
        }
    }
}