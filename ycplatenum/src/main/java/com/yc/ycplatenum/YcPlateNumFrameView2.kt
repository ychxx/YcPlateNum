package com.yc.ycplatenum

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import com.yc.ycplatenum.YcPlateNumData.getYcPlateNumFrameBg
import com.yc.ycplatenum.databinding.YcPlateNumFrameBinding

/**
 * 输入框容器
 */
open class YcPlateNumFrameView2 : LinearLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    protected var mPlateNumBtnList: MutableList<Button> = arrayListOf()
    private lateinit var mViewBinding: YcPlateNumFrameBinding

    /**
     * 车牌颜色
     */

    private var mPlateNumColorTypeSelect: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_BLUE

    /**
     * Orc识别到的车牌颜色
     */

    private var mPlateNumColorTypeOcr: @YcPlateNumData.YcPlateNumColorType String? = YcPlateNumData.PLATE_NUM_TYPE_ERROR
    private var mPlateNumColorTypeAll: List<@YcPlateNumData.YcPlateNumColorType String> = listOf()
    private fun initView(context: Context, attrs: AttributeSet?) {
        mViewBinding = YcPlateNumFrameBinding.inflate(LayoutInflater.from(context), this, false)
        mPlateNumBtnList.add(mViewBinding.plateNum0)
        mPlateNumBtnList.add(mViewBinding.plateNum1)
        mPlateNumBtnList.add(mViewBinding.plateNum2)
        mPlateNumBtnList.add(mViewBinding.plateNum3)
        mPlateNumBtnList.add(mViewBinding.plateNum4)
        mPlateNumBtnList.add(mViewBinding.plateNum5)
        mPlateNumBtnList.add(mViewBinding.plateNum6)
        mPlateNumBtnList.add(mViewBinding.energyBtn)
        addView(mViewBinding.root)
    }

    fun addKeyboardDate(content: String, type: @YcPlateNumData.PlateNumKeyboardType String) {
        when (type) {
            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DEL -> {
                if (mSelectBtnIndex > 0) {
                    if (mPlateNumBtnList[mSelectBtnIndex].text.isNullOrEmpty()) {
                        //当前为空，前一位也设为空（小程序是这样的）
                        mPlateNumBtnList[mSelectBtnIndex - 1].text = ""
                    }
                    mPlateNumBtnList[mSelectBtnIndex].text = ""
                    mSelectBtnIndex--
                    updatePlateNumBtn()
                } else {
                    mPlateNumBtnList[mSelectBtnIndex].text = ""
                    updatePlateNumBtn()
                }
            }

            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_OK -> {

            }

            YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DATA -> {
                mPlateNumBtnList[mSelectBtnIndex].text = content
                if (mSelectBtnIndex < mPlateNumBtnList.size - 1) {
                    mSelectBtnIndex++
                }
                updatePlateNumBtn()
            }
        }
    }

    private var mSelectBtnIndex = -1
    private fun initPlateNumBtn() {
        mPlateNumBtnList.forEachIndexed { index, button ->
            button.setOnClickListener {
                mSelectBtnIndex = index
                mPlateClick?.invoke(index)
                button.setBackgroundResource(getYcPlateNumFrameBg(index, mPlateNumColorTypeSelect, index == mSelectBtnIndex))
                if (mPlateNumColorTypeSelect == YcPlateNumData.PLATE_NUM_TYPE_BLACK) {
                    button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_black))
                } else {
                    button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_other))
                }

//                keyboardPopupShow()
            }
        }
        updatePlateNumBtn()
    }

    /**
     * 点击车牌号回调
     */
    public var mPlateClick: ((select: Int) -> Unit)? = null

    /**
     * 车牌颜色可选回调
     */
    public var mPlateNumColorChangeCall: ((listColor: List<@YcPlateNumData.YcPlateNumColorType String>) -> Unit)? = null
    private fun updatePlateNumBtn() {
        var plateNum = ""
        mPlateNumBtnList.forEach {
            plateNum += it.text.toString()
        }
        mViewBinding.energyMaskTv.visibility = if (mViewBinding.energyBtn.text.toString().isBlank()) GONE else VISIBLE
        val plateNumColorMaybeList = YcPlateNumData.getPlateNumColorMaybe(mPlateNumColorTypeOcr, plateNum)
        if (plateNumColorMaybeList.isNullOrEmpty()) {
            mPlateNumColorTypeSelect = plateNumColorMaybeList!![0]
        }
        mPlateNumBtnList.forEachIndexed { index, button ->
            button.setBackgroundResource(getYcPlateNumFrameBg(index, mPlateNumColorTypeSelect, index == mSelectBtnIndex))
            if (mPlateNumColorTypeSelect == YcPlateNumData.PLATE_NUM_TYPE_BLACK) {
                button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_black))
            } else {
                button.setTextColor(context.resources.getColor(R.color.yc_plate_num_frame_text_other))
            }
        }
        mPlateNumColorChangeCall?.invoke(mPlateNumColorTypeAll)
        mKeyboardUpdateData?.invoke(mSelectBtnIndex)
//        mPlateNumKeyboardPopup.updateKeyboardData(mSelectBtnIndex)
    }


    var mKeyboardUpdateData: ((plateNumSelectIndex: Int) -> Unit)? = null

    /**
     * 设置ocr识别的车牌颜色
     * 业务：ocr识别的车牌颜色不能修改为白色或黑色
     * 需求：识别成为白色或黑色牌照的车辆可手动修改为其他颜色，但识别成其他颜色车牌不能手动修改为白色或黑色，以免泊管员误操作导致订单金额与实际不符；
     */
    fun setPlateNum(
        plateNum: String?,
        plateNumColor: @YcPlateNumData.YcPlateNumColorType String?,
        plateNumColorOcr: @YcPlateNumData.YcPlateNumColorType String?,
    ) {
        mPlateNumBtnList.forEachIndexed { index, _ ->
            if (!plateNum.isNullOrEmpty() && plateNum.length > index) {
                mPlateNumBtnList[index].text = plateNum[index].toString()
            } else {
                mPlateNumBtnList[index].text = ""
            }
        }
        mSelectBtnIndex = -1
        if (plateNumColor != null)
            mPlateNumColorTypeSelect = plateNumColor

        mPlateNumColorTypeOcr = plateNumColorOcr
        updatePlateNumBtn()
    }

    fun setPlateNum(plateNumFrameView2: YcPlateNumFrameView2) {
        this.mPlateNumColorTypeOcr = plateNumFrameView2.mPlateNumColorTypeOcr
        this.mPlateNumColorTypeSelect = plateNumFrameView2.mPlateNumColorTypeSelect
        this.mSelectBtnIndex = plateNumFrameView2.mSelectBtnIndex
        updatePlateNumBtn()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initPlateNumBtn()
    }
}

