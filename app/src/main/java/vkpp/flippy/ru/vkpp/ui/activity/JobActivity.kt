package vkpp.flippy.ru.vkpp.ui.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import vkpp.flippy.ru.vkpp.R
import vkpp.flippy.ru.vkpp.ui.adapter.PhotoTypePagerAdapter

class JobActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val photoTypeTabLayout = findViewById<TabLayout>(R.id.photoTypeTabLayout)
        val photoTypeViewPager = findViewById<ViewPager>(R.id.photoTypeViewPager)
        photoTypeViewPager.offscreenPageLimit = 1
        photoTypeViewPager.adapter = PhotoTypePagerAdapter(supportFragmentManager)
        photoTypeTabLayout.setupWithViewPager(photoTypeViewPager)
    }
}