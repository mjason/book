package co.geekku.libs

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by mj on 13-10-27.
 */

fun JSONArray?.foreach(
        callback: (JSONObject) -> Unit
) {
    if (this != null) {
        val length = this.length().minus(1)
        for(i in 0..length) {
            val jsonObject = this.getJSONObject(i)
            if (jsonObject != null) {
                callback(jsonObject)
            }
        }
    }
}