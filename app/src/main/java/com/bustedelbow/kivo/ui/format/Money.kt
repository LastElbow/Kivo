package com.bustedelbow.kivo.ui.format

/** The Philippine peso sign, shared with the UI that lets the user type an amount. */
const val PESO_SIGN = "₱"

/**
 * Formats [minorUnits] as Philippine pesos for display (ADR-0003 keeps formatting at the UI edge),
 * e.g. `100_000` -> `"₱1,000.00"`. Amounts are integers, so no `Double` is involved.
 */
fun formatPhp(minorUnits: Long): String {
    val isNegative = minorUnits < 0
    val magnitude = if (isNegative) -minorUnits else minorUnits
    val major = magnitude / 100
    val cents = magnitude % 100
    val grouped =
        major
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    val sign = if (isNegative) "-" else ""
    return "$sign$PESO_SIGN$grouped.${cents.toString().padStart(2, '0')}"
}

/**
 * Parses what the user typed at the UI edge into minor units (ADR-0003), tolerating a peso sign,
 * thousands separators and spaces. Returns null when the input is not a valid amount or carries
 * sub-centavo precision.
 */
fun parsePhpToMinorUnits(raw: String): Long? {
    val cleaned =
        raw
            .trim()
            .replace(PESO_SIGN, "")
            .replace(",", "")
            .replace(" ", "")
    if (cleaned.isEmpty()) return null

    val isNegative = cleaned.startsWith("-")
    val unsigned = cleaned.removePrefix("-").removePrefix("+")
    if (unsigned.isEmpty()) return null

    val parts = unsigned.split(".")
    if (parts.size > 2) return null

    val wholePart = parts[0].ifEmpty { "0" }
    val fractionPart = parts.getOrElse(1) { "" }
    if (wholePart.any { !it.isDigit() } || fractionPart.any { !it.isDigit() }) return null
    if (fractionPart.length > 2) return null

    val whole = wholePart.toLongOrNull() ?: return null
    val cents = fractionPart.padEnd(2, '0').toLong()
    val minorUnits = whole * 100 + cents
    return if (isNegative) -minorUnits else minorUnits
}
