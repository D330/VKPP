package vkpp.flippy.ru.vkpp.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import vkpp.flippy.ru.vkpp.ui.fragment.MessagesPhotoFragment
import vkpp.flippy.ru.vkpp.ui.fragment.SavePhotoFragment

class PhotoTypePagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            MESSAGES_PHOTO -> MessagesPhotoFragment()
            else -> SavePhotoFragment()
        }
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            MESSAGES_PHOTO -> "Из диалогов"
            else -> "Из сохранёнок"
        }
    }

    companion object {
        private const val MESSAGES_PHOTO = 0
        private const val SAVE_PHOTO = 1
    }
}