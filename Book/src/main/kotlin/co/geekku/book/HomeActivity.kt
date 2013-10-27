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
import co.geekku.book.model.*
import android.app.ProgressDialog
import org.apache.http.conn.HttpHostConnectException
import org.apache.http.conn.ConnectTimeoutException
import co.geekku.libs.*

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
        val progressDialog = ProgressDialog.show(this@HomeActivity, "温馨提醒", "获取数据中...", true, false)

        client.get(
                this,
                "${baseUrl}/api/v1/books.json?token=${token}",
                JSONResponseHandler<JSONArray, JSONObject>(
                        {
                            progressDialog.dismiss()
                            it.foreach {
                                listArray.add(it.getString("name")!!)
                            }

                            val arrayAdpter = ArrayAdapter(this@HomeActivity, android.R.layout.simple_expandable_list_item_1, listArray)
                            listView.setAdapter(arrayAdpter)
                        },
                        { e, res ->
                            progressDialog.dismiss()
                            when (e) {
                                is HttpHostConnectException -> {
                                    Toast.makeText(this@HomeActivity, "无法连接", Toast.LENGTH_SHORT).show()
                                }
                                is ConnectTimeoutException -> {
                                    Toast.makeText(this@HomeActivity, "连接超时", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@HomeActivity, res?.getString("error"), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                )
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
            val progressDialog = ProgressDialog.show(this@HomeActivity, "温馨提醒", "添加图书中...", true, false)
            val bundle = data.getExtras()
            val scanResult = bundle?.getString("SCAN_RESULT")
            val params = RequestParams()
            params.put("isbn", scanResult)
            client.post(
                    this,
                    "${baseUrl}/api/v1/books.json?token=${token}",
                    params,
                    JSONResponseHandler<JSONObject, JSONObject>(
                            {
                                progressDialog.dismiss()
                                val message = it?.getString("message")
                                Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
                                relaodListView()
                            },
                            { e, res ->
                                progressDialog.dismiss()
                                when (e) {
                                    is HttpHostConnectException -> {
                                        Toast.makeText(this@HomeActivity, "无法连接", Toast.LENGTH_SHORT).show()
                                    }
                                    is ConnectTimeoutException -> {
                                        Toast.makeText(this@HomeActivity, "连接超时", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        Toast.makeText(this@HomeActivity, res?.getString("error"), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    )
            )
        }
    }
}