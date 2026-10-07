package com.bustedelbow.kivo.ui.format

/** The Philippine peso sign, shared with the UI that lets the user type an amount. */
const val PESO_SIGN = "₱"

/**
 * The typographic minus that opens a negative amount, so its width matches the `+` that opens an
 * Income. The ASCII hyphen is never drawn.
 */
private const val MINUS_SIGN = "−"

/**
 * Formats [minorUnits] as Philippine pesos for display (ADR-0003 keeps formatting at the UI edge),
 * e.g. `100_000` -> `"₱1,000.00"`. A negative amount is opened with a typographic `−`; a positive
 * one is left unsigned. Amounts are integers, so no `Double` is involved.
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
    val sign = if (isNegative) MINUS_SIGN else ""
    return "$sign$PESO_SIGN$grouped.${cents.toString().padStart(2, '0')}"
}

/**
 * Formats [minorUnits] with an explicit sign, e.g. `100_000` -> `"+₱1,000.00"` and `-550` ->
 * `"−₱5.50"`; zero stays unsigned. The UI uses this for Income and Expense, whose direction is the
 * meaning, so the sign is never left to colour alone.
 */
fun formatSignedPhp(minorUnits: Long): String =
    when {
        minorUnits > 0L -> "+${formatPhp(minorUnits)}"
        minorUnits < 0L -> formatPhp(minorUnits)
        else -> formatPhp(0L)
    }

/**
 * Parses what the user typed at the UI edge into minor units (ADR-0003), tolerating a peso sign, a
 * leading `+` or `−` (typographic or ASCII), thousands separators and spaces. Returns null when the
 * input is not a valid amount or carries sub-centavo precision.
 */
fun parsePhpToMinorUnits(raw: String): Long? {
    val cleaned =
        raw
            .trim()
            .replace(PESO_SIGN, "")
            .replace(",", "")
            .replace(" ", "")
    if (cleaned.isEmpty()) return null

    val isNegative = cleaned.startsWith("-") || cleaned.startsWith(MINUS_SIGN)
    val unsigned = cleaned.removePrefix("-").removePrefix("+").removePrefix(MINUS_SIGN)
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
