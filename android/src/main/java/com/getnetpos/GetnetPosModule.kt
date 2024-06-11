package com.getnetpos

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableNativeMap
import com.getnet.posdigital.PosDigital
import com.getnet.posdigital.camera.ICameraCallback
import com.getnet.posdigital.card.CardResponse
import com.getnet.posdigital.card.ICardCallback
import com.getnet.posdigital.card.SearchType
import com.getnet.posdigital.printer.AlignMode;
import com.getnet.posdigital.printer.FontFormat;
import com.getnet.posdigital.printer.IPrinterCallback;
import com.getnet.posdigital.printer.PrinterStatus;

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

  @ReactMethod
  fun cardStartConnectAntenna(data: ReadableMap, promise: Promise) {
    val timeout = data.getInt("timeout").toLong()
    val cardType = data.getString("cardType")?.lowercase()
    var searchType = arrayOf<String>()

    when (cardType) {
      "nfc" -> searchType = arrayOf(SearchType.NFC)
      "chip" -> searchType = arrayOf(SearchType.CHIP)
      "magnetic" -> searchType = arrayOf(SearchType.MAG)
      else -> searchType = arrayOf(SearchType.MAG, SearchType.CHIP, SearchType.NFC)
    }

    try {
      PosDigital.getInstance().card.search(timeout, searchType, object : ICardCallback.Stub() {
        val connectCardResponse = WritableNativeMap()

        override fun onCard(cardResponse: CardResponse) {
          connectCardResponse.putString("pan", cardResponse.pan)
          connectCardResponse.putString("type", cardResponse.type)
          connectCardResponse.putString("track1", cardResponse.track1)
          connectCardResponse.putString("track2", cardResponse.track2)
          connectCardResponse.putString("track3", cardResponse.track3)
          connectCardResponse.putString("dataExpired", cardResponse.expireDate)
          connectCardResponse.putInt("numberCard", cardResponse.describeContents())
          promise.resolve(connectCardResponse)
        }
        override fun onMessage(s: String) {
          connectCardResponse.putBoolean("error", false)
          connectCardResponse.putString("message", s)
          promise.resolve(connectCardResponse)
        }
        override fun onError(s: String) {
          connectCardResponse.putBoolean("error", true)
          connectCardResponse.putString("message", s)
          promise.resolve(connectCardResponse)
        }
      })
    } catch (e: Exception) {
      promise.reject("error", e.message)
    }
  }

  @ReactMethod
  fun cardStopConnectAntenna(promise: Promise) {
    val cardStopConnectResponse = WritableNativeMap()
    try {
      PosDigital.getInstance().getCard().stopAllReaders()
      cardStopConnectResponse.putBoolean("stop", true)
      promise.resolve(cardStopConnectResponse)
    } catch (e: Exception) {
      promise.reject("error", e.message)
    }
  }

  @ReactMethod
  fun printView(data: ReadableArray, promise: Promise) {
    val printVector = mutableListOf<String>()
    val sizePaper = 54

    if (data != null) {
      for (i in 0 until data.size()) {
        val item = data.getMap(i)
        val value = item?.getString("value") ?: ""
        val type = item?.getString("type")?.lowercase()
        val align = item?.getString("align")?.lowercase()
        val fontSize = item?.getString("fontSize")?.lowercase()

        val maxSizeLine = when (fontSize) {
          "small" -> 54
          "medium" -> 42
          else -> 29
        }
        if (type == "text") {
          val newText = when (align) {
            "left" -> {
              val trimmedText = value.take(maxSizeLine)
              val paddedText = trimmedText.padEnd(maxSizeLine - 1, '_')
              "|$paddedText|"
            }
            "right" -> {
              val trimmedText = if (value.length > maxSizeLine) value.takeLast(maxSizeLine) else value
              val leftSpaces = maxSizeLine - trimmedText.length
              "|" + "_".repeat(leftSpaces) + trimmedText + "|"
            }
            else -> {
              val trimmedText = value.take(maxSizeLine)
              val leftSpaces = (maxSizeLine - trimmedText.length) / 2
              val rightSpaces = maxSizeLine - trimmedText.length - leftSpaces
              "|${"_".repeat(leftSpaces)}$trimmedText${"_".repeat(rightSpaces)}|"
            }
          }
          printVector.add(newText)
        } else if (type == "image") {
          val image = "|${if (fontSize == "small") "______Image show in next update module______" else "Image show in next update module"}|"
          printVector.add(image)
        }
      }
    }

    val promisePrintView = Arguments.createArray()

    promisePrintView.pushString("|\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\".substring(0, sizePaper) + "|")
    printVector.forEach { promisePrintView.pushString(it) }
    promisePrintView.pushString("|\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\".substring(0, sizePaper) + "|")

    promise.resolve(promisePrintView)
  }

  @ReactMethod
  fun printMethod(options: ReadableArray, promise: Promise) {
    try {
      PosDigital.getInstance().printer.init()
      for (i in 0 until options.size()) {
        val option = options.getMap(i)
        val weight = option?.getInt("weight") ?: 0
        val value = option?.getString("value") ?: ""
        val type = option?.getString("type")?.lowercase()
        val align = option?.getString("align")?.lowercase()

        when (type) {
          "image" -> {
            PosDigital.getInstance().printer.setGray(weight)
            val position = value.indexOf(',')
            val newValue = if (position == -1) value else value.substring(position + 1, value.length)
            val byteImage = Base64.decode(newValue, Base64.DEFAULT)
            val decodeImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)

            when (align) {
              "left" -> {
                PosDigital.getInstance().printer.addImageBitmap(AlignMode.LEFT, decodeImage)
                PosDigital.getInstance().printer.addText(AlignMode.LEFT, "\n")
              }
              "right" -> {
                PosDigital.getInstance().printer.addImageBitmap(AlignMode.RIGHT, decodeImage)
                PosDigital.getInstance().printer.addText(AlignMode.RIGHT, "\n")
              }
              else -> {
                PosDigital.getInstance().printer.addImageBitmap(AlignMode.CENTER, decodeImage)
                PosDigital.getInstance().printer.addText(AlignMode.CENTER, "\n")
              }
            }
          }
          "barcode" -> {
            PosDigital.getInstance().printer.setGray(weight)
            PosDigital.getInstance().printer.defineFontFormat(FontFormat.SMALL)
            when (align) {
              "left" -> {
                PosDigital.getInstance().printer.addBarCode(AlignMode.LEFT, value)
                PosDigital.getInstance().printer.addText(AlignMode.LEFT, "\n")
              }
              "right" -> {
                PosDigital.getInstance().printer.addBarCode(AlignMode.RIGHT, value)
                PosDigital.getInstance().printer.addText(AlignMode.RIGHT, "\n")
              }
              else -> {
                PosDigital.getInstance().printer.addBarCode(AlignMode.CENTER, value)
                PosDigital.getInstance().printer.addText(AlignMode.CENTER, "\n")
              }
            }
          }
          "qrcode" -> {
            PosDigital.getInstance().printer.setGray(weight)
            PosDigital.getInstance().printer.defineFontFormat(FontFormat.SMALL)
            when (align) {
              "left" -> {
                PosDigital.getInstance().printer.addQrCode(AlignMode.LEFT, 240, value)
                PosDigital.getInstance().printer.addText(AlignMode.LEFT, "\n")
              }
              "right" -> {
                PosDigital.getInstance().printer.addQrCode(AlignMode.RIGHT, 240, value)
                PosDigital.getInstance().printer.addText(AlignMode.RIGHT, "\n")
              }
              else -> {
                PosDigital.getInstance().printer.addQrCode(AlignMode.CENTER, 240, value)
                PosDigital.getInstance().printer.addText(AlignMode.CENTER, "\n")
              }
            }
          }
          else -> {
            PosDigital.getInstance().printer.setGray(weight)
            val fontSize = option?.getString("fontSize")?.lowercase()
            when (fontSize) {
              "small" -> PosDigital.getInstance().printer.defineFontFormat(FontFormat.SMALL)
              "large" -> PosDigital.getInstance().printer.defineFontFormat(FontFormat.LARGE)
              else -> PosDigital.getInstance().printer.defineFontFormat(FontFormat.MEDIUM)
            }
            when (align) {
              "left" -> PosDigital.getInstance().printer.addText(AlignMode.LEFT, value)
              "right" -> PosDigital.getInstance().printer.addText(AlignMode.RIGHT, value)
              else -> PosDigital.getInstance().printer.addText(AlignMode.CENTER, value)
            }
          }
        }
      }

      PosDigital.getInstance().printer.addText(AlignMode.LEFT, "\n \n \n \n")
      PosDigital.getInstance().printer.print(object : IPrinterCallback.Stub() {

        override fun onSuccess() {
          val printResponse = WritableNativeMap().apply {
            putBoolean("printer", true)
            putString("message", "success")
          }
          promise.resolve(printResponse)
        }

        override fun onError(i: Int) {
          val printResponse = WritableNativeMap().apply {
            putBoolean("printer", false)
            val message = when (i) {
              2 -> "Impressora não iniciada"
              3 -> "Impressora superaquecida"
              4 -> "Fila de impressão muito grande"
              5 -> "Parametros incorretos"
              10 -> "Porta da impressora aberta"
              11 -> "Temperatura baixa de mais"
              12 -> "Sem bateria suficiente para impressão"
              13 -> "Motor de passo com problemas"
              15 -> "Sem bobina"
              16 -> "Bobina acabando"
              17 -> "Bobina travada"
              else -> "Erro não identificado"
            }
            putString("message", message)
          }
          promise.resolve(printResponse)
        }
      })
    } catch (error: Exception) {
      promise.reject("error", error.message)
    }
  }
}
