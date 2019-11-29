package com.lwh.debugtools.base.thread

import android.os.Handler
import android.os.Looper
import android.util.Log

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author lwh
 * @Date 2019/8/23 12:27
 * @description ThreadUtil
 */
object ThreadUtil {

    private val TAG = "ThreadUtil"
    private val THREADPOOL_QUEUE_SIZE = 30
    private val mLockQueue = Any()// ,mLock=new
    // Object();
    private var mExecutorServiceQueue: ExecutorService? = null
    private var mExecutorService: ExecutorService? = null

    /**
     * 若指定的任务需要立刻执行时，用这个方法
     *
     * @param task
     * @param immediately
     * :ture表示希望task任务立刻执行，不能排队
     */
    @JvmOverloads
    fun queueWork(task: Runnable, immediately: Boolean = false) {
        if (mExecutorServiceQueue == null) {
            synchronized(mLockQueue) {
                if (mExecutorServiceQueue == null) {
                    mExecutorServiceQueue = ThreadPoolExecutor(
                        THREADPOOL_QUEUE_SIZE / 10 + 1,
                            THREADPOOL_QUEUE_SIZE, 30L, TimeUnit.SECONDS, LinkedBlockingQueue(),
                            AspThreadFactory("apputil"), ThreadPoolExecutor.DiscardPolicy())
                }
            }
        }
        val executor = mExecutorServiceQueue as ThreadPoolExecutor?
        val queue = executor!!.queue
        if (!immediately || queue == null || queue.size < executor.corePoolSize) {
            executor.execute(task)
        } else {
            // Thread thread = new Thread(task);
            // thread.start();
            runWork(task)
        }
        Log.d(TAG, "queueWork task=$task,immdeiately=$immediately")
    }

    /**
     * 立刻在线程池运行线程
     *
     * @param task
     */
    private fun runWork(task: Runnable) {
        if (mExecutorService == null) {
            synchronized(mLockQueue) {
                if (mExecutorService == null) {
                    mExecutorService = Executors.newCachedThreadPool(AspThreadFactory("apputil"))
                }
            }
        }
        mExecutorService!!.submit(task)
    }

    fun releaseThreadPool() {
        if (mExecutorService != null && !mExecutorService!!.isShutdown) {
            mExecutorService!!.shutdownNow()
        }
        mExecutorService = null

        if (mExecutorServiceQueue != null && !mExecutorServiceQueue!!.isShutdown) {
            mExecutorServiceQueue!!.shutdownNow()
        }
        mExecutorServiceQueue = null
    }

    fun runMain(run:()->Unit) {
        MainHandler.getInstance()!!.post(run)
    }

}

/**
 * 在线程池运行线程。若达到线程池最大限制(10)则排队等待执行
 *
 * @param task
 */

internal class AspThreadFactory(private val mName: String) : ThreadFactory {

    private val mPoolSize = AtomicInteger(1)

    override fun newThread(r: Runnable): Thread {
        val size = mPoolSize.getAndIncrement()
        return Thread(r, "$mName:$size")
    }

}

internal class MainHandler private constructor() : Handler(Looper.getMainLooper()) {
    companion object {
        @Volatile
        private var instance: MainHandler? = null

        fun getInstance(): MainHandler? {
            if (null == instance) {
                synchronized(MainHandler::class.java) {
                    if (null == instance) {
                        instance = MainHandler()
                    }
                }
            }
            return instance
        }
    }
}