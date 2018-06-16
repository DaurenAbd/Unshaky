package com.example.sanzharaubakir.unshaky.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*

class Accelerometer(manager: SensorManager) : AbstractSensor(manager, Sensor.TYPE_LINEAR_ACCELERATION), SensorEventListener {
    private var listeners: MutableList<AccelerometerListener> = LinkedList()

    fun enable() {
        registerListener(this, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun disable() {
        unregisterListener(this)
    }

    fun registerListener(listener: AccelerometerListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: AccelerometerListener) {
        listeners.remove(listener)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values == null) {
            return
        }

        for (listener in listeners) {
            listener.onSensorChanged(event.timestamp, event.values)
        }
    }
}

interface AccelerometerListener {
    fun onSensorChanged(timestamp: Long, acc: FloatArray)
}