package com.lwh.debugtools.crash

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.io.Serializable
import java.lang.reflect.Modifier


const val BACKGROUND_MODE_SILENT = 0
const val BACKGROUND_MODE_SHOW_CUSTOM = 1
const val BACKGROUND_MODE_CRASH = 2

/**
 * @author lwh
 * @Date 2019/8/29 21:36
 * @description CrashConfig
 */
class CrashConfig : Serializable {


    @IntDef(BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class BackgroundMode

    /**
     * Interface to be called when events occur, so they can be reported
     * by the app as, for example, Google Analytics events.
     */
    interface EventListener : Serializable {
        fun onLaunchErrorActivity()

        fun onRestartAppFromErrorActivity()

        fun onCloseAppFromErrorActivity()
    }

    private var backgroundMode = BACKGROUND_MODE_SHOW_CUSTOM
    private var enabled = true
    private var showErrorDetails = true
    private var showRestartButton = true
    private var trackActivities = false
    private var minTimeBetweenCrashesMs = 3000
    private var errorDrawable: Int? = null
    private var errorActivityClass: Class<out Activity>? = null
    private var restartActivityClass: Class<out Activity>? = null
    private var eventListener: EventListener? = null

    @BackgroundMode
    fun getBackgroundMode(): Int {
        return backgroundMode
    }

    fun setBackgroundMode(@BackgroundMode backgroundMode: Int) {
        this.backgroundMode = backgroundMode
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isShowErrorDetails(): Boolean {
        return showErrorDetails
    }

    fun setShowErrorDetails(showErrorDetails: Boolean) {
        this.showErrorDetails = showErrorDetails
    }

    fun isShowRestartButton(): Boolean {
        return showRestartButton
    }

    fun setShowRestartButton(showRestartButton: Boolean) {
        this.showRestartButton = showRestartButton
    }

    fun isTrackActivities(): Boolean {
        return trackActivities
    }

    fun setTrackActivities(trackActivities: Boolean) {
        this.trackActivities = trackActivities
    }

    fun getMinTimeBetweenCrashesMs(): Int {
        return minTimeBetweenCrashesMs
    }

    fun setMinTimeBetweenCrashesMs(minTimeBetweenCrashesMs: Int) {
        this.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs
    }

    @Nullable
    @DrawableRes
    fun getErrorDrawable(): Int? {
        return errorDrawable
    }

    fun setErrorDrawable(@Nullable @DrawableRes errorDrawable: Int?) {
        this.errorDrawable = errorDrawable
    }

    @Nullable
    fun getErrorActivityClass(): Class<out Activity>? {
        return errorActivityClass
    }

    fun setErrorActivityClass(@Nullable errorActivityClass: Class<out Activity>) {
        this.errorActivityClass = errorActivityClass
    }

    @Nullable
    fun getRestartActivityClass(): Class<out Activity>? {
        return restartActivityClass
    }

    fun setRestartActivityClass(@Nullable restartActivityClass: Class<out Activity>) {
        this.restartActivityClass = restartActivityClass
    }

    @Nullable
    fun getEventListener(): EventListener? {
        return eventListener
    }

    fun setEventListener(@Nullable eventListener: EventListener) {
        this.eventListener = eventListener
    }

    class Builder {
        private lateinit var config: CrashConfig

        /**
         * Defines if the error activity must be launched when the app is on background.
         * BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: launch the error activity when the app is in background,
         * BackgroundMode.BACKGROUND_MODE_CRASH: launch the default system error when the app is in background,
         * BackgroundMode.BACKGROUND_MODE_SILENT: crash silently when the app is in background,
         * The default is BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM (the app will be brought to front when a crash occurs).
         */
        @NonNull
        fun backgroundMode(@BackgroundMode backgroundMode: Int): Builder {
            config.backgroundMode = backgroundMode
            return this
        }

        /**
         * Defines if CustomActivityOnCrash crash interception mechanism is enabled.
         * Set it to true if you want CustomActivityOnCrash to intercept crashes,
         * false if you want them to be treated as if the library was not installed.
         * The default is true.
         */
        @NonNull
        fun enabled(enabled: Boolean): Builder {
            config.enabled = enabled
            return this
        }

        /**
         * Defines if the error activity must shown the error details button.
         * Set it to true if you want to show the full stack trace and device info,
         * false if you want it to be hidden.
         * The default is true.
         */
        @NonNull
        fun showErrorDetails(showErrorDetails: Boolean): Builder {
            config.showErrorDetails = showErrorDetails
            return this
        }

        /**
         * Defines if the error activity should show a restart button.
         * Set it to true if you want to show a restart button,
         * false if you want to show a close button.
         * Note that even if restart is enabled but you app does not have any launcher activities,
         * a close button will still be used by the default error activity.
         * The default is true.
         */
        @NonNull
        fun showRestartButton(showRestartButton: Boolean): Builder {
            config.showRestartButton = showRestartButton
            return this
        }

        /**
         * Defines if the activities visited by the user should be tracked
         * so they are reported when an error occurs.
         * The default is false.
         */
        @NonNull
        fun trackActivities(trackActivities: Boolean): Builder {
            config.trackActivities = trackActivities
            return this
        }

        /**
         * Defines the time that must pass between app crashes to determine that we are not
         * in a crash loop. If a crash has occurred less that this time ago,
         * the error activity will not be launched and the system crash screen will be invoked.
         * The default is 3000.
         */
        @NonNull
        fun minTimeBetweenCrashesMs(minTimeBetweenCrashesMs: Int): Builder {
            config.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs
            return this
        }

        /**
         * Defines which drawable to use in the default error activity image.
         * Set this if you want to use an image other than the default one.
         * The default is R.drawable.customactivityoncrash_error_image (a cute upside-down bug).
         */
        @NonNull
        fun errorDrawable(@Nullable @DrawableRes errorDrawable: Int?): Builder {
            config.errorDrawable = errorDrawable
            return this
        }

        /**
         * Sets the error activity class to launch when a crash occurs.
         * If null, the default error activity will be used.
         */
        @NonNull
        fun errorActivity(@Nullable errorActivityClass: Class<out Activity>): Builder {
            config.errorActivityClass = errorActivityClass
            return this
        }

        /**
         * Sets the main activity class that the error activity must launch when a crash occurs.
         * If not set or set to null, the default launch activity will be used.
         * If your app has no launch activities and this is not set, the default error activity will close instead.
         */
        @NonNull
        fun restartActivity(@Nullable restartActivityClass: Class<out Activity>): Builder {
            config.restartActivityClass = restartActivityClass
            return this
        }

        /**
         * Sets an event listener to be called when events occur, so they can be reported
         * by the app as, for example, Google Analytics events.
         * If not set or set to null, no events will be reported.
         *
         * @param eventListener The event listener.
         * @throws IllegalArgumentException if the eventListener is an inner or anonymous class
         */
        @NonNull
        fun eventListener(eventListener: EventListener): Builder {
            if (eventListener != null && eventListener.javaClass.enclosingClass != null && !Modifier.isStatic(
                    eventListener.javaClass.modifiers
                )
            ) {
                throw IllegalArgumentException("The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class.")
            } else {
                config.eventListener = eventListener
            }
            return this
        }

        @NonNull
        fun get(): CrashConfig {
            return config
        }

        fun apply() {
            CustomActivityOnCrash.setConfig(config)
        }

        companion object {

            @NonNull
            fun create(): Builder {
                val builder = Builder()
                val currentConfig = CustomActivityOnCrash.getConfig()

                val config = CrashConfig()
                config.backgroundMode = currentConfig.backgroundMode
                config.enabled = currentConfig.enabled
                config.showErrorDetails = currentConfig.showErrorDetails
                config.showRestartButton = currentConfig.showRestartButton
                config.trackActivities = currentConfig.trackActivities
                config.minTimeBetweenCrashesMs = currentConfig.minTimeBetweenCrashesMs
                config.errorDrawable = currentConfig.errorDrawable
                config.errorActivityClass = currentConfig.errorActivityClass
                config.restartActivityClass = currentConfig.restartActivityClass
                config.eventListener = currentConfig.eventListener

                builder.config = config

                return builder
            }
        }
    }
}