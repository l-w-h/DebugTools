package com.lwh.debugtools.base

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author lwh
 * @Date 2019/10/19 15:28
 * @description MyAsyncListDiffer
 */
class MyAsyncListDiffer<T>(
    private val mUpdateCallback: ListUpdateCallback,
    internal/* synthetic access */ val diffCallback: DiffUtil.ItemCallback<T>
) {
    internal/* synthetic access */ val mMainThreadExecutor: Executor
    private var mBackgroundThreadExecutor: Executor? = null

    private val backgroundThreadExecutor: Executor
        get() {
            if (mBackgroundThreadExecutor == null) {
                synchronized(sExecutorLock) {
                    if (sDiffExecutor == null) {
                        sDiffExecutor = Executors.newFixedThreadPool(2)
                    }
                }
                mBackgroundThreadExecutor = sDiffExecutor
            }
            return mBackgroundThreadExecutor!!
        }

    private var mList: List<T>? = null

    /**
     * Non-null, unmodifiable version of mList.
     *
     *
     * Collections.emptyList when mList is null, wrapped by Collections.unmodifiableList otherwise
     */
    /**
     * Get the current List - any diffing to present this list has already been computed and
     * dispatched via the ListUpdateCallback.
     *
     *
     * If a `null` List, or no List has been submitted, an empty list will be returned.
     *
     *
     * The returned list may not be mutated - mutations to content must be done through
     * [.submitList].
     *
     * @return current List.
     */
    var currentList = emptyList<T>()
        private set

    // Max generation of currently scheduled runnable
    internal /* synthetic access */ var mMaxScheduledGeneration: Int = 0

    private class MainThreadExecutor internal constructor() : Executor {
        internal val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    /**
     * Convenience for
     * `AsyncListDiffer(new AdapterListUpdateCallback(adapter),
     * new AsyncDifferConfig.Builder().setDiffCallback(diffCallback).build());`
     *
     * @param adapter      Adapter to dispatch position updates to.
     * @param diffCallback ItemCallback that compares items to dispatch appropriate animations when
     * @see DiffUtil.DiffResult.dispatchUpdatesTo
     */
    constructor(
        adapter: RecyclerView.Adapter<*>,
        diffCallback: DiffUtil.ItemCallback<T>
    ) : this(AdapterListUpdateCallback(adapter), diffCallback) {
    }

    init {
        mMainThreadExecutor = sMainThreadExecutor
    }

    /**
     * Pass a new List to the AdapterHelper. Adapter updates will be computed on a background
     * thread.
     *
     *
     * If a List is already present, a diff will be computed asynchronously on a background thread.
     * When the diff is computed, it will be applied (dispatched to the [ListUpdateCallback]),
     * and the new List will be swapped in.
     *
     * @param newList The new List.
     */
    fun submitList(newList: List<T>?, runnable: Runnable) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++mMaxScheduledGeneration

        if (newList === mList) {
            // nothing to do (Note - still had to inc generation, since may have ongoing work)
            runnable.run()
            return
        }

        // fast simple remove all
        if (newList == null) {

            val countRemoved = mList!!.size
            mList = null
            currentList = emptyList()
            // notify last, after list is updated
            mUpdateCallback.onRemoved(0, countRemoved)
            runnable.run()
            return
        }

        // fast simple first insert
        if (mList == null) {
            mList = newList
            currentList = Collections.unmodifiableList(newList)
            // notify last, after list is updated
            mUpdateCallback.onInserted(0, newList.size)
            runnable.run()
            return
        }

        val oldList = mList
        backgroundThreadExecutor.execute {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList!!.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return if (oldItem != null && newItem != null) {
                        diffCallback.areItemsTheSame(oldItem, newItem)
                    } else oldItem == null && newItem == null
                    // If both items are null we consider them the same.
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    if (oldItem != null && newItem != null) {
                        return diffCallback.areContentsTheSame(oldItem, newItem)
                    }
                    if (oldItem == null && newItem == null) {
                        return true
                    }
                    // There is an implementation bug if we reach this point. Per the docs, this
                    // method should only be invoked when areItemsTheSame returns true. That
                    // only occurs when both items are non-null or both are null and both of
                    // those cases are handled above.
                    throw AssertionError()
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    if (oldItem != null && newItem != null) {
                        return diffCallback.getChangePayload(oldItem, newItem)
                    }
                    // There is an implementation bug if we reach this point. Per the docs, this
                    // method should only be invoked when areItemsTheSame returns true AND
                    // areContentsTheSame returns false. That only occurs when both items are
                    // non-null which is the only case handled above.
                    throw AssertionError()
                }
            })

            mMainThreadExecutor.execute {
                if (mMaxScheduledGeneration == runGeneration) {
                    latchList(newList, result)
                    runnable.run()
                }
            }
        }
    }

    internal /* synthetic access */ fun latchList(newList: List<T>, diffResult: DiffUtil.DiffResult) {
        mList = newList
        // notify last, after list is updated
        currentList = Collections.unmodifiableList(newList)
        diffResult.dispatchUpdatesTo(mUpdateCallback)
    }

    companion object {
        private val sExecutorLock = Any()
        private var sDiffExecutor: Executor? = null

        //  use MainThreadExecutor from supportlib once one exists
        private val sMainThreadExecutor = MainThreadExecutor()
    }

}
