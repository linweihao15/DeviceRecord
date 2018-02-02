package me.jack.devicerecord.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.forEachChildWithIndex

/**
 * Created by Jack on 2017/12/31.
 */
class RecyclerViewDivider : RecyclerView.ItemDecoration {

    private val ATTRS = intArrayOf(android.R.attr.listDivider)
    private val DIVIDER_HEIGHT = 1

    private var mOrientation = LinearLayoutManager.VERTICAL
    private var mPaint: Paint? = null
    private var mDivider: Drawable? = null
    private var mDividerHeight: Int = DIVIDER_HEIGHT

    /**
     * Default
     */
    constructor(ctx: Context, orientation: Int) {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw IllegalArgumentException("Invalid orientation")
        }
        mOrientation = orientation
        val a = ctx.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    /**
     * Custom drawable
     */
    constructor(ctx: Context, orientation: Int, @DrawableRes drawableId: Int) : this(ctx, orientation) {
        mDivider = ContextCompat.getDrawable(ctx, drawableId)
        mDividerHeight = mDivider?.intrinsicHeight ?: DIVIDER_HEIGHT
    }

    /**
     * Custom height and color
     */
    constructor(ctx: Context, orientation: Int, dividerHeight: Int, dividerColor: Int) : this(ctx, orientation) {
        mDividerHeight = dividerHeight
        mPaint = Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = dividerColor
            style = Paint.Style.FILL
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect?.set(0, 0, 0, mDividerHeight)
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawHorizontal(c, parent)
        } else {
            drawVertical(c, parent)
        }
    }

    private fun drawHorizontal(c: Canvas?, parent: RecyclerView?) {
        parent?.let {
            val left = it.paddingLeft
            val right = it.measuredWidth - it.paddingRight
            it.forEachChildWithIndex { i, child ->
                if (i == it.childCount - 1) return@forEachChildWithIndex
                val param = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + param.bottomMargin
                val bottom = top + mDividerHeight
                mDivider?.let { divider ->
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(c)
                }
                mPaint?.let { paint ->
                    c?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                }
            }
        }
    }

    private fun drawVertical(c: Canvas?, parent: RecyclerView?) {
        parent?.let {
            val top = it.paddingTop
            val bottom = it.measuredHeight - it.paddingBottom
            it.forEachChildWithIndex { i, child ->
                if (i == it.childCount - 1) return@forEachChildWithIndex
                val param = child.layoutParams as RecyclerView.LayoutParams
                val left = child.right + param.rightMargin
                val right = left + mDividerHeight
                mDivider?.let { divider ->
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(c)
                }
                mPaint?.let { paint ->
                    c?.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                }
            }
        }
    }

}