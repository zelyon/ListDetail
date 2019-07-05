package bzh.zelyon.listdetail.view.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import bzh.zelyon.listdetail.R

abstract class AbsToolBarFragment: AbsFragment() {

    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setSupportActionBar(view.findViewById(R.id.toolbar))
        mainActivity.supportActionBar?.title = getTitle()
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(showBack())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (getIdMenu() != -1) {
            inflater.inflate(getIdMenu(), menu)
            this.menu = menu
            onUpdateMenu()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mainActivity.supportFragmentManager.popBackStack()
        } else {
            onIdClick(item.itemId)
        }
        return super.onOptionsItemSelected(item)
    }

    abstract fun getTitle(): String

    open fun showBack() = false

    open fun getIdMenu() = -1

    open fun onUpdateMenu() {}
}