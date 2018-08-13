package com.example.sanzharaubakir.unshaky.sensor

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class AbstractSensor(private var manager: SensorManager, type: Int) {
    private var sensor: Sensor = manager.getDefaultSensor(type)

    fun registerListener(listener: SensorEventListener, delay: Int = SensorManager.SENSOR_DELAY_NORMAL) {
        manager.registerListener(listener, sensor, delay)
    }

    fun unregisterListener(listener: SensorEventListener) {
        manager.unregisterListener(listener, sensor)
    }

}