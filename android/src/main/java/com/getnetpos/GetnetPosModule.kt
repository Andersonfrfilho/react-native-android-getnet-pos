package com.getnetpos

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableNativeMap
import com.getnet.posdigital.PosDigital
import com.getnet.posdigital.camera.ICameraCallback;
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

  @ReactMethod
  fun startingServices(promise: Promise) {
    connectPosDigitalService()
    promise.resolve(true)
  }
  private fun connectPosDigitalService() {
    PosDigital.register(reactApplicationContext, bindCallback)
  }
  private fun disconnectPosDigitalService() {
    PosDigital.unregister(reactApplicationContext)
  }
  private val bindCallback: PosDigital.BindCallback get() = object : PosDigital.BindCallback {
    override fun onError(e: Exception) {
      if (PosDigital.getInstance().isInitiated) {
          disconnectPosDigitalService()
      }
      connectPosDigitalService()
    }
    override fun onConnected() {
      Log.d(TAG, "onConnected")
    }
    override fun onDisconnected() {
      Log.d(TAG, "onDisconnected")
    }
  }

  @ReactMethod
  fun checkConnections(promise: Promise) {
    val promiseCheckConnection = WritableNativeMap()
    promiseCheckConnection.putBoolean("connection", PosDigital.getInstance().isInitiated)
    promise.resolve(promiseCheckConnection)
  }

  @ReactMethod
  fun beeperMethod(data: ReadableMap, promise: Promise) {
    try {
        val beeperResponse = WritableNativeMap()
        when (data.getString("beeperMode")?.lowercase()) {
          "nfc" -> PosDigital.getInstance().beeper.nfc()
          "error" -> PosDigital.getInstance().beeper.error()
          "digit" -> PosDigital.getInstance().beeper.digit()
          else -> PosDigital.getInstance().beeper.success()
        }
        beeperResponse.putBoolean("beeper", true)
        beeperResponse.putString("type", data.getString("beeperMode")?.lowercase())
        promise.resolve(beeperResponse)
    } catch (e: Exception) {
        promise.reject("error on beeperMethod: ", e.message)
    }
  }

  @ReactMethod
  fun ledMethod(data: ReadableMap, promise: Promise) {
    try {
      val ledResponse = WritableNativeMap()
      val turnOn = data.getBoolean("turn")
      val color = data.getString("color")?.lowercase()

      if (turnOn) {
        when (color) {
          "red" -> PosDigital.getInstance().led.turnOnRed()
          "blue" -> PosDigital.getInstance().led.turnOnBlue()
          "green" -> PosDigital.getInstance().led.turnOnGreen()
          "yellow" -> PosDigital.getInstance().led.turnOnYellow()
          else -> PosDigital.getInstance().led.turnOnAll()
        }
        ledResponse.putBoolean("turn", true)
        ledResponse.putString("color", color)
        promise.resolve(ledResponse)
      } else {
        when (color) {
          "red" -> PosDigital.getInstance().led.turnOffRed()
          "blue" -> PosDigital.getInstance().led.turnOffBlue()
          "green" -> PosDigital.getInstance().led.turnOffGreen()
          "yellow" -> PosDigital.getInstance().led.turnOffYellow()
          else -> PosDigital.getInstance().led.turnOffAll()
        }
        ledResponse.putBoolean("turn", false)
        ledResponse.putString("color", color)
        promise.resolve(ledResponse)
      }
    } catch (e: Exception) {
      promise.reject("error on ledMethod: ", e.message)
    }
  }

  @ReactMethod
  fun cameraMethod(data: ReadableMap, promise: Promise) {
    try {
      val timeout = data.getInt("timeout")
      val camera = PosDigital.getInstance().getCamera()
      val cameraType = data.getString("cameraType")?.lowercase()

      val callback: ICameraCallback = object: ICameraCallback.Stub() {
        val cameraResponse = WritableNativeMap()

        override fun onSuccess(s: String) {
          cameraResponse.putBoolean("error", false)
          cameraResponse.putString("message", s)
          promise.resolve(cameraResponse)
        }
        override fun onTimeout() {
          cameraResponse.putBoolean("error", true)
          cameraResponse.putString("message", "time exceeded")
          promise.resolve(cameraResponse)
        }
        override fun onCancel() {
          cameraResponse.putBoolean("error", true)
          cameraResponse.putString("message", "option canceled")
          promise.resolve(cameraResponse)
        }
        override fun onError(s: String) {
          cameraResponse.putBoolean("error", true)
          cameraResponse.putString("message", s)
          promise.resolve(cameraResponse)
        }
      }
      if (cameraType == "front") {
          camera.readFront(timeout, callback)
      } else {
          camera.readBack(timeout, callback)
      }
    } catch (e: Exception) {
        promise.reject("error", e.message)
    }
  }
}
