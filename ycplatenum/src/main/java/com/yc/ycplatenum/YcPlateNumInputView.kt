package com.yc.ycplatenum

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.yc.ycplatenum.databinding.YcPlateNumViewBinding

/**
 * 输入框容器
 */
open class YcPlateNumInputView : YcPlateNumFrameBase {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var mViewBinding: YcPlateNumViewBinding

    /**
     * 给外部弹窗用
     */
    var mPlateNumClickOutCall: (() -> Unit)? = null

    private lateinit var mPlateNumColorAdapter: YcPlateNumColorAdapter
    private lateinit var mKeyboardAdapter: YcKeyboardAdapter

    var mCloseCall: ((YcPlateNumViewBean) -> Unit)? = null

    fun showKeyboard(hasShow: Boolean) {
        mViewBinding.deleteBtn.visibility = if (hasShow) VISIBLE else GONE
        mViewBinding.closeBtn.visibility = if (hasShow) VISIBLE else GONE
        mViewBinding.keyboardRv.visibility = if (hasShow) VISIBLE else GONE
    }

    override fun initView(context: Context, attrs: AttributeSet?) {
        super.initView(context, attrs)
        mViewBinding = YcPlateNumViewBinding.inflate(LayoutInflater.from(context), this, false)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum0)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum1)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum2)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum3)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum4)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum5)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.plateNum6)
        mPlateNumBtnList.add(mViewBinding.plateNumFrameInclude.energyBtn)
        mKeyboardAdapter = YcKeyboardAdapter()
        mKeyboardAdapter.mItemClick = {
            addKeyboardDate(it, YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DATA)
        }
        mViewBinding.keyboardRv.adapter = mKeyboardAdapter
        mViewBinding.keyboardRv.layoutManager = GridLayoutManager(context, 8)
        mViewBinding.keyboardRv.addItemDecoration(Decoration((6 * resources.displayMetrics.density + 0.5).toInt()))
        mViewBinding.deleteBtn.setOnClickListener {
            addKeyboardDate("", YcPlateNumData.PLATE_NUM_KEYBOARD_TYPE_DEL)
        }
        mPlateNumColorAdapter = YcPlateNumColorAdapter().apply {
            mItemClickCall = {
                mPlateNumViewBean.plateNumColorSelect = it
                updatePlateNumDate(KEY_COLOR_CHANGE_SOURCE_COLOR)
            }
        }
        mPlateNumColorAdapter.addAllData(YcPlateNumData.PLATE_NUM_COLOR_LIST)
        mViewBinding.plateNumColorRv.adapter = mPlateNumColorAdapter
        mViewBinding.plateNumColorRv.layoutManager = GridLayoutManager(context, 3)
        mViewBinding.plateNumColorRv.addItemDecoration(Decoration((10 * resources.displayMetrics.density + 0.5).toInt()))
        mViewBinding.closeBtn.setOnClickListener {
            mCloseCall?.invoke(mPlateNumViewBean)
        }
        initPlateNumBtn()
        addView(mViewBinding.root)
    }

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

    override var mPlateNumFrameClick: (() -> Unit)? = {
        updateKeyboardData(mPlateNumViewBean.mPlateNumSelectIndex)
        mPlateNumClickOutCall?.invoke()
    }


    /**
     * @param changeSource 改变来源 0：车牌号输入 1：车牌颜色选择
     */
    override fun updatePlateNumDate(changeSource: Int) {
        var plateNum = ""
        mPlateNumBtnList.forEach {
            plateNum += it.text.toString()
        }
        mViewBinding.plateNumFrameInclude.energyMaskTv.visibility = if (mViewBinding.plateNumFrameInclude.energyBtn.text.isNullOrEmpty()) VISIBLE else GONE
        //手动输入时，根据车牌号推断车牌颜色
        if (changeSource == KEY_COLOR_CHANGE_SOURCE_INPUT) {
            val plateNumColorMaybeList = YcPlateNumData.getPlateNumColorMaybe(mPlateNumViewBean.plateNumColorSelectOcr, plateNum)
            if (!plateNumColorMaybeList.isNullOrEmpty()) {
                mPlateNumViewBean.plateNumColorSelect = plateNumColorMaybeList!![0]
            }

        }
        if (changeSource != KEY_COLOR_CHANGE_SOURCE_COLOR) {
            mPlateNumColorAdapter.setSelectData(mPlateNumViewBean.plateNumColorSelect)
        }
        updateBtnSetting()
        updateKeyboardData(mPlateNumViewBean.mPlateNumSelectIndex)
    }

    fun updateKeyboardData(plateNumIndex: Int) {
        when (plateNumIndex) {
            0 -> {
                mKeyboardAdapter.addAllData(YcPlateNumData.PLATE_NUM_CONTENT_PROVINCE.toList())
            }

            1 -> {
                mKeyboardAdapter.addAllData(YcPlateNumData.PLATE_NUM_CONTENT_LETTER.toList())
            }

            6, 7 -> {
                mKeyboardAdapter.addAllData((YcPlateNumData.PLATE_NUM_CONTENT_DIGIT + YcPlateNumData.PLATE_NUM_CONTENT_LETTER + YcPlateNumData.PLATE_NUM_CONTENT_SPECIAL).toList())
            }

            else -> {
                mKeyboardAdapter.addAllData((YcPlateNumData.PLATE_NUM_CONTENT_DIGIT + YcPlateNumData.PLATE_NUM_CONTENT_LETTER).toList())
            }
        }
    }

    override fun setPlateNum(plateNumViewBean: YcPlateNumViewBean) {
        mPlateNumColorAdapter.setOcrData(plateNumViewBean.plateNumColorSelectOcr, mPlateNumViewBean.plateNumColorSelect)
        super.setPlateNum(plateNumViewBean)
    }
}

