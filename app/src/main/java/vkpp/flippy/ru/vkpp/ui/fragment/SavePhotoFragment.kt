package vkpp.flippy.ru.vkpp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import vkpp.flippy.ru.vkpp.R
import vkpp.flippy.ru.vkpp.data.Prefs
import vkpp.flippy.ru.vkpp.VKUtils
import java.io.File
import java.util.*

class SavePhotoFragment : RequestWriteStoragePermissionFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_saves_photo, container, false)
        val startBtn = rootView.findViewById<Button>(R.id.startBtn)
        startBtn.setOnClickListener { startWriteStorageJobSafely { downloadPhotosFromSaves() } }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun downloadPhotosFromSaves() {
        val token = Prefs.getInstance().token
        val processDialog = AlertDialog.Builder(activity!!)
                .setView(R.layout.dialog_process)
                .setCancelable(false)
                .show()
        val processProgressBar = processDialog.findViewById<ProgressBar>(R.id.process_progressBar)!!
        val processProgressInfo = processDialog.findViewById<TextView>(R.id.process_progress_info)!!

        processProgressBar.isIndeterminate = true
        processProgressInfo.text = "Грузим сохранёнки..."

        launch(UI) {
            val client = OkHttpClient().newBuilder().build()
            val savesRequest = Request.Builder()
                    .url("https://api.vk.com/method/photos.get?album_id=saved&count=1000&v=5.74&access_token=$token")
                    .build()
            val savesResponse = async { client.newCall(savesRequest).execute().body()!!.string() }.await()
            Log.d("XXX", savesResponse)
            val saves = JSONObject(savesResponse).getJSONObject("response").getJSONArray("items")
            val photoLinks = ArrayList<String>()
            for (savesIndex in 0 until saves.length()) {
                val savedPhoto = saves.getJSONObject(savesIndex)
                val maxQualityPhotoLink = VKUtils.getMaxQualityPhotoLink(savedPhoto)
                photoLinks.add(maxQualityPhotoLink)
            }
            val targetDirectoryPath = "${Environment.getExternalStorageDirectory()}/VKPhotos/Saves/"
            val targetDirectory = File(targetDirectoryPath)
            if (!targetDirectory.exists()) targetDirectory.mkdirs()
            val photosCount = photoLinks.size
            processProgressBar.isIndeterminate = false
            processProgressBar.max = photosCount
            photoLinks.forEachIndexed { photoIndex, photoLink ->
                processProgressInfo.text = "Качаем фотографии (${photoIndex + 1}/$photosCount)"
                processProgressBar.progress = photoIndex + 1
                val photoFile = File("$targetDirectoryPath$photoIndex.jpg")
                val downloadRequest = Request.Builder()
                        .url(photoLink)
                        .build()
                async {
                    val inputStream = client.newCall(downloadRequest).execute().body()!!.byteStream()
                    photoFile.writeBytes(inputStream.readBytes())
                }.await()
            }
            processDialog.dismiss()
        }
    }
}