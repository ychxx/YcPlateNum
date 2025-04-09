package com.yc.ycplatenum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yc.ycplatenum.databinding.YcPlateNumKeyboardItemBinding

class YcKeyboardAdapter : RecyclerView.Adapter<YcKeyboardAdapter.Holder>() {
    inner class Holder(val viewBinding: YcPlateNumKeyboardItemBinding) : RecyclerView.ViewHolder(viewBinding.root)

    open var mData: MutableList<Char> = mutableListOf()
        protected set

    var mItemClickCall: ((@YcPlateNumData.YcPlateNumColorType String) -> Unit)? = null
    fun addAllData(data: List<Char>?, isClear: Boolean = true) {
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
        return Holder(YcPlateNumKeyboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var mItemClick: ((String) -> Unit)? = null
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dataBean = mData[position]
        holder.itemView.rootView.setOnClickListener {
            mItemClick?.invoke(dataBean.toString())
        }
        holder.viewBinding.apply {
            keyboardBtn.text = dataBean.toString()
        }

    }
}
