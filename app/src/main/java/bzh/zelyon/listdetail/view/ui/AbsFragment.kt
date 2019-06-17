package bzh.zelyon.listdetail.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class AbsFragment: Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        integrateOnClick(view)
    }

    private fun integrateOnClick(view: View) {
        if (view.isClickable && !view.hasOnClickListeners()) {
            view.setOnClickListener { onIdClick(it.id) }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                integrateOnClick(view.getChildAt(i))
            }
        }
    }

    abstract fun getLayoutId(): Int

    abstract fun onIdClick(id: Int)
}