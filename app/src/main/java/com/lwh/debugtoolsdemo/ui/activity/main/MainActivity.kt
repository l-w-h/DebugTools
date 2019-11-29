package com.lwh.debugtoolsdemo.ui.activity.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lwh.debugtools.DebugTools
import com.lwh.debugtoolsdemo.R
import com.lwh.debugtoolsdemo.ui.activity.main.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val mainPresenter = MainPresenter()

    var i: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {

        btn_request?.setOnClickListener(this)
        btn_log?.setOnClickListener(this)
        btn_error?.setOnClickListener(this)
        btn_jump?.setOnClickListener(this)
        btn_add_view?.setOnClickListener(this)
        btn_remove_view?.setOnClickListener(this)
    }

    private fun request() {
        mainPresenter.request(::success, ::fail)
    }

    private fun success(data: Any?) {
        data?.let {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fail(e: Throwable?) {
        Toast.makeText(this, e?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_request -> {
                request()
            }
            R.id.btn_log -> {
                DebugTools.getInstance().logE("DebugView", "啦啦啦${i++}")
            }
            R.id.btn_error -> {
                throw RuntimeException("自定义运行时错误 -.-")
            }
            R.id.btn_jump -> {
            }
            R.id.btn_add_view -> {
                DebugTools.getInstance().attachDebugView(this@MainActivity)
            }
            R.id.btn_remove_view -> {
                DebugTools.getInstance().detachDebugView(this)
            }


        }
    }
}
