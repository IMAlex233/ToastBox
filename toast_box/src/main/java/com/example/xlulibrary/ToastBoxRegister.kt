package com.example.xlulibrary

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.xlulibrary.data.TextStyle
import com.example.xlulibrary.toast.ActivityToast
import com.example.xlulibrary.toast.xToast
import com.example.xlulibrary.util.xLog
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue

object ToastBoxRegister : ActivityLifecycleCallbacks {

    private val TAG = "ToastBoxRegister"

    private var _currentActivity: WeakReference<Activity> ?= null
    private val currentActivity get() = _currentActivity?.get()!!

    lateinit var application: Application

    var defaultIcon : Int ?= null

    /**
     * 设置toast字体和背景样式
     */
    var textStyle:TextStyle ?= null

    /**
     * 设置toast默认弹出动画
     */
    var animStyle:Int = R.style.ToastAnim_1


    private var boxStack = LinkedBlockingQueue<xToast>()

    /**
     * WindowsToast同时最多弹出的数量
     */
    var WindowsToastSize:Int = 3


    /**
     * 在app中初始化，监听activity声明周期
     */
    fun init(application: Application):ToastBoxRegister = apply{
        application.registerActivityLifecycleCallbacks(this)
        this@ToastBoxRegister.application = application
    }

    fun getActivity():Activity{
        return currentActivity
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: ${activity.localClassName}")
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: ${activity.localClassName}")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "onActivityResumed: ${activity.localClassName}")
        _currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "onActivityPaused: ${activity.localClassName}")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped: ${activity.localClassName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState: ${activity.localClassName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "onActivityDestroyed: ${activity.localClassName}")
        if (activity.javaClass.name == currentActivity.javaClass.name){
            _currentActivity = null
        }
    }





    /**
     * 记录toastBox弹出数量
     */
    @Synchronized
    fun register(xToast: xToast?){
        if (xToast==null) return
        boxStack.offer(xToast)

        while (boxStack.size > WindowsToastSize){
            val toast : xToast ?= boxStack.poll()
            toast?.cancel()
        }
        xLog.d(TAG,"Register    ----  toast_size:${boxStack.size}")
    }

    @Synchronized
    fun unRegister(xToast:xToast?){
        xToast?.let {
            boxStack.remove(it)
        }
        xLog.d(TAG,"unRegister  ----  toast_size:${boxStack.size}")
    }


}
