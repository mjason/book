package co.geekku.book

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.loopj.android.http.*
import org.json.JSONObject
import android.content.Intent
import co.geekku.book.model.baseUrl
import co.geekku.book.model.JSONResponseHandler
import android.app.ProgressDialog
import com.sun.jmx.snmp.tasks.Task
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.conn.HttpHostConnectException


public open class MainActivity() : Activity() {

    public fun getEditTextString(id: Int): String = (findViewById(id) as EditText).getText().toString()

    val client = AsyncHttpClient()
    var token: String? = ""

    protected override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        token = getSharedPreferences("config", 0)?.getString("token", "")
        init()

        (findViewById(R.id.btn_login) as Button).setOnClickListener {
            val progressDialog = ProgressDialog.show(this@MainActivity, "温馨提醒", "登录中...", true, false);
            val email = getEditTextString(R.id.ed_email)
            val password = getEditTextString(R.id.ed_password)
            val params = RequestParams()
            params.put("email", email)
            params.put("password", password)
            client.post(this, "${baseUrl}/api/v1/users.json",
                   params,
                   JSONResponseHandler<JSONObject, JSONObject>(
                           {
                               val settings = getSharedPreferences("config", 0)
                               settings?.edit()?.putString("token", it!!.getString("token"))?.commit()
                               val intent = Intent()
                               intent.setClass(this@MainActivity, javaClass<HomeActivity>())
                               progressDialog.dismiss()
                               startActivity(intent)
                               finish()
                           },
                           { e, res ->
                               progressDialog.dismiss()
                               when (e) {
                                   is HttpHostConnectException -> {
                                       Toast.makeText(this@MainActivity, "无法连接", Toast.LENGTH_SHORT).show()
                                   }
                                   is ConnectTimeoutException -> {
                                       Toast.makeText(this@MainActivity, "连接超时", Toast.LENGTH_SHORT).show()
                                   }
                                   else -> {
                                       Toast.makeText(this@MainActivity, res?.getString("error"), Toast.LENGTH_SHORT).show()
                                   }
                               }
                           }
                   )
            )
        }
    }

    fun init() {
        client.setTimeout(11000)
        val progressDialog = ProgressDialog.show(this@MainActivity, "温馨提醒", "获取数据", true, false);
        client.get(this, "${baseUrl}/api/v1/users.json?token=${token}",
                JSONResponseHandler<JSONObject, JSONObject>(
                        {
                            progressDialog.dismiss()
                            val intent = Intent()
                            intent.setClass(this@MainActivity, javaClass<HomeActivity>())
                            startActivity(intent)
                            finish()
                        },
                        { e, res ->
                            progressDialog.dismiss()
                            when (e) {
                                is HttpHostConnectException -> {
                                    Toast.makeText(this@MainActivity, "无法连接", Toast.LENGTH_SHORT).show()
                                }
                                is ConnectTimeoutException -> {
                                    Toast.makeText(this@MainActivity, "连接超时", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this@MainActivity, res?.getString("error"), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                )
        )
    }
}
