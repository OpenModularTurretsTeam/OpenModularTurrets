package omtteam.openmodularturrets.item;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustedPlayer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryCardNbtTest {
    private static final UUID ALICE = UUID.nameUUIDFromBytes("Alice".getBytes());

    @Test
    void readsCardsWrittenBy3x() {
        NBTTagCompound data = new NBTTagCompound();
        data.setBoolean("attacksPlayers", true);
        data.setBoolean("attacksMobs", false);
        data.setBoolean("attacksNeutrals", true);
        data.setInteger("currentMaxRange", 7);
        data.setInteger("upperBoundMaxRange", 20);
        data.setBoolean("multiTargeting", true);
        data.setInteger("mode", MachineMode.ALWAYS_ON.ordinal());
        NBTTagCompound player = new NBTTagCompound();
        player.setString("name", "Alice");
        player.setString("UUID", ALICE.toString());
        player.setInteger("accessLevel", AccessLevel.ADMIN.ordinal());
        NBTTagList list = new NBTTagList();
        list.appendTag(player);
        NBTTagCompound trust = new NBTTagCompound();
        trust.setTag("trustedPlayers", list);
        data.setTag("trustedPlayers", trust);

        MemoryCardData card = MemoryCardNbt.read(data);
        assertNotNull(card);
        assertEquals(MemoryCardData.Source.TURRET_BASE, card.source());
        MemoryCardData.BaseSettings base = card.base();
        assertNotNull(base);
        assertEquals(MachineMode.ALWAYS_ON, base.mode());
        assertTrue(base.multiTargeting());
        assertTrue(base.targetPlayers());
        assertTrue(base.targetPassive());
        assertEquals(7, base.range());
        assertEquals(1, card.trust().trustedPlayers().size());
        assertEquals(ALICE, card.trust().trustedPlayers().get(0).getUuid());
        assertEquals(AccessLevel.ADMIN, card.trust().trustedPlayers().get(0).getAccessLevel());
        assertNull(card.camo());
    }

    @Test
    void roundTripsATrapCard() {
        MemoryCardData card = new MemoryCardData(MemoryCardData.Source.CAMO_TRAP, null,
                new MemoryCardData.TrustSettings(Collections.singletonList(
                        new TrustedPlayer(new PlayerIdentity(ALICE, "Alice"), AccessLevel.NONE))),
                new MemoryCardData.CamoSettings("minecraft:stone@1", new CamoLight(4, 9)));
        MemoryCardData read = MemoryCardNbt.read(MemoryCardNbt.write(card));
        assertNotNull(read);
        assertEquals(MemoryCardData.Source.CAMO_TRAP, read.source());
        assertNull(read.base());
        assertEquals("Alice", read.trust().trustedPlayers().get(0).getName());
        assertNotNull(read.camo());
        assertEquals("minecraft:stone@1", read.camo().disguise());
        assertEquals(new CamoLight(4, 9), read.camo().light());
    }

    @Test
    void roundTripsABaseCardWithoutCamouflage() {
        MemoryCardData card = new MemoryCardData(MemoryCardData.Source.TURRET_BASE,
                new MemoryCardData.BaseSettings(MachineMode.NONINVERTED, false, true, true, false, 12),
                new MemoryCardData.TrustSettings(Collections.<TrustedPlayer>emptyList()), null);
        MemoryCardData read = MemoryCardNbt.read(MemoryCardNbt.write(card));
        assertNotNull(read);
        assertNotNull(read.base());
        assertEquals(MachineMode.NONINVERTED, read.base().mode());
        assertEquals(12, read.base().range());
        assertNull(read.camo());
    }

    private static void assertBaseRoundTrip(MemoryCardData.BaseSettings base) {
        MemoryCardData read = MemoryCardNbt.read(MemoryCardNbt.write(new MemoryCardData(
                MemoryCardData.Source.TURRET_BASE, base,
                new MemoryCardData.TrustSettings(Collections.<TrustedPlayer>emptyList()), null)));
        assertNotNull(read);
        MemoryCardData.BaseSettings readBase = read.base();
        assertNotNull(readBase);
        assertEquals(base.mode(), readBase.mode());
        assertEquals(base.multiTargeting(), readBase.multiTargeting(), "multi targeting");
        assertEquals(base.targetPlayers(), readBase.targetPlayers(), "players");
        assertEquals(base.targetMobs(), readBase.targetMobs(), "mobs");
        assertEquals(base.targetPassive(), readBase.targetPassive(), "passive mobs");
        assertEquals(base.range(), readBase.range());
    }

    @Test
    void everyBaseSettingSurvivesTheRoundTrip() {
        assertBaseRoundTrip(new MemoryCardData.BaseSettings(MachineMode.NONINVERTED, false, true, true, false, 12));
        assertBaseRoundTrip(new MemoryCardData.BaseSettings(MachineMode.INVERTED, true, false, false, true, 3));
    }
}
