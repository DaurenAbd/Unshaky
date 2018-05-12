package com.example.sanzharaubakir.unshaky.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.sanzharaubakir.unshaky.utils.Constants
import com.example.sanzharaubakir.unshaky.utils.Utils

class Accelerometer(manager: SensorManager) : AbstractSensor(manager, Sensor.TYPE_LINEAR_ACCELERATION), SensorEventListener {
    private var timestamp: Long = 0L

    private var data = FloatArray(3)
    private var acc = FloatArray(3)
    private var vel = FloatArray(3)
    private var pos = FloatArray(3)

    var listener: AccelerometerListener? = null

    fun enable() {
        registerListener(this, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun disable() {
        unregisterListener(this)
    }

    fun reset() {
        for (i in 0..2) {
            pos[i] = 0f
            vel[i] = 0f
            acc[i] = 0f
        }
        timestamp = 0L
        listener?.onPositionChanged(pos)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.values == null) {
            return
        }

        for (i in 0..2) {
            data[i] = Utils.rangeValue(event.values[i], -Constants.MAX_ACC, Constants.MAX_ACC)
        }

        if (timestamp == 0L) {
            for (i in 0..2) {
                acc[i] = data[i]
            }
        } else {
            Utils.lowPassFilter(data, acc, Constants.LOW_PASS_ALPHA_DEFAULT)
            val dt = (event.timestamp - timestamp) * Constants.NS2S

            for (i in 0..2) {
                vel[i] += acc[i] * dt - Constants.VELOCITY_FRICTION_DEFAULT * vel[i]
                vel[i] = Utils.fixNanOrInfinite(vel[i])

                pos[i] += vel[i] * Constants.VELOCITY_AMPL_DEFAULT * dt - Constants.POSITION_FRICTION_DEFAULT * pos[i]
                pos[i] = Utils.rangeValue(pos[i], -Constants.MAX_POS_SHIFT, Constants.MAX_POS_SHIFT)
            }
        }

        timestamp = event.timestamp

        listener?.onPositionChanged(pos)
    }
}

interface AccelerometerListener {
    fun onPositionChanged(position: FloatArray)
}