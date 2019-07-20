package io.github.proudust.acrobaticscrafter

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.max

@Mod(
        modid = AcrobaticsCrafter.MOD_ID,
        name = AcrobaticsCrafter.MOD_NAME,
        version = AcrobaticsCrafter.VERSION,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object AcrobaticsCrafter {
    const val MOD_ID = "acrobatics-crafter"
    const val MOD_NAME = "Acrobatics Crafter"
    const val VERSION = "2019.1-1.2.23"
}

@Mod.EventBusSubscriber
object PlayerMidairJump {
    private val gameSetting = Minecraft.getMinecraft().gameSettings

    private var isDoubleJumped = false

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun midairJump(event: TickEvent.PlayerTickEvent) {
        val player = event.player
        if (gameSetting.keyBindJump.isPressed && gameSetting.keyBindJump.isKeyDown
                && !player.collidedVertically && !isDoubleJumped
                && !player.isInWater && !player.isInLava) {
            player.jump()
            isDoubleJumped = true
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun resetFlag(event: LivingFallEvent) {
        if (event.entityLiving is EntityPlayerSP) isDoubleJumped = false
    }
}

@Mod.EventBusSubscriber
object PlayerStun {
    private var playerStunTick = -1

    private fun stun(tick: Int) {
        playerStunTick = max(playerStunTick, tick)
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun stunCountAndKeyboardCancelIfStun(event: TickEvent.PlayerTickEvent) {
        if (playerStunTick < 0) return
        playerStunTick--
        when {
            playerStunTick > 0 -> KeyBinding.unPressAllKeys()
            playerStunTick == 0 -> KeyBinding.updateKeyBindState()
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun mouseCancelIfStun(event: MouseEvent) {
        event.isCanceled = playerStunTick >= 0
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @JvmStatic
    fun fallStun(event: LivingFallEvent) {
        if (event.entityLiving is EntityPlayerSP && event.distance * event.damageMultiplier >= 3.0F) stun(20)
        if (event.entityLiving is EntityPlayer) event.damageMultiplier = 0.0F
    }
}
