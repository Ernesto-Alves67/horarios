package com.scherzolambda.horarios.data_transformation

import android.content.Context
import java.util.*

object EnvConfig {
    private val props: Properties by lazy { Properties() }
    private var loaded = false

    fun load(context: Context) {
        if (!loaded) {
            context.assets.open("env.properties").use { inputStream ->
                props.load(inputStream)
                loaded = true
            }
        }
    }

    fun get(key: String, default: String = ""): String {
        return props.getProperty(key, default)
    }
}
