package com.lwh.debugtools.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.lwh.debugtools.R
import com.lwh.debugtools.base.ui.HeaderFooterActivity
import kotlinx.android.synthetic.main.l_activity_default_error.*

/**
 * @author lwh
 * @Date 2019/10/25 14:37
 * @description 接收异常activity
 */
class DefaultErrorActivity : HeaderFooterActivity() {

    override fun initBefore() {
        bodyLayout = R.layout.l_activity_default_error
        val a = obtainStyledAttributes(R.styleable.AppCompatTheme)
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar)
        }
        a.recycle()
    }

    override fun init() {

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)
        val errorMessage = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)
        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            tv_activity_restart.setText(R.string.customactivityoncrash_error_activity_restart_app)
            tv_activity_restart.setOnClickListener {
                CustomActivityOnCrash.restartApplication(
                    this@DefaultErrorActivity,
                    config
                )
            }
        } else {
            tv_activity_restart.setOnClickListener {
                CustomActivityOnCrash.closeApplication(
                    this@DefaultErrorActivity,
                    config
                )
            }
        }


        if (config.isShowErrorDetails()) {
            tv_error_info.setOnClickListener {
                //We retrieve all the error data and show it
                val alertDialog: AlertDialog = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.customactivityoncrash_error_activity_error_details_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.customactivityoncrash_error_activity_error_details_copy)) { _, _ ->
                        copyErrorToClipboard(errorMessage)
                        Toast.makeText(
                            this@DefaultErrorActivity,
                            R.string.customactivityoncrash_error_activity_error_details_copied,
                            Toast.LENGTH_SHORT
                        ).show()
                    }.setNegativeButton(
                        getString(R.string.customactivityoncrash_error_activity_error_details_close),
                        null
                    )
                    .create()
                alertDialog.show()

            }
        } else {
            tv_error_info.visibility = View.GONE
        }

        val defaultErrorActivityDrawableId = config.getErrorDrawable()

        if (defaultErrorActivityDrawableId != null && defaultErrorActivityDrawableId != 0) {
            iv_error.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    defaultErrorActivityDrawableId,
                    theme
                )
            )
        }
    }

    private fun copyErrorToClipboard(errorInformation: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label),
            errorInformation
        )
        clipboard.setPrimaryClip(clip)
    }
}
