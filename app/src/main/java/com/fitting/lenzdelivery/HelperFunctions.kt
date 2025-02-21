package com.fitting.lenzdelivery

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun String.formDate(): String {
    val instant = Instant.parse(this)
    val zonedDateTime = instant.atZone(ZoneId.of("Asia/Kolkata"))
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    return zonedDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.toIST(): String {
    val utcDateTime = ZonedDateTime.parse(this)
    val istDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    val formatter = DateTimeFormatterBuilder()
        .appendPattern("hh:mm ")
        .appendText(ChronoField.AMPM_OF_DAY, mapOf(0L to "a.m.", 1L to "p.m."))
        .toFormatter(Locale.ENGLISH)
    return istDateTime.format(formatter)
}