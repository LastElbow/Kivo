package com.bustedelbow.kivo.ui.format

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val ISO_DATE: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

/** Formats an ISO epoch day as a date for display, e.g. `20_000` -> `"2024-10-04"`. */
fun formatEpochDay(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(ISO_DATE)
