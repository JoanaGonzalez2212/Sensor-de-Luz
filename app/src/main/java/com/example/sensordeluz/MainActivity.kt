package com.example.sensordeluz

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SensorEventListener{
    private lateinit var tvTitulo: TextView
    private lateinit var tvValores: TextView
    private lateinit var tvEstado: TextView
    private lateinit var layoutPrincipal: LinearLayout

    private lateinit var sensorManager: SensorManager
    private var sensorLuz: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvTitulo = findViewById(R.id.tvTitulo)
        tvEstado = findViewById(R.id.tvEstado)
        tvValores = findViewById(R.id.tvValores)
        layoutPrincipal = findViewById(R.id.layout)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (sensorLuz == null) {
            tvValores.text = "No hay sensor de Luz en este dispositivo"
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val luz = event.values[0]
            tvValores.text = "Luz: $luz lx"

            // Evaluación de niveles de luz para cambiar de modo
            if (luz < 50) {
                // Modo Oscuro (Fondo negro, texto blanco)
                tvEstado.text = "Modo Oscuro"
                aplicarTema(Color.BLACK, Color.WHITE)
            } else if (luz > 500) {
                // Modo Claro (Fondo blanco, texto negro)
                tvEstado.text = "Modo Claro"
                aplicarTema(Color.WHITE, Color.BLACK)
            }
        }
    }

    private fun aplicarTema(colorFondo: Int, colorTexto: Int) {
        layoutPrincipal.setBackgroundColor(colorFondo)
        tvTitulo.setTextColor(colorTexto)
        tvValores.setTextColor(colorTexto)
        tvEstado.setTextColor(colorTexto)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        // Registro del sensor para ahorrar batería al pausar/reanudar
        sensorLuz?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el listener al pausar la app
        sensorManager.unregisterListener(this)
    }
}


