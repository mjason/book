package co.geekku.book

/**
 * Created by mj on 13-10-21.
 */

import android.app.Activity
import android.os.Bundle
import android.view.MenuInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import com.loopj.android.http.*
import org.json.JSONObject
import org.json.JSONArray
import android.widget.ListView
import android.widget.ArrayAdapter
import android.util.Log
import java.util.ArrayList
import co.geekku.book.model.baseUrl

public open class HomeActivity() : Activity() {

    var token: String? = ""
    val client = AsyncHttpClient()

    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home)
        token = getSharedPreferences("config", 0)?.getString("token", "")
        relaodListView()

    }

    public fun relaodListView(): Unit {

        val listView = findViewById(R.id.listView) as ListView
        val listArray = ArrayList<String>()

        client.get(
                this,
                "${baseUrl}/api/v1/books.json?token=${token}",
                object: JsonHttpResponseHandler() {
                    override public fun onSuccess(resp: JSONArray?) {
                        if(resp!!.length() == 0) {
                            return
                        }
                        for (i in 0..resp!!.length().minus(1)) {
                            listArray.add(resp!!.getJSONObject(i)?.getString("name")!!)
                            Log.i("array", listArray.size.toString())
                        }

                        val arrayAdpter = ArrayAdapter(this@HomeActivity, android.R.layout.simple_expandable_list_item_1, listArray)
                        listView.setAdapter(arrayAdpter)
                    }
                }
        )
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater()?.inflate(R.menu.main, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.getItemId()) {
            R.id.action_plus -> {
                IntentIntegrator.initiateScan(this)
                return true
            }
            else -> {
                Toast.makeText(this, "没有这个方法", Toast.LENGTH_SHORT).show()
                return super.onOptionsItemSelected(item);
            }
        }
    }

    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            val bundle = data.getExtras()
            val scanResult = bundle?.getString("SCAN_RESULT")
            val params = RequestParams()
            params.put("isbn", scanResult)
            client.post(
                    this,
                    "${baseUrl}/api/v1/books.json?token=${token}",
                    params,
                    object: JsonHttpResponseHandler() {
                        override public fun onSuccess(resp: JSONObject?) {
                            val message = resp?.getString("message")
                            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
                            relaodListView()
                        }
                    }
            )
        }
    }
}