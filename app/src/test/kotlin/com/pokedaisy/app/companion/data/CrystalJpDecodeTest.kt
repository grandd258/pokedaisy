package com.pokedaisy.app.companion.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Japanese Crystal's addresses are the published GameShark / symbol ones, not "whatever
 * [GEN2_CRYSTAL_JP] currently says". The `crystal_jp` fixture is a synthetic WRAM image at those
 * addresses (see its README); it is not a ROM or a captured save. Reading it with [GEN2_CRYSTAL]
 * must miss the party, the bag and the money.
 */
class CrystalJpDecodeTest {
    @Test
    fun `addresses match the GameShark list, and the battle block is not English's`() {
        assertEquals(wramx(1, 0xDC9D), GEN2_CRYSTAL_JP.partyCount)
        assertEquals(wramx(1, 0xDCA5), GEN2_CRYSTAL_JP.partyMons)
        assertEquals(wramx(1, 0xD841), GEN2_CRYSTAL_JP.money)
        assertEquals(wramx(1, 0xD885), GEN2_CRYSTAL_JP.numItems)
        assertEquals(wramx(1, 0xD8AF), GEN2_CRYSTAL_JP.numKeyItems)
        assertEquals(wramx(1, 0xD8CA), GEN2_CRYSTAL_JP.numBalls)
        assertEquals(wramx(1, 0xD84C), GEN2_CRYSTAL_JP.tmsHms)
        assertEquals(wramx(1, 0xDC7B), GEN2_CRYSTAL_JP.mapGroup)
        assertEquals(wramx(1, 0xDC73), GEN2_CRYSTAL_JP.backupMapGroup)
        assertEquals(wramx(1, 0xD483), GEN2_CRYSTAL_JP.playerGender)
        assertEquals(wramx(1, 0xD4B7), GEN2_CRYSTAL_JP.gameTime)
        assertEquals(wramx(1, 0xDE2D), GEN2_CRYSTAL_JP.dexCaught)
        assertEquals(wramx(1, 0xDE4D), GEN2_CRYSTAL_JP.dexSeen)
        // In-battle foe level is 0xD244 (English 0xD213 + 0x31), so the struct starts at 0xD237.
        assertEquals(wramx(1, 0xD237), GEN2_CRYSTAL_JP.enemyMon)
        assertEquals(wramx(1, 0xD25E), GEN2_CRYSTAL_JP.battleMode)
        assertEquals(wramx(1, 0xD266), GEN2_CRYSTAL_JP.moveMenuType)
        assertEquals(wramx(1, 0xD105), GEN2_CRYSTAL_JP.curBattleMon)
        assertEquals(wramx(1, 0xD172), GEN2_CRYSTAL_JP.partyMenuAction)
        assertEquals(wramx(1, 0xD2AC), GEN2_CRYSTAL_JP.otPartyCount)
        assertEquals(wramx(1, 0xD2B4), GEN2_CRYSTAL_JP.otPartyMons)
        assertEquals(0xC622L, GEN2_CRYSTAL_JP.battleMon)
        assertEquals(0xCFA1L, GEN2_CRYSTAL_JP.menu2D)
        assertEquals(0xC4A0L, GEN2_CRYSTAL_JP.tileMap)
        assertNotEquals(GEN2_CRYSTAL.enemyMon, GEN2_CRYSTAL_JP.enemyMon)
        assertNotEquals(GEN2_CRYSTAL.partyCount, GEN2_CRYSTAL_JP.partyCount)
        assertEquals(6, GEN2_CRYSTAL_JP.nameLength)
        assertTrue(TelemetrySampler.CRYSTAL_JP_SHA1 in TelemetrySampler.SUPPORTED_GB_SHA1S)
        assertTrue(TelemetrySampler.CRYSTAL_JP_SHA1 !in TelemetrySampler.CRYSTAL_SHA1S)
        assertEquals("ポケットモンスター クリスタル", com.pokedaisy.app.GameTitles.BY_SHA1[TelemetrySampler.CRYSTAL_JP_SHA1])
    }

    @Test
    fun `synthetic WRAM at the GameShark addresses decodes, and English's do not`() {
        activeGame = GameKind.CRYSTAL
        val ram = FixtureMemoryReader.load("crystal_jp")
        val t = readGen2Telemetry(ram, GEN2_CRYSTAL_JP)
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
        assertTrue(t.inBattle)
        assertEquals(25, t.battleMons[BATTLE_POS_PLAYER_LEFT].species)
        assertEquals(12, t.battleMons[BATTLE_POS_PLAYER_LEFT].level)
        assertEquals(16, t.battleMons[BATTLE_POS_OPPONENT_LEFT].species)
        assertEquals(3, t.battleMons[BATTLE_POS_OPPONENT_LEFT].level)
        assertEquals(10, t.battleMons[BATTLE_POS_OPPONENT_LEFT].hp)

        val english = readGen2Telemetry(ram, GEN2_CRYSTAL)
        assertEquals(0, english.partyCount)
        assertEquals(0, english.itemCount)
        assertEquals(0L, english.money)
        assertEquals(0, english.battleMons[BATTLE_POS_OPPONENT_LEFT].species)
    }
}
