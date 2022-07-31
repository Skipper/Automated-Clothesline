package com.example.mqtt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var mqttClient : MQTTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val suscribir: Button = findViewById(R.id.button)
        val publicar: Button = findViewById(R.id.btn_activity_main_add_new_device)
        val conditions: TextView = findViewById(R.id.tv_main_activity_conditions_value)

        var respuesta : String = ""

        // Parametros de conexión a MQTT
        val serverURI   = "ssl://srv-iot.diatel.upm.es:8883"//getString(MQTT_SERVER_URI_KEY)
        val clientId    = "app"//getString(MQTT_CLIENT_ID_KEY)
        val username    = "NgPgndrYR4pDQc7TUjOc"//getString(MQTT_USERNAME_KEY)
        val pwd         = ""//getString(MQTT_PWD_KEY)

        // Chequeamos los parametros de conexion
        if (    serverURI   != null    &&
            clientId    != null    &&
            username    != null    &&
            pwd         != null        ) {
            // Abrimos la conexión con el broker MQTT
            mqttClient = MQTTClient(this@MainActivity, serverURI, "kotlin_client2")

            // Nos conectamos con el Broker MQTT
            mqttClient.connect( username,
                pwd,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, "Connection success")

                        Toast.makeText(this@MainActivity, "MQTT Connection success", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Connection failure: ${exception.toString()}")

                        Toast.makeText(this@MainActivity, "MQTT Connection fails: ${exception.toString()}", Toast.LENGTH_SHORT).show()

                    }
                },
                object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val msg = "Receive message: ${message.toString()} from topic: $topic"
                        Log.d(this.javaClass.name, msg)
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d(this.javaClass.name, "Delivery complete")
                    }
                })
        }

        //Funcion de subscripción (no utilizada)
        /*
        suscribir.setOnClickListener{
            if (mqttClient.isConnected()) {

                mqttClient.subscribe("v1/devices/me/telemetry",
                    1,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg = "Subscribed to: v1/devices/me/attributes"
                            Log.d(this.javaClass.name, msg)

                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(this.javaClass.name, "Failed to subscribe: v1/devices/me/attributes")
                        }
                    })
            } else {
                Log.d(this.javaClass.name, "Impossible to subscribe, no server connected")
            }
        }*/

        //Funcion de publish
        publicar.setOnClickListener {

            val message = "{\"temperature\":\"100\",\"humidity\":\"100\",\"clear\":\"100\",\"lluvia\":\"1\"}"
            if (mqttClient.isConnected()) {
                mqttClient.publish("v1/devices/me/telemetry",
                    message,
                    1,
                    false,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            val msg = "Action sent to Tender"
                            //Log.d(this.javaClass.name, msg)

                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()

                            val deferredResult = GlobalScope.async() {

                                respuesta = getResponseFromHttpUrl().toString()

                            }

                            runBlocking() {

                                val result = deferredResult.await()

                            }

                            println("El Resultado es $respuesta")


                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.d(this.javaClass.name, "Failed to publish message to topic")
                        }
                    })
            } else {
                Log.d(this.javaClass.name, "Impossible to publish, no server connected")
            }

            if (conditions.text != "Retracted")
                conditions.text == "Retracted"

        }

        val deferredResult = GlobalScope.async() {

            respuesta = getResponseFromHttpUrl().toString()

        }

        runBlocking() {

            val result = deferredResult.await()

        }

        //Con un timer vamos actualizando los valores en Pantalla (obtenidos via llamada API Rest)
        val timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask(){
            override fun run(){
                actualizarValores()
            }
        }, 6000, 6000)

        println("El Resultado es $respuesta")

    }

    //Funcion que se conecta via API Rest para obtener valores de los sensores
    fun getResponseFromHttpUrl(): String? {

        lateinit var connection: HttpURLConnection

        //solicito token de acceso a la API
        val urlParameters : String = "{\"username\":\"francisco.lupi@alumnos.upm.es\", \"password\":\"Panico12-\"}"
        var url: URL =
            URL("https://srv-iot.diatel.upm.es/api/auth/login")
        connection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("Accept", "application/json")
        connection.setDoOutput( true );

        connection.getOutputStream().use { os ->
            val input: ByteArray = urlParameters.toByteArray(UTF_8)
            os.write(input, 0, input.size)
        }

        try {
            // Manejo de errores (si ocurren)
            val responseCode: Int = connection.getResponseCode()
            val inputStream: InputStream
            inputStream = if (200 <= responseCode && responseCode <= 299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val `in` = BufferedReader(
                InputStreamReader(
                    inputStream
                )
            )

            val response = StringBuilder()
            var currentLine: String?

            while (`in`.readLine().also { currentLine = it } != null) response.append(currentLine)

            `in`.close()

            val jsonObj = JSONObject(response.toString())

            val tokenAPI = jsonObj.getString("token")

            println("El mensaje devuelto es "+response.toString())
            println("El token es $tokenAPI")

            //Una vez obtenido el token, solicito con el los datos a la API
            url =
                URL("https://srv-iot.diatel.upm.es/api/plugins/telemetry/DEVICE/31d52190-5e87-11ec-9a04-591db17ccd5b/values/timeseries")
            connection = url.openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")
            connection.addRequestProperty("Content-Type", "application/json")
            connection.addRequestProperty(
                "X-Authorization",
                "Bearer $tokenAPI"
            )

            // Manejo de errores (si ocurren)
            val responseCode1: Int = connection.getResponseCode()
            val inputStream1: InputStream
            inputStream1 = if (200 <= responseCode1 && responseCode1 <= 299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val `in1` = BufferedReader(
                InputStreamReader(
                    inputStream1
                )
            )

            val response1 = StringBuilder()
            var currentLine1: String?

            while (`in1`.readLine().also { currentLine1 = it } != null) response1.append(currentLine1)

            `in1`.close()

            val jsonObj1 = JSONObject(response1.toString())

            val temperature = JSONObject(jsonObj1.getString("temperature").replace("[", "").replace("]", ""))

            val valorTemperature = temperature.getString("value")

            println("La temperatura es $valorTemperature")

            return response1.toString()



        } finally {
            connection.disconnect()
        }
    }

    //Funcion que actualiza valores en Pantalla
    private fun actualizarValores(){
        runOnUiThread{

            var respuesta : String = ""
            val lluvia: TextView = findViewById(R.id.cv_main_activity_rain_value)
            val clearText: TextView = findViewById(R.id.cv_main_activity_led_value)
            val temp: TextView = findViewById(R.id.cv_main_activity_temperature_value)
            val hum: TextView = findViewById(R.id.cv_main_activity_humidity_value)
            val conditions: TextView = findViewById(R.id.tv_main_activity_conditions_value)

            val deferredResult = GlobalScope.async() {

                respuesta = getResponseFromHttpUrl().toString()

            }

            runBlocking() {

                val result = deferredResult.await()

            }

            val jsonObj1 = JSONObject(respuesta)

            val lluviaJson = JSONObject(jsonObj1.getString("lluvia").replace("[", "").replace("]", ""))
            val clearJson = JSONObject(jsonObj1.getString("clear").replace("[", "").replace("]", ""))
            val temperatureJson = JSONObject(jsonObj1.getString("temperature").replace("[", "").replace("]", ""))
            val humidityJson = JSONObject(jsonObj1.getString("humidity").replace("[", "").replace("]", ""))
            var valorLluvia = lluviaJson.getString("value")
            var valorClear = clearJson.getString("value")
            var valorTemp = temperatureJson.getString("value")
            var valorHum = humidityJson.getString("value")
            var condicion = ""

            if (valorLluvia.toInt() > 0)
                valorLluvia = "Yes"
            else
                valorLluvia = "No"
            if (valorClear.toInt() > 0)
                valorClear = "Ok"
            else
                valorClear = "Not Ok"

            lluvia.text = valorLluvia
            clearText.text = valorClear
            temp.text = valorTemp
            hum.text = valorHum
            //temp.text = valorTemp.substringBefore(".","00")
            //hum.text = valorHum.substringBefore(".","00")

            var valorHumFloat = valorHum.toFloat()
            var valorTempFloat = valorTemp.toFloat()

            if (valorLluvia == "Yes"){
                condicion = "Rain Retracting"
            }
            else {
               if ((valorTempFloat > 16) && (valorHumFloat < 30) && (valorClear=="Ok"))
                   condicion = "Optimal"
               else
                   if (((valorTempFloat >= 0) && (valorTempFloat <= 16)) || ((valorHumFloat >= 30) && (valorHumFloat < 55)) || (valorClear!="Ok"))
                       condicion = "Regular"
                   else
                       if ((valorTempFloat < 0) || (valorHumFloat >= 55))
                            condicion = "Bad"
               }

            /*
            if (conditions.text != "Retracted")
                conditions.text = condicion
            */
            conditions.text = condicion

            println("El Resultado es $respuesta")


        }


    }

}
