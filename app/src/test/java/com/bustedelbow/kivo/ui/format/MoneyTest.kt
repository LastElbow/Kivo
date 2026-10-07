package com.bustedelbow.kivo.ui.format

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Formatting and parsing happen only at the UI edge (ADR-0003); these pin the PHP presentation. */
class MoneyTest {
    @Test
    fun `formats pesos with thousands separators and two decimals`() {
        assertEquals("₱1,000.00", formatPhp(100_000))
        assertEquals("₱1,234,567.89", formatPhp(123_456_789))
        assertEquals("₱0.00", formatPhp(0))
        assertEquals("₱12.05", formatPhp(1_205))
        assertEquals("−₱5.50", formatPhp(-550))
    }

    @Test
    fun `signed format marks income with a plus and an expense or balance with a minus`() {
        assertEquals("+₱1,000.00", formatSignedPhp(100_000))
        assertEquals("+₱1,234,567.89", formatSignedPhp(123_456_789))
        assertEquals("−₱5.50", formatSignedPhp(-550))
        assertEquals("−₱1,000.00", formatSignedPhp(-100_000))
        assertEquals("₱0.00", formatSignedPhp(0))
    }

    @Test
    fun `signed format never uses the ASCII hyphen`() {
        assertEquals("−₱0.01", formatSignedPhp(-1))
    }

    @Test
    fun `parses amounts the user may type`() {
        assertEquals(100_000L, parsePhpToMinorUnits("1000"))
        assertEquals(100_000L, parsePhpToMinorUnits("₱1,000"))
        assertEquals(100_000L, parsePhpToMinorUnits(" 1,000.00 "))
        assertEquals(1_050L, parsePhpToMinorUnits("10.5"))
        assertEquals(0L, parsePhpToMinorUnits("0"))
        assertEquals(-550L, parsePhpToMinorUnits("-5.50"))
        assertEquals(-550L, parsePhpToMinorUnits("−₱5.50"))
    }

    @Test
    fun `rejects input that is not a whole-centavo amount`() {
        assertNull(parsePhpToMinorUnits(""))
        assertNull(parsePhpToMinorUnits("abc"))
        assertNull(parsePhpToMinorUnits("1.234"))
        assertNull(parsePhpToMinorUnits("1.2.3"))
        assertNull(parsePhpToMinorUnits("-"))
    }
}
