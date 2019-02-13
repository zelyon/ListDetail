package bzh.zelyon.listdetail.utils

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import bzh.zelyon.listdetail.dpToPixel

class ViewParams (val context: Context, private var width: Int = MATCH, private var height: Int = WRAP, private var weight: Float = -1f) {

    companion object {

        const val MATCH = MATCH_PARENT
        const val WRAP = WRAP_CONTENT
    }

    private var topGravity = false
    private var bottomGravity = false
    private var leftGravity = false
    private var rightGravity = false
    private var centerGravity = false
    private var centerHorizontalGravity = false
    private var centerVerticalGrativity = false

    private var topMargin = 0
    private var bottomMargin = 0
    private var leftMargin = 0
    private var rightMargin = 0

    init {

        if(width != MATCH && width != WRAP) {

            width = context.dpToPixel(width)
        }
        if(height != MATCH && height != WRAP) {

            height = context.dpToPixel(height)
        }
    }

    fun topMargin(topMargin: Int): ViewParams {

        this.topMargin = context.dpToPixel(topMargin)

        return this
    }

    fun bottomMargin(bottomMargin: Int): ViewParams {

        this.bottomMargin = context.dpToPixel(bottomMargin)

        return this
    }

    fun leftMargin(leftMargin: Int): ViewParams {

        this.leftMargin = context.dpToPixel(leftMargin)

        return this
    }

    fun rightMargin(rightMargin: Int): ViewParams {

        this.rightMargin = context.dpToPixel(rightMargin)

        return this
    }

    fun margins(margins: Int): ViewParams {

        this.topMargin = context.dpToPixel(margins)
        this.bottomMargin = context.dpToPixel(margins)
        this.leftMargin = context.dpToPixel(margins)
        this.rightMargin = context.dpToPixel(margins)

        return this
    }

    fun margins(verticalMargins: Int, horizontalMargins: Int): ViewParams {

        this.topMargin = context.dpToPixel(verticalMargins)
        this.bottomMargin = context.dpToPixel(verticalMargins)
        this.leftMargin = context.dpToPixel(horizontalMargins)
        this.rightMargin = context.dpToPixel(horizontalMargins)

        return this
    }

    fun margins(topMargin: Int, bottomMargin: Int, leftMargin: Int, rightMargin: Int): ViewParams {

        this.topMargin = context.dpToPixel(topMargin)
        this.bottomMargin = context.dpToPixel(bottomMargin)
        this.leftMargin = context.dpToPixel(leftMargin)
        this.rightMargin = context.dpToPixel(rightMargin)

        return this
    }

    fun topGravity(): ViewParams {

        topGravity = true
        bottomGravity = false

        return this
    }

    fun bottomGravity(): ViewParams {

        topGravity = false
        bottomGravity = true

        return this
    }

    fun leftGravity(): ViewParams {

        leftGravity = true
        rightGravity = false

        return this
    }

    fun rightGravity(): ViewParams {

        leftGravity = false
        rightGravity = true

        return this
    }

    fun centerGravity(): ViewParams {

        centerGravity = true

        return this
    }

    fun centerHorizontalGravity(): ViewParams {

        centerHorizontalGravity = true
        centerVerticalGrativity = false

        return this
    }

    fun centerVerticalGrativity(): ViewParams {

        centerHorizontalGravity = false
        centerVerticalGrativity = true

        return this
    }

    fun linear(): LinearLayout.LayoutParams {

        val result = LinearLayout.LayoutParams(width, height, weight)

        if (topMargin != 0) {

            result.topMargin = topMargin
        }

        if (bottomMargin != 0) {

            result.bottomMargin = bottomMargin
        }

        if (leftMargin != 0) {

            result.leftMargin = leftMargin
        }

        if (rightMargin != 0) {

            result.rightMargin = rightMargin
        }

        when {
            centerGravity -> result.gravity = Gravity.CENTER
            centerHorizontalGravity -> result.gravity = Gravity.CENTER_HORIZONTAL
            centerVerticalGrativity -> result.gravity = Gravity.CENTER_VERTICAL
        }

        when {
            topGravity -> result.gravity = Gravity.TOP
            bottomGravity -> result.gravity = Gravity.BOTTOM
        }

        when {
            leftGravity -> result.gravity = result.gravity or Gravity.LEFT
            rightGravity -> result.gravity = result.gravity or Gravity.RIGHT
        }

        return result
    }


    fun relative(): RelativeLayout.LayoutParams {

        val result = RelativeLayout.LayoutParams(width, height)

        if (topMargin != 0) {

            result.topMargin = topMargin
        }

        if (bottomMargin != 0) {

            result.bottomMargin = bottomMargin
        }

        if (leftMargin != 0) {

            result.leftMargin = leftMargin
        }

        if (rightMargin != 0) {

            result.rightMargin = rightMargin
        }

        when {
            centerGravity -> result.addRule(RelativeLayout.CENTER_IN_PARENT)
            centerHorizontalGravity -> result.addRule(RelativeLayout.CENTER_HORIZONTAL)
            centerVerticalGrativity -> result.addRule(RelativeLayout.CENTER_VERTICAL)
        }

        when {
            topGravity -> result.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            bottomGravity -> result.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }

        when {
            leftGravity -> result.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            rightGravity -> result.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }

        return result
    }
}