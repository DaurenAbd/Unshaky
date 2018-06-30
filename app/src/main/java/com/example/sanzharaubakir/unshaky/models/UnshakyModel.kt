package com.example.sanzharaubakir.unshaky.models

abstract class UnshakyModel {
    var listener: ModelListener? = null

    abstract fun enable()

    abstract fun disable()

    abstract fun reset()

    abstract fun getTag(): String
}

interface ModelListener {
    fun onPositionChanged(position: FloatArray)
}