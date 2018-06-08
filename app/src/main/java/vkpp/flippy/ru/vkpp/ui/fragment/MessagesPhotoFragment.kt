package vkpp.flippy.ru.vkpp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
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

class MessagesPhotoFragment : RequestWriteStoragePermissionFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_messages_photo, container, false)
        val startBtn = rootView.findViewById<Button>(R.id.startBtn)
        startBtn.setOnClickListener { startWriteStorageJobSafely { downloadPhotosFromDialogs() } }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun downloadPhotosFromDialogs() {
        val token = Prefs.getInstance().token
        val processDialog = AlertDialog.Builder(activity!!)
                .setView(R.layout.dialog_process)
                .setCancelable(false)
                .show()
        val processProgressBar = processDialog.findViewById<ProgressBar>(R.id.process_progressBar)!!
        val processProgressInfo = processDialog.findViewById<TextView>(R.id.process_progress_info)!!

        processProgressBar.isIndeterminate = true
        processProgressInfo.text = "Грузим диалоги..."

        launch(UI) {
            val client = OkHttpClient().newBuilder().build()
            val dialogsRequest = Request.Builder()
                    .url("https://api.vk.com/method/messages.getDialogs?count=200&v=5.74&access_token=$token")
                    .build()
            val dialogsResponse = async { client.newCall(dialogsRequest).execute().body()!!.string() }.await()
            val dialogs = JSONObject(dialogsResponse).getJSONObject("response").getJSONArray("items")
            val userIds = ArrayList<Long>()
            for (i in 0 until dialogs.length()) {
                val dialog = dialogs.getJSONObject(i)
                val userId = dialog.getJSONObject("message").getLong("user_id")
                userIds.add(userId)
            }
            val dialogsCount = userIds.size
            processProgressBar.isIndeterminate = false
            processProgressBar.max = dialogsCount
            val photoLinks = ArrayList<String>()
            userIds.forEachIndexed { dialogIndex, userId ->
                processProgressInfo.text = "Получаем фотографии из диалогов (${dialogIndex + 1}/$dialogsCount)"
                processProgressBar.progress = dialogIndex + 1
                val photosRequest = Request.Builder()
                        .url("https://api.vk.com/method/messages.getHistoryAttachments?peer_id=$userId&count=200&media_type=photo&v=5.74&access_token=$token")
                        .build()
                val photosResponse = async { client.newCall(photosRequest).execute().body()!!.string() }.await()
                val photos = JSONObject(photosResponse).getJSONObject("response").getJSONArray("items")
                for (i in 0 until photos.length()) {
                    val attachment = photos.getJSONObject(i)
                    val photo = attachment.getJSONObject("attachment").getJSONObject("photo")
                    val maxQualityPhotoLink = VKUtils.getMaxQualityPhotoLink(photo)
                    photoLinks.add(maxQualityPhotoLink)
                }
                async { Thread.sleep(500) }.await()
            }
            val targetDirectoryPath = "${Environment.getExternalStorageDirectory()}/VKPhotos/Dialogs/"
            val targetDirectory = File(targetDirectoryPath)
            if (!targetDirectory.exists()) targetDirectory.mkdirs()
            val photosCount = photoLinks.size
            processProgressBar.progress = 0
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