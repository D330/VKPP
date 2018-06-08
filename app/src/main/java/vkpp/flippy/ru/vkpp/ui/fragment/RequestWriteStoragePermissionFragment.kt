package vkpp.flippy.ru.vkpp.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.TextView
import vkpp.flippy.ru.vkpp.App

abstract class RequestWriteStoragePermissionFragment : Fragment() {

    companion object {
        private const val WRITE_PERMISSION_REQUEST = 1488
    }

    private var safelyJob: () -> Unit = {}

    fun startWriteStorageJobSafely(job: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkWriteStoragePermission()) job()
            else {
                safelyJob = job
                requestWriteStoragePermission()
            }
        }
        else {
            job()
        }
    }

    private fun checkWriteStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(App.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteStoragePermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            safelyJob()
        } else {
            val grantPermissionManuallySnackBar =
                    Snackbar.make(activity!!.window.decorView.findViewById(android.R.id.content),
                            "Предоставьте разрешение на запись\nРазрешения > Память > Включить", Snackbar.LENGTH_LONG)
                            .setAction("Перейти", {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", activity!!.packageName, null))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            })
            val textView = grantPermissionManuallySnackBar.view
                    .findViewById(android.support.design.R.id.snackbar_text) as TextView
            textView.maxLines = Integer.MAX_VALUE
            grantPermissionManuallySnackBar.show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}