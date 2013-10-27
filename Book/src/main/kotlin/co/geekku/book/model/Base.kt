package co.geekku.book.model

import org.json.JSONArray
import org.json.JSONObject
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.AsyncHttpClient
import org.apache.http.conn.HttpHostConnectException
import android.widget.Toast
import org.apache.http.conn.ConnectTimeoutException

/**
 * Created by mj on 13-10-21.
 */

val baseUrl: String = "http://192.168.1.100:3000"

class JSONResponseHandler<T, E>(
        okCallback: (T?) -> Unit,
        errCallback: (Throwable?, E?) -> Unit
) : JsonHttpResponseHandler() {
    val okCallBack = okCallback
    val errCallBack = errCallback

    override public open fun onSuccess(response : JSONArray?) {
        okCallBack(response as? T)
    }

    override public open fun onFailure(e: Throwable?, errorResponse: JSONArray?) {
        errCallBack(e, (errorResponse as? E))
    }

    override public open fun onSuccess(response : JSONObject?) {
        okCallBack(response as? T)
    }

    override public open fun onFailure(e: Throwable?, errorResponse: JSONObject?) {
        errCallBack(e, (errorResponse as? E))
    }

    override public open fun onFailure(e: Throwable?) {
        errCallBack(e, null)
    }
}