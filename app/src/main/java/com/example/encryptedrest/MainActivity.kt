package com.example.encryptedrest

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.encryptedrest.api.IApi
import com.example.encryptedrest.api.RetrofitBuilderClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //create retrofit
        val client =
            RetrofitBuilderClient.buildClient(
                "https://vestindobueno.com",
                IApi::class.java,
                application
            )

        withIntercept.setOnClickListener { view ->
            lifecycleScope.launch {
                runCoroutine {
                    return@runCoroutine client.teste(TesteBody("1234"))
                }
            }
        }
        withoutIntercept.setOnClickListener {
            lifecycleScope.launch {
                runCoroutine {
                    return@runCoroutine client.teste2()
                }
            }
        }

    }

    suspend fun runCoroutine(block: suspend () -> Any) {
        runCatching {
            block.invoke()
        }.onFailure {
            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
        }
    }

}
