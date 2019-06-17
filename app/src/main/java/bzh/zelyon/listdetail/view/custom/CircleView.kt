package bzh.zelyon.listdetail.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout

class CircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var path: Path = Path()

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val centerX = measuredWidth/2f
        val centerY = measuredHeight/2f
        path.reset()
        path.moveTo(centerX, centerY)
        path.addCircle(centerX, centerY, Math.min(width/2f, height/2f) - 10f, Path.Direction.CW)
        path.close()
        setMeasuredDimension(width, height)
        invalidate()
    }
}