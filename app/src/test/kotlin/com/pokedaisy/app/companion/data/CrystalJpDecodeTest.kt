package com.pokedaisy.app.companion.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Japanese Crystal's RAM is not English Crystal's. These bytes sit at [GEN2_CRYSTAL_JP]'s addresses
 * (party, a 6-byte kana nickname, one bag item, money, the map) and must not be read by [GEN2_CRYSTAL].
 * No ROM: the layout is the one derived in [GEN2_CRYSTAL_JP]'s comment.
 */
class CrystalJpDecodeTest {
    @Test
    fun `sha is registered apart from the English carts`() {
        assertTrue(TelemetrySampler.CRYSTAL_JP_SHA1 in TelemetrySampler.SUPPORTED_GB_SHA1S)
        assertTrue(TelemetrySampler.CRYSTAL_JP_SHA1 !in TelemetrySampler.CRYSTAL_SHA1S)
        assertEquals("ポケットモンスター クリスタル", com.pokedaisy.app.GameTitles.BY_SHA1[TelemetrySampler.CRYSTAL_JP_SHA1])
        assertNotEquals(GEN2_CRYSTAL.partyCount, GEN2_CRYSTAL_JP.partyCount)
        assertEquals(6, GEN2_CRYSTAL_JP.nameLength)
        assertEquals(11, GEN2_CRYSTAL.nameLength)
    }

    @Test
    fun `party nickname bag money and map use the Japanese layout`() {
        activeGame = GameKind.CRYSTAL
        val cfg = GEN2_CRYSTAL_JP
        val party = ByteArray(PARTY_SIZE * 48)
        party[0] = 25 // PIKACHU
        party[31] = 12
        party[34] = 0
        party[35] = 30
        party[36] = 0
        party[37] = 30
        // ピカチュウ then '@'. ピ is the handakuten code 0x41, not ピ built from ヒ plus a mark.
        val nicks = ByteArray(PARTY_SIZE * cfg.nameLength)
        byteArrayOf(0x41, 0x85.toByte(), 0x90.toByte(), 0xAE.toByte(), 0x82.toByte(), 0x50).copyInto(nicks)
        val ram = SparseRam(
            cfg.partyCount to byteArrayOf(1, 25, 0xFF.toByte()),
            cfg.partyMons to party,
            cfg.partyMons + (PARTY_SIZE * 48 + PARTY_SIZE * cfg.nameLength) to nicks,
            cfg.numItems to byteArrayOf(1, 4, 2, 0xFF.toByte()),
            cfg.numBalls to byteArrayOf(0, 0xFF.toByte()),
            cfg.numKeyItems to byteArrayOf(0, 0xFF.toByte()),
            cfg.money to byteArrayOf(0x00, 0x01, 0x2C),
            cfg.mapGroup to byteArrayOf(1, 10, 4, 6),
            cfg.backupMapGroup to byteArrayOf(1, 10),
            cfg.battleMode to byteArrayOf(0),
        )
        val t = readGen2Telemetry(ram, cfg)
        assertEquals(1, t.partyCount)
        assertEquals("PIKACHU", speciesName(t.party[0].species))
        assertEquals(12, t.party[0].level)
        assertEquals(30, t.party[0].hp)
        assertEquals("ピカチュウ", t.party[0].nickname)
        assertEquals(listOf(4), t.items.map { it.itemId })
        assertEquals(2, t.items[0].quantity)
        assertEquals(300L, t.money)
        assertEquals(1, t.mapGroup)
        assertEquals(10, t.mapNum)
        assertEquals(4, t.y)
        assertEquals(6, t.x)
        assertEquals(false, t.inBattle)

        val english = readGen2Telemetry(ram, GEN2_CRYSTAL)
        assertEquals(0, english.partyCount)
        assertEquals(0, english.itemCount)
        assertEquals(0, english.money)
    }

    /** Writes land on top of a zero-filled bus. Overlapping ranges are copied in insertion order. */
    private class SparseRam(private vararg val writes: Pair<Long, ByteArray>) : MemoryReader {
        override fun readCoreMemory(addr: Long, size: Int): ByteArray {
            val out = ByteArray(size)
            for ((base, bytes) in writes) {
                val start = maxOf(addr, base)
                val end = minOf(addr + size, base + bytes.size)
                if (start >= end) continue
                val dst = (start - addr).toInt()
                val src = (start - base).toInt()
                bytes.copyInto(out, dst, src, src + (end - start).toInt())
            }
            return out
        }
    }
}
