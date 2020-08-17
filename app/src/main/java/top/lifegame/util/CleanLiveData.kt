package top.lifegame.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class CleanLiveData<T> : LiveData<T>() {
    //LiveData实例初始化后，只要值被改变过，就将hasModified置为true
    //后续再调用observe设置监听的时候，就需要拦截第一次onChanged
    var hasModified = false
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, object : Observer<T> {
            var hasIntercept = false
            override fun onChanged(t: T) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t)
                }
                hasIntercept = true
            }
        })
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(object : Observer<T> {
            var hasIntercept = false
            override fun onChanged(t: T) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t)
                }
                hasIntercept = true
            }
        })
    }

    public override fun setValue(value: T) {
        super.setValue(value)
        hasModified = true
    }

    public override fun postValue(value: T) {
        super.postValue(value)
        hasModified = true
    }
}