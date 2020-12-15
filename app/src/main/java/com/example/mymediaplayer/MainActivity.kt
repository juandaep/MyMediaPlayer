package com.example.mymediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private var mService: Messenger? = null

    private lateinit var mBoundServiceIntent: Intent
    private var mServiceBound = false

    /*
    Service Connection adalah interface yang digunakan untuk menghubungkan antara boundservice dengan activity
    */
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mServiceBound = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mServiceBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnPlay.setOnClickListener {
            //untuk mengirim perintah play
            if (mServiceBound) {
                try {
                    mService?.send(Message.obtain(null, MediaService.PLAY, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        btnStop.setOnClickListener {
            if (mServiceBound) {
                //Untuk mengirim perintah stop
                try {
                    mService?.send(Message.obtain(null, MediaService.STOP, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        /*
        Start service untuk media player
        */
        mBoundServiceIntent = Intent(this@MainActivity, MediaService::class.java)
        mBoundServiceIntent.action = MediaService.ACTION_CREATE

        startService(mBoundServiceIntent)
        bindService(mBoundServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unbindService(mServiceConnection)
        mBoundServiceIntent.action = MediaService.ACTION_DESTROY

        startService(mBoundServiceIntent)
    }

}