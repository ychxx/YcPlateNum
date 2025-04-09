package com.yc.ycplatenum

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 分割线
 */
open class Decoration(
    protected var mSpace: Int ,
    protected val mSpaceFirst: Int = mSpace,
    protected val mSpaceLast: Int = mSpace,
    protected val mIsHasBorder: Boolean = false
) : ItemDecoration() {
    protected var mLineColor = Color.parseColor("#00FFFFFF")
    protected var mIsHasColor = false //是否有颜色

    //边缘是否需要分割线；线性布局的上下/左右，表格布局的四周
    protected var mPaint: Paint? = null

    init {
        mPaint = Paint()
        mPaint!!.color = mLineColor
    }


    fun setSpacePx(space: Int) {
        mSpace = space
    }

    fun setLineColor(lineColor: Int) {
        mLineColor = lineColor
        mIsHasColor = true
        mPaint!!.color = mLineColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (!mIsHasColor) return
        super.onDrawOver(c, parent, state)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (!mIsHasColor) return
        super.onDraw(c, parent, state)
        val layoutManager = parent.layoutManager
        val childCount = parent.childCount //返回 可见 的item数量
        if (layoutManager is LinearLayoutManager) { //线性布局
//            ((LinearLayoutManager) layoutManager).getOrientation()
            // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
            @RecyclerView.Orientation val orientation = layoutManager.orientation
            for (i in 0 until childCount) {
                // 每个Item的位置
                drawLinear(parent.getChildAt(i), c, orientation)
            }
        } else if (layoutManager is StaggeredGridLayoutManager) { //表格式布局
            // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
            for (i in 0 until childCount) {
                // 每个Item的位置
                drawStaggeredGrid(parent.getChildAt(i), c)
            }
        } else if (layoutManager is GridLayoutManager) {
            for (i in 0 until childCount) {
                // 每个Item的位置
                drawStaggeredGrid(parent.getChildAt(i), c)
            }
        }
    }

    private fun drawLinear(child: View, c: Canvas, @RecyclerView.Orientation orientation: Int) {
        if (orientation == RecyclerView.HORIZONTAL) {
            if (child.left - mSpace <= 0) c.drawRect((child.left - mSpace).toFloat(), 0f, mSpace.toFloat(), child.bottom.toFloat(), mPaint!!)
            c.drawRect(child.right.toFloat(), child.top.toFloat(), (child.right + mSpace).toFloat(), child.bottom.toFloat(), mPaint!!)
        } else if (orientation == RecyclerView.VERTICAL) {
            if (child.top - mSpace <= 0)
                c.drawRect(0f, (child.top - mSpace).toFloat(), child.right.toFloat(), mSpace.toFloat(), mPaint!!)

            c.drawRect(child.left.toFloat(), child.bottom.toFloat(), child.right.toFloat(), (child.bottom + mSpace).toFloat(), mPaint!!)
        }
    }

    private fun drawStaggeredGrid(child: View, c: Canvas) {
        c.drawRect((child.left - mSpace).toFloat(), (child.top - mSpace).toFloat(), (child.right + mSpace).toFloat(), mSpace.toFloat(), mPaint!!) //上
        c.drawRect(
            (child.left - mSpace).toFloat(), child.bottom.toFloat(), (child.right + mSpace).toFloat(), (child.bottom + mSpace).toFloat(),
            mPaint!!
        ) //下
        c.drawRect(
            (child.left - mSpace).toFloat(), (child.top - mSpace).toFloat(), child.left.toFloat(), (child.bottom + mSpace).toFloat(),
            mPaint!!
        ) //左
        c.drawRect(
            child.right.toFloat(), (child.top - mSpace).toFloat(), (child.right + mSpace).toFloat(), (child.bottom + mSpace).toFloat(),
            mPaint!!
        ) //右
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        val position = parent.getChildAdapterPosition(view)
        if (layoutManager is StaggeredGridLayoutManager) {//表格式布局(瀑布流)
            itemOffsetsStaggeredGrid(outRect, position, layoutManager.spanCount)
        } else if (layoutManager is GridLayoutManager) {//表格式布局(高相等，或宽相等，一行一行的)
            itemOffsetsGridLayoutManager(outRect, position, layoutManager.spanCount, layoutManager.orientation)
        } else if (layoutManager is LinearLayoutManager) { //线性布局
            @RecyclerView.Orientation val orientation = layoutManager.orientation
            itemOffsetsLinear(outRect, orientation, position, layoutManager.itemCount)
        }
    }

    private fun itemOffsetsLinear(outRect: Rect, @RecyclerView.Orientation orientation: Int, position: Int, itemCount: Int) {
        if (itemCount == 1) {
            if (mIsHasBorder) {
                if (orientation == RecyclerView.HORIZONTAL) {
                    outRect.left = mSpaceFirst
                    outRect.right = mSpaceLast
                } else {
                    outRect.top = mSpaceFirst
                    outRect.bottom = mSpaceLast
                }
            }
        }
        if (position == 0) {
            if (orientation == RecyclerView.HORIZONTAL) {
                if (mIsHasBorder) {
                    outRect.left = mSpaceFirst
                }
                outRect.right = mSpace / 2
            } else {
                if (mIsHasBorder) {
                    outRect.top = mSpaceFirst
                }
                outRect.bottom = mSpace / 2
            }
        } else if (position == itemCount - 1) {
            if (orientation == RecyclerView.HORIZONTAL) {
                if (mIsHasBorder) {
                    outRect.right = mSpaceLast
                }
                outRect.left = mSpace / 2
            } else {
                if (mIsHasBorder) {
                    outRect.bottom = mSpaceLast
                }
                outRect.top = mSpace / 2
            }
        } else {
            if (orientation == RecyclerView.HORIZONTAL) {
                outRect.left = mSpace / 2
                outRect.right = mSpace / 2
            } else if (orientation == RecyclerView.VERTICAL) {
                outRect.bottom = mSpace / 2
                outRect.top = mSpace / 2
            }
        }
    }

    private fun itemOffsetsGridLayoutManager(outRect: Rect, position: Int, verticalNum: Int, @RecyclerView.Orientation orientation: Int) {
        if (orientation == RecyclerView.HORIZONTAL) {
            outRect.left = mSpace
        } else if (orientation == RecyclerView.VERTICAL) {
            if (position / verticalNum > 0) {
                outRect.top = mSpace
            }
            if (verticalNum == 2) {
                if (position % verticalNum == 0) {
                    outRect.left = mSpace
                    outRect.right = mSpace / 2
                } else {
                    outRect.left = mSpace / 2
                    outRect.right = mSpace
                }
            } else {
                if (position % verticalNum == 0) {
                    outRect.left = mSpace
                } else if (position % verticalNum == verticalNum - 1) {
                    outRect.right = mSpace
                } else {
                    outRect.left = mSpace / 2
                    outRect.right = mSpace / 2
                }
            }
        }
    }

    private fun itemOffsetsStaggeredGrid(outRect: Rect, position: Int, verticalNum: Int) {
        //设置列间距
        val itemReallySumSpace: Int = if (mIsHasBorder) {
            //设置行间距
            if (position < verticalNum) {
                outRect.top = mSpace
            }
            outRect.bottom = mSpace
            ((verticalNum + 1) * mSpace * 1.0 / verticalNum).toInt()
        } else {
            //设置行间距
            if (position >= verticalNum) {
                outRect.top = mSpace
            }
            ((verticalNum - 1) * mSpace * 1.0 / verticalNum).toInt()
        }
        if (itemReallySumSpace == 0) return
        var left = mSpace
        var right = itemReallySumSpace - left
        val horizontalPosition = position % verticalNum
        for (i in 0 until horizontalPosition) {
            left = mSpace - right
            right = itemReallySumSpace - left
        }
        outRect.left = left
        outRect.right = right
    }

}
