package com.yc.ycplatenum

import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class YcPlateNumDialog @JvmOverloads constructor(
    val context: Context,
    private val lifecycleOwner: LifecycleOwner? = null,
) {
    private var mPopupWindow: PopupWindow? = null
    val mPlateNumView: YcPlateNumInputView

    init {
        lifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                mPopupWindow?.dismiss()
            }
        })
        mPlateNumView = YcPlateNumInputView(context)
        mPlateNumView.setBackgroundResource(R.color.yc_plate_num_frame_view_bg)
        mPlateNumView.showKeyboard(true)
        mPopupWindow = PopupWindow(mPlateNumView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mPlateNumView.mCloseCall = {
            mOkCall?.invoke(it)
            mPopupWindow?.dismiss()
        }
        mPopupWindow?.setOnDismissListener {
        }
    }

    fun keyboardPopupShow(view: View, plateNumViewBean: YcPlateNumViewBean) {
        mPlateNumView.setPlateNum(plateNumViewBean)
        mPopupWindow?.height = getDisplayMetrics(context).heightPixels - view.height - getStatusBarHeight(context)
        mPopupWindow?.showAsDropDown(view, Gravity.BOTTOM, 0, 0)
    }

    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun ycDismiss() {
        mOkCall?.invoke(mPlateNumView.mPlateNumViewBean)
        mPopupWindow?.dismiss()
    }

    fun hasShow(): Boolean {
        return mPopupWindow?.isShowing == true
    }

    fun dismiss() {
        mPopupWindow?.dismiss()
    }

    var mOkCall: ((YcPlateNumViewBean) -> Unit)? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)

//
//
//        val dialogWindow = window
//        dialogWindow!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
//        dialogWindow.setLayout(LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//        setContentView(view)
//        setCancelable(true)
//        setCanceledOnTouchOutside(false)
//    }

//    fun showPlateNum(plateNumFrameView2: YcPlateNumFrameView2, colorAdapterSelect: Int) {
//        mPlateNumColorAdapter.mSelect = colorAdapterSelect
//        mViewBinding.plateNumFrameView.setPlateNum(plateNumFrameView2)
//    }

}
