package bzh.zelyon.listdetail.view.custom

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import bzh.zelyon.listdetail.util.dpToPixel

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

    private var topMarg = 0
    private var bottomMarg = 0
    private var leftMarg = 0
    private var rightMarg = 0

    init {
        if (width != MATCH && width != WRAP) {
            width = context.dpToPixel(width)
        }
        if (height != MATCH && height != WRAP) {
            height = context.dpToPixel(height)
        }
    }

    fun topMargin(topMargin: Int): ViewParams {
        this.topMarg = context.dpToPixel(topMargin)
        return this
    }

    fun bottomMargin(bottomMargin: Int): ViewParams {
        this.bottomMarg = context.dpToPixel(bottomMargin)
        return this
    }

    fun leftMargin(leftMargin: Int): ViewParams {
        this.leftMarg = context.dpToPixel(leftMargin)
        return this
    }

    fun rightMargin(rightMargin: Int): ViewParams {
        this.rightMarg = context.dpToPixel(rightMargin)
        return this
    }

    fun margins(margins: Int): ViewParams {
        return topMargin(margins).bottomMargin(margins).rightMargin(margins).leftMargin(margins)
    }

    fun margins(verticalMargins: Int, horizontalMargins: Int): ViewParams {
        return topMargin(verticalMargins).bottomMargin(verticalMargins).rightMargin(horizontalMargins).leftMargin(horizontalMargins)
    }

    fun margins(topMargin: Int, bottomMargin: Int, leftMargin: Int, rightMargin: Int): ViewParams {
        return topMargin(topMargin).bottomMargin(bottomMargin).rightMargin(rightMargin).leftMargin(leftMargin)
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

    fun linear(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height, weight).apply {
        if (topMarg != 0) {
            topMargin = topMarg
        }
        if (bottomMarg != 0) {
            bottomMargin = bottomMarg
        }
        if (leftMarg != 0) {
            leftMargin = leftMarg
        }
        if (rightMarg != 0) {
            rightMargin = rightMarg
        }
        when {
            centerGravity -> gravity = Gravity.CENTER
            centerHorizontalGravity -> gravity = Gravity.CENTER_HORIZONTAL
            centerVerticalGrativity -> gravity = Gravity.CENTER_VERTICAL
        }
        when {
            topGravity -> gravity = Gravity.TOP
            bottomGravity -> gravity = Gravity.BOTTOM
        }
        when {
            leftGravity -> gravity = gravity or Gravity.LEFT
            rightGravity -> gravity = gravity or Gravity.RIGHT
        }
    }


    fun relative(): RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, height).apply {
        if (topMarg != 0) {
            topMargin = topMarg
        }
        if (bottomMarg != 0) {
            bottomMargin = bottomMarg
        }
        if (leftMarg != 0) {
            leftMargin = leftMarg
        }
        if (rightMarg != 0) {
            rightMargin = rightMarg
        }
        when {
            centerGravity -> addRule(RelativeLayout.CENTER_IN_PARENT)
            centerHorizontalGravity -> addRule(RelativeLayout.CENTER_HORIZONTAL)
            centerVerticalGrativity -> addRule(RelativeLayout.CENTER_VERTICAL)
        }
        when {
            topGravity -> addRule(RelativeLayout.ALIGN_PARENT_TOP)
            bottomGravity -> addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
        when {
            leftGravity -> addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            rightGravity -> addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
    }
}