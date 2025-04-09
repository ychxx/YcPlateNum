package com.yc.ycplatenum

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yc.ycplatenum.databinding.YcPlateNumColorItem2Binding

class YcPlateNumColorAdapter : RecyclerView.Adapter<YcPlateNumColorAdapter.Holder>() {
    inner class Holder(val viewBinding: YcPlateNumColorItem2Binding) : RecyclerView.ViewHolder(viewBinding.root) {
    }

    open var mData: MutableList<String> = mutableListOf()
        protected set
    var mSelect: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_ERROR
    private var mPlateNumColorSelectOcr: @YcPlateNumData.YcPlateNumColorType String = YcPlateNumData.PLATE_NUM_TYPE_ERROR
    var mPlateNum: String = ""


    var mItemClickCall: ((@YcPlateNumData.YcPlateNumColorType String) -> Unit)? = null
    fun setData(select: @YcPlateNumData.YcPlateNumColorType String, plateNum: String, list: List<@YcPlateNumData.YcPlateNumColorType String>) {
        mSelect = select
        mPlateNum = plateNum
        mData.clear()
        mData.addAll(list)
    }

    fun setOcrData(ocrColor: @YcPlateNumData.YcPlateNumColorType String, select: @YcPlateNumData.YcPlateNumColorType String) {
        mPlateNumColorSelectOcr = ocrColor
        setSelectData(select)
    }

    fun setSelectData(select: @YcPlateNumData.YcPlateNumColorType String) {
        mSelect = select
        notifyDataSetChanged()
    }
    fun addAllData(data: List<String>?, isClear: Boolean = true) {
        if (isClear)
            mData.clear()
        if (data == null) {
            mData.clear()
        } else {
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(YcPlateNumColorItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var recyclerViewOrNull: RecyclerView? = null
        private set
    val recyclerView: RecyclerView
        get() {
            checkNotNull(recyclerViewOrNull) {
                "Please get it after onAttachedToRecyclerView()"
            }
            return recyclerViewOrNull!!
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewOrNull = recyclerView
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dataBean = mData[position]
        holder.itemView.rootView.setOnClickListener {
            if (YcPlateNumData.hasChangeColor(mPlateNumColorSelectOcr, dataBean)) {
                mSelect = dataBean
                mItemClickCall?.invoke(dataBean)
                notifyDataSetChanged()
            } else {
                Toast.makeText(recyclerView.context, "识别非黑/白牌车，禁止修改！", Toast.LENGTH_SHORT).show()
            }
        }
        holder.viewBinding.apply {
            plateNumColorCb.text = dataBean
            plateNumColorCb.isChecked = mSelect == dataBean
            plateNumColorCb.isEnabled = YcPlateNumData.hasChangeColor(mPlateNumColorSelectOcr, dataBean)
        }
    }
}
