/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tragicbytes.midi.services

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//TODO: Add imports

class MyFirebaseMessagingService: FirebaseMessagingService() {
  private var broadcaster: LocalBroadcastManager? = null


  // TODO: add onCreate
  override fun onCreate() {
    broadcaster = LocalBroadcastManager.getInstance(this)
  }

  // TODO: override onNewToken

  // TODO: add an onMessageReceived function
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    Log.d("TAG", remoteMessage.toString())
    handleMessage(remoteMessage)
  }

  // TODO: add a handle message method
  private fun handleMessage(remoteMessage: RemoteMessage) {
    //1
    val handler = Handler(Looper.getMainLooper())

    //2
    handler.post(Runnable {
      remoteMessage.notification?.let {
        val intent = Intent("MyData")
        intent.putExtra("message", remoteMessage.data["text"]);
        broadcaster?.sendBroadcast(intent);
      }
      Toast.makeText(baseContext, "A notification arrived!",
              Toast.LENGTH_LONG).show()
    }
    )
  }

  // TODO: Create a message receiver constant

  companion object {
    private const val TAG = "MyFirebaseMessagingS"
  }
}