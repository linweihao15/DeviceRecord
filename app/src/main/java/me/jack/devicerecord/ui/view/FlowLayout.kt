package me.jack.devicerecord.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import me.jack.kotlin.library.extension.ctx
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

/**
 * Created by Jack on 2018/1/21.
 */
class FlowLayout : ViewGroup {

    private val childViews = ArrayList<List<View>>()
    private var lineViews = ArrayList<View>()
    private val lineHeights = ArrayList<Int>()

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet, style: Int) : super(ctx, attrs, style)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        
        childViews.clear()
        lineViews.clear()
        lineHeights.clear()

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        Log.d(javaClass.simpleName, "onMeasure width=$widthSize, height=$heightSize")

        Log.d(javaClass.simpleName, "Parent padding left=$paddingLeft, right=$paddingRight, top=$paddingTop, bottom=$paddingBottom, start=$paddingStart, end=$paddingEnd")

        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility == GONE) continue

            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val param = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + param.leftMargin + param.rightMargin
            val childHeight = child.measuredHeight + param.topMargin + param.bottomMargin
            Log.d(javaClass.simpleName, "Child[$i] childWidth=$childWidth, childHeight=$childHeight")
            Log.d(javaClass.simpleName, "Child[$i] Margin left=${param.leftMargin}, right=${param.rightMargin}," +
                    " start=${param.marginStart}, end=${param.marginEnd}, top=${param.topMargin}, bottom=${param.bottomMargin}")
            Log.d(javaClass.simpleName, "Child[$i] Padding left=${child.paddingLeft}, right=${child.paddingRight}," +
                    " start=${child.paddingStart}, end=${child.paddingEnd}, top=${child.paddingTop}, bottom=${child.paddingBottom}")

            if (lineWidth + childWidth > widthSize - paddingLeft - paddingRight) {
                lineHeights.add(lineHeight)
                childViews.add(lineViews)
                lineViews = ArrayList()

                width = Math.max(width, lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
                Log.d(javaClass.simpleName, "Next line. width: $width, height: $height, childWidth: $lineWidth, childHeight: $lineHeight")
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
                Log.d(javaClass.simpleName, "Line width=$lineWidth, height=$lineHeight")
            }
            lineViews.add(child)

            if (i == childCount - 1) {
                lineHeights.add(lineHeight)
                childViews.add(lineViews)

                width = Math.max(width, lineWidth)
                height += lineHeight
                height += paddingTop + paddingBottom
                Log.d(javaClass.simpleName, "Last child, width=$width, height=$height")
            }
        }
        setMeasuredDimension(if (widthMode == MeasureSpec.EXACTLY) widthSize else width, if (heightMode == MeasureSpec.EXACTLY) heightSize else height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//        childViews.clear()
//        lineHeights.clear()
//        lineViews.clear()
//
//        var lineWidth = 0
//        var lineHeight = 0
//
//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//
//            if (child.visibility == GONE) continue
//
//            val lp = child.layoutParams as MarginLayoutParams
//            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
//            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
//
//            if (childWidth + lineWidth > width - paddingLeft - paddingRight) {
//                Log.d(javaClass.simpleName, "Next line. line height=$lineHeight")
//                lineHeights.add(lineHeight)
//                childViews.add(lineViews)
//                lineWidth = 0
//                lineHeight = 0
//                lineViews = ArrayList()
//            }
//            lineWidth += childWidth
//            lineHeight = Math.max(lineHeight, childHeight)
//            lineViews.add(child)
//            Log.d(javaClass.simpleName, "Add child[$i] width=$childWidth, height=$childHeight")
//            Log.d(javaClass.simpleName, "Line width=$lineWidth, height=$lineHeight")
//        }
//        lineHeights.add(lineHeight)
//        childViews.add(lineViews)
//        Log.d(javaClass.simpleName, "Last line, line height=$lineHeight")

        var left = paddingLeft
        var top = paddingTop
        childViews.forEachWithIndex { i, lines ->
            val lHeight = lineHeights[i]
            for (j in 0 until lines.size) {
                val child = lines[j]
                if (child.visibility == GONE) continue

                val lp = child.layoutParams as MarginLayoutParams
                val cLeft = left + lp.leftMargin
                val cTop = top + lp.topMargin
                val cRight = cLeft + child.measuredWidth
                val cBottom = cTop + child.measuredHeight
                child.layout(cLeft, cTop, cRight, cBottom)
                Log.d(javaClass.simpleName, "Layout. Child[$j] left=$cLeft, top=$cTop, right=$cRight, bottom=$cBottom")
                left += child.measuredWidth + lp.rightMargin + lp.marginStart
            }
            left = paddingLeft
            top += lHeight
            Log.d(javaClass.simpleName, "Next line. left=$left, top=$top")
        }

    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(ctx, attrs)
    }

}