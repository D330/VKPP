package vkpp.flippy.ru.vkpp

import org.json.JSONObject

object VKUtils {
    fun getMaxQualityPhotoLink(photo: JSONObject): String {
        val keys = photo.keys()
        var maxQuality = 0
        keys.forEach { key ->
            if (key.startsWith("photo_")) {
                val quality = key.split("_")[1].toInt()
                maxQuality = Math.max(quality, maxQuality)
            }
        }
        return photo.getString("photo_$maxQuality")
    }
}