package com.yc.ycplatenum

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yc.ycplatenum.databinding.YcPlateNumBinding

class YcPlateNumView : LinearLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        this.initView(context, attrs)
    }

    protected var mContext: Context? = null

    /**
     * 选中的车牌颜色
     */
    var mPlateNumColor: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_BLUE

    /**
     * 车牌号码
     */
    var mPlateNum: String = ""
    private lateinit var mViewBinding: YcPlateNumBinding
    var mTextSize: Float = 22f
        set(value) {
            field = value
            mViewBinding.plateNumLeftTv.textSize = value
            mViewBinding.plateNumPointTv.textSize = value
            mViewBinding.plateNumMidTv.textSize = value
            mViewBinding.plateNumRightTv.textSize = value
        }

    protected fun initView(context: Context, attrs: AttributeSet?) {
        mViewBinding = YcPlateNumBinding.inflate(LayoutInflater.from(context), this, false)
        mViewBinding.updatePlateView()
        val a = context.obtainStyledAttributes(attrs, R.styleable.YcPlateNumView)
        mTextSize = a.getDimension(R.styleable.YcPlateNumView_ycTextSize, 22f)
        addView(mViewBinding.root)
        a.recycle()
    }

    protected fun YcPlateNumBinding.updatePlateView() {
        if (mPlateNumColor == YcPlateNumData.PLATE_NUM_TYPE_NONE) {
            plateNumMidTv.text = "无牌车辆"//TODO 待开放出去
            plateNumMidTv.visibility = View.VISIBLE
            plateNumLeftTv.visibility = View.GONE
            plateNumPointTv.visibility = View.GONE
            plateNumRightTv.visibility = View.GONE
        } else if (mPlateNum.isBlank()) {
            plateNumMidTv.visibility = View.GONE
            plateNumLeftTv.visibility = View.GONE
            plateNumPointTv.visibility = View.GONE
            plateNumRightTv.visibility = View.GONE
        } else if (mPlateNum.length < 2) {
            plateNumMidTv.visibility = View.GONE
            plateNumLeftTv.visibility = View.VISIBLE
            plateNumPointTv.visibility = View.VISIBLE
            plateNumRightTv.visibility = View.VISIBLE
            plateNumLeftTv.text = mPlateNum.ifEmpty { "" }
        } else {
            plateNumMidTv.visibility = View.GONE
            plateNumLeftTv.visibility = View.VISIBLE
            plateNumPointTv.visibility = View.VISIBLE
            plateNumRightTv.visibility = View.VISIBLE
            plateNumLeftTv.text = mPlateNum.substring(0, 2)
            plateNumRightTv.text = mPlateNum.substring(2, mPlateNum.length)
        }
        YcPlateNumData.getPlateNumTypeColorBg(mPlateNumColor) { bgColorLeft, textColor, bgColorRight ->
            if (mPlateNumColor == YcPlateNumData.PLATE_NUM_TYPE_GREEN) {
                plateNumBgV.visibility = VISIBLE
                plateNumBgV.setBackgroundResource(R.drawable.yc_plate_num_bg_green_border)
            } else if (mPlateNumColor == YcPlateNumData.PLATE_NUM_TYPE_WHITE) {
                plateNumBgV.visibility = VISIBLE
                plateNumBgV.setBackgroundResource(R.drawable.yc_plate_num_bg_white_border)
            } else {
                plateNumBgV.visibility = GONE
            }
            plateNumLeftTv.setTextColor(context.resources.getColor(textColor))
            plateNumLeftBgV.setBackgroundResource(bgColorLeft)
            plateNumMidTv.setTextColor(context.resources.getColor(textColor))
            plateNumPointTv.setTextColor(context.resources.getColor(textColor))
            plateNumRightTv.setTextColor(context.resources.getColor(textColor))
            plateNumRightBgV.setBackgroundResource(bgColorRight)
        }
    }

    fun setPlateNum(plateNum: String?, plateNumColor: String?) {
        mPlateNumColor = plateNumColor ?: YcPlateNumData.PLATE_NUM_TYPE_BLUE
        mPlateNum = plateNum.toString()
        mViewBinding.updatePlateView()
    }
}