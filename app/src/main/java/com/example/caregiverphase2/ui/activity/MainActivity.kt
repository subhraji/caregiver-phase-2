package com.example.caregiverphase2.ui.activity

import android.app.ActionBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityMainBinding
import com.example.caregiverphase2.model.pojo.chat.ChatModel
import com.example.caregiverphase2.model.pojo.chat.Data
import com.example.caregiverphase2.service.BackgroundLocationService
import com.example.caregiverphase2.utils.Constants
import com.example.caregiverphase2.utils.PrefManager
import com.example.caregiverphase2.utils.SocketHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lightStatusBar
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var CHANNEL_ID = "101"
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        binding.bottomNavigation.itemIconTintList=null
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        subscribeToTopic()
        getToken()
        createNotificationChannel()

        //socket connect
        /*CoroutineScope(Dispatchers.IO).launch {
            SocketHelper.initSocket()
        }*/

        //initSocket()
    }


    //notification subscribe
    private fun subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("cloud")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            }
    }

    //get token
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //val msg = getString(R.string.msg_token_fmt, token)
            Log.d("Token", token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    private fun createNotificationChannel() {

        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "firebaseNotifChannel"
            val descriptionText = "this is a channel to receive firebase notification."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun initSocket(){
        try {
            mSocket = IO.socket(Constants.NODE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket?.on("receiveMessage", onNewMessage);
        mSocket?.connect()
    }

    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            this@MainActivity.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                val username: String
                val msg: String
                var image: String? = null
                var time: String
                val gson = Gson()

                try {
                    //msg = data.getString("msg")
                    val messageData = data.getJSONObject("chatResponse")
                    val message = Gson().fromJson(messageData.toString(), Data::class.java)
                    msg = message.msg
                    image = message.image
                    time = message.time

                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    return@Runnable
                }

                // add the message to view
                //addMessage(username, message)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //SocketHelper.mSocket!!.disconnect()
    }
}