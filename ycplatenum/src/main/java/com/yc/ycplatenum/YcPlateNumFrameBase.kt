package com.yc.ycplatenum

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import com.yc.ycplatenum.YcPlateNumData.getYcPlateNumFrameBg

/**
 * 输入框容器
 */
open class YcPlateNumFrameBase : LinearLayout {
    /**
     * 改变来源 0：车牌号输入
     */

    protected val KEY_COLOR_CHANGE_SOURCE_INPUT = 0

    /**
     * 改变来源 1：车牌颜色选择
     */
    protected val KEY_COLOR_CHANGE_SOURCE_COLOR = 1

    /**
     * 改变来源 2:外部设置
     */
    protected val KEY_COLOR_CHANGE_SOURCE_SET = 2

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        this.initView(context, attrs)
    }

    protected var mContext: Context? = null
    protected var mPlateNumBtnList: MutableList<Button> = arrayListOf()
    var mPlateNumViewBean: YcPlateNumViewBean = YcPlateNumViewBean()

    /**
     * 能否修改
     */
    var hasChange: Boolean = true


    protected open fun initView(context: Context, attrs: AttributeSet?) {

    }

    protected fun initPlateNumBtn() {
        mPlateNumBtnList.forEachIndexed { index, button ->
            button.setOnClickListener {
                mPlateNumViewBean.mPlateNumSelectIndex = index
                updateBtnSetting()
                mPlateNumFrameClick?.invoke()
            }
        }
        mPlateNumFrameClick?.invoke()
        updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_SET)
    }

    /**
     * 点击车牌号回调
     */
    open var mPlateNumFrameClick: (() -> Unit)? = null
    protected fun updateBtnSetting() {
        mPlateNumBtnList.forEachIndexed { index, button ->
            //字体颜色设置
            if (mPlateNumViewBean.plateNumColorSelect == YcPlateNumData.PLATE_NUM_TYPE_BLACK) {
                button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_black))
            } else {
                button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_other))
            }
            //背景颜色设置
            button.setBackgroundResource(getYcPlateNumFrameBg(
                    index,
                    mPlateNumViewBean.plateNumColorSelect,
                    hasChange && index == mPlateNumViewBean.mPlateNumSelectIndex
                )
            )
        }
    }

    /**
     * @param changeSource 改变来源 0：车牌号输入 1：车牌颜色选择
     */
    protected open fun updatePlateNumDate(changeSource: Int) {

    }

    fun getPlateNum(): String {
        var plateNum = ""
        mPlateNumBtnList.forEach {
            plateNum += it.text.toString()
        }
        return plateNum
    }

    fun getPlateNumColor(): String {
        return mPlateNumViewBean.plateNumColorSelect
    }

    /**
     * 设置数据
     */
    open fun setPlateNum(plateNumViewBean: YcPlateNumViewBean) {
        this.mPlateNumViewBean.plateNumColorSelect = plateNumViewBean.plateNumColorSelect
        this.mPlateNumViewBean.plateNumColorSelectOcr = plateNumViewBean.plateNumColorSelectOcr
        this.mPlateNumViewBean.mPlateNumSelectIndex = plateNumViewBean.mPlateNumSelectIndex
        this.mPlateNumViewBean.plateNumBtnList = plateNumViewBean.plateNumBtnList
        this.mPlateNumViewBean.plateNumBtnList.forEachIndexed { index, s ->
            mPlateNumBtnList[index].text = s
        }
        updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_SET)
    }

    /**
     * 设置来至ocr的数据
     */
    fun setOcrPlateNum(plateNumColorSelectOcr: @YcPlateNumData.YcPlateNumColorType String, plateNum: String) {
        val plateNumViewBean = YcPlateNumViewBean()
        plateNum.forEachIndexed { index, c ->
            plateNumViewBean.mPlateNumSelectIndex = 0
            plateNumViewBean.plateNumBtnList[index] = c.toString()
        }
        plateNumViewBean.plateNumColorSelectOcr = plateNumColorSelectOcr
        plateNumViewBean.plateNumColorSelect = plateNumColorSelectOcr

        setPlateNum(plateNumViewBean)
    }

}

