package com.getnetpos

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.getnet.posdigital.PosDigital
import android.util.Log

class GetnetPosModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  companion object {
    public const val TAG = "GetnetPosModuleReactNative"
    public const val NAME = "GetnetPos"
  }

  override fun getName(): String {
    return NAME
  }

  private val bindCallback: PosDigital.BindCallback
    get() = object : PosDigital.BindCallback {
      override fun onError(e: Exception) {
        if (PosDigital.getInstance().isInitiated)
          PosDigital.unregister(getReactApplicationContext())
        connectPosDigitalService()
      }
      override fun onConnected() {

        Log.d(TAG, "connected uhull zo/")
      }

      override fun onDisconnected() {
        Log.d(TAG, "disconnected :X")
      }
    }

  private fun connectPosDigitalService() {
    PosDigital.register(getReactApplicationContext(), bindCallback)
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {

    promise.resolve(a * b)
  }
}
