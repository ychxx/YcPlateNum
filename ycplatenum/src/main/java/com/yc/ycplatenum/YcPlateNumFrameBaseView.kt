package com.yc.ycplatenum

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.yc.ycplatenum.databinding.YcPlateNumFrameBinding

/**
 * 输入框容器
 */
open class YcPlateNumFrameBaseView : YcPlateNumFrameBase {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private lateinit var mViewBinding: YcPlateNumFrameBinding

    /**
     * 是否推断车牌颜色
     */
    var hasDeduceColor: Boolean = false
    override fun initView(context: Context, attrs: AttributeSet?) {
        super.initView(context, attrs)
        mViewBinding = YcPlateNumFrameBinding.inflate(LayoutInflater.from(context), this, false)
        mPlateNumBtnList.add(mViewBinding.plateNum0)
        mPlateNumBtnList.add(mViewBinding.plateNum1)
        mPlateNumBtnList.add(mViewBinding.plateNum2)
        mPlateNumBtnList.add(mViewBinding.plateNum3)
        mPlateNumBtnList.add(mViewBinding.plateNum4)
        mPlateNumBtnList.add(mViewBinding.plateNum5)
        mPlateNumBtnList.add(mViewBinding.plateNum6)
        mPlateNumBtnList.add(mViewBinding.energyBtn)
        initPlateNumBtn()
        addView(mViewBinding.root)
    }

    override var mPlateNumFrameClick: (() -> Unit)? = {
        mKeyboardUpdateData?.invoke(mPlateNumViewBean.mPlateNumSelectIndex)
    }

    /**
     * @param changeSource 改变来源 0：车牌号输入 1：车牌颜色选择
     */
    override fun updatePlateNumDate(changeSource: Int) {
        var plateNum = ""
        mPlateNumBtnList.forEach {
            plateNum += it.text.toString()
        }
        mViewBinding.energyMaskTv.visibility = if (mViewBinding.energyBtn.text.isNullOrEmpty()) VISIBLE else GONE
        //手动输入时，根据车牌号推断车牌颜色
        if (hasDeduceColor && changeSource == KEY_COLOR_CHANGE_SOURCE_INPUT) {
            val plateNumColorMaybeList = YcPlateNumData.getPlateNumColorMaybe(mPlateNumViewBean.plateNumColorSelectOcr, plateNum)
            if (!plateNumColorMaybeList.isNullOrEmpty()) {
                mPlateNumViewBean.plateNumColorSelect = plateNumColorMaybeList!![0]
            }
        }
        updateBtnSetting()
        mKeyboardUpdateData?.invoke(mPlateNumViewBean.mPlateNumSelectIndex)
    }

    var mKeyboardUpdateData: ((plateNumSelectIndex: Int) -> Unit)? = null

    fun addKeyboardDate(content: String, type: @YcPlateNumData.PlateNumKeyboardType String) {
        when (type) {
            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DEL -> {
                if (mPlateNumViewBean.mPlateNumSelectIndex > 0) {
                    if (mPlateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex].text.isNullOrEmpty()) {
                        //当前为空，前一位也设为空（小程序是这样的）
                        mPlateNumViewBean.plateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex - 1] = ""
                        mPlateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex - 1].text = ""
                    }
                    mPlateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex].text = ""
                    mPlateNumViewBean.plateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex] = ""
                    mPlateNumViewBean.mPlateNumSelectIndex--
                    updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_INPUT)
                } else {
                    mPlateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex].text = ""
                    mPlateNumViewBean.plateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex] = ""
                    updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_INPUT)
                }
            }

            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_OK -> {

            }

            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DATA -> {
                mPlateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex].text = content
                mPlateNumViewBean.plateNumBtnList[mPlateNumViewBean.mPlateNumSelectIndex] = content
                if (mPlateNumViewBean.mPlateNumSelectIndex < mPlateNumBtnList.size - 1) {
                    mPlateNumViewBean.mPlateNumSelectIndex++
                }
                updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_INPUT)
            }
        }
    }
}

