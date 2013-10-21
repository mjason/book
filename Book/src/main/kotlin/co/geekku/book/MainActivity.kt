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
            val email = getEditTextString(R.id.ed_email)
            val password = getEditTextString(R.id.ed_password)
            val params = RequestParams()
            params.put("email", email)
            params.put("password", password)
            client.post(this, "${baseUrl}/api/v1/users.json",
                   params,
                   object: JsonHttpResponseHandler() {
                       override public fun onSuccess(resp: JSONObject?) {
                           val settings = getSharedPreferences("config", 0)
                           settings?.edit()?.putString("token", resp!!.getString("token"))?.commit()
                           val intent = Intent()
                           intent.setClass(this@MainActivity, javaClass<HomeActivity>())
                           startActivity(intent)
                           finish()
                       }
                   })
        }
    }

    fun init() {
        client.get(this, "${baseUrl}/api/v1/users.json?token=${token}",
                object: JsonHttpResponseHandler() {
                    override public fun onSuccess(resp: JSONObject?) {
                        val intent = Intent()
                        intent.setClass(this@MainActivity, javaClass<HomeActivity>())
                        startActivity(intent)
                        finish()
                    }
                })
    }
}
