package com.axtarget.processnova.core

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Convierte un Double a formato moneda mexicana (MXN)
 */
fun Double.toMXN(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(this)
}

/**
 * Formatea una fecha ISO a formato legible en español
 */
fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

/**
 * Formatea una fecha corta a formato abreviado
 */
fun String.toShortDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale("es", "MX"))
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

/**
 * Convierte timestamp a tiempo relativo (hace Xh, hace Xm, etc.)
 */
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days > 0 -> "hace ${days}d"
        hours > 0 -> "hace ${hours}h"
        minutes > 0 -> "hace ${minutes}m"
        else -> "ahora"
    }
}

/**
 * Trunca un string al máximo de caracteres indicado
 */
fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) this.take(maxLength) + "..." else this
}

