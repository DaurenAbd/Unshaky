package com.example.sanzharaubakir.unshaky.models.spring_dumper

import com.example.sanzharaubakir.unshaky.models.UnshakyModel
import com.example.sanzharaubakir.unshaky.sensor.Accelerometer
import com.example.sanzharaubakir.unshaky.sensor.AccelerometerListener
import com.example.sanzharaubakir.unshaky.utils.Constants
import com.example.sanzharaubakir.unshaky.utils.Utils

class SpringDumper(private var accelerometer: Accelerometer) : UnshakyModel(), AccelerometerListener {
    private var data: FloatArray = FloatArray(3)
    private var acceleration: FloatArray = FloatArray(3)
    private var velocity: FloatArray = FloatArray(3)
    private var position: FloatArray = FloatArray(3)
    private var lastTimestamp: Long = 0L

    init {
        accelerometer.registerListener(this)
    }

    companion object {
        const val TAG: String = "Spring-dumper"
    }

    override fun onSensorChanged(timestamp: Long, acc: FloatArray) {
        for (i in 0..2) {
            data[i] = Utils.rangeValue(acc[i], -Constants.MAX_ACC, Constants.MAX_ACC)
        }

        if (lastTimestamp == 0L) {
            for (i in 0..2) {
                acceleration[i] = data[i]
            }
        } else {
            Utils.lowPassFilter(data, acceleration, Constants.LOW_PASS_ALPHA_DEFAULT)
            val dt = (timestamp - lastTimestamp) * Constants.NS2S

            for (i in 0..2) {
                velocity[i] += acceleration[i] * dt - Constants.VELOCITY_FRICTION_DEFAULT * velocity[i]
                velocity[i] = Utils.fixNanOrInfinite(velocity[i])

                position[i] += velocity[i] * Constants.VELOCITY_AMPL_DEFAULT * dt - Constants.POSITION_FRICTION_DEFAULT * position[i]
                position[i] = Utils.rangeValue(position[i], -Constants.MAX_POS_SHIFT, Constants.MAX_POS_SHIFT)
            }

            listener?.onPositionChanged(position)
        }

        lastTimestamp = timestamp
    }

    override fun enable() {
        accelerometer.enable()
    }

    override fun disable() {
        accelerometer.disable()
    }


    override fun reset() {
        for (i in 0..2) {
            position[i] = 0f
            velocity[i] = 0f
            acceleration[i] = 0f
        }
        lastTimestamp = 0L
        listener?.onPositionChanged(position)
    }

    override fun getTag(): String {
        return TAG
    }
}