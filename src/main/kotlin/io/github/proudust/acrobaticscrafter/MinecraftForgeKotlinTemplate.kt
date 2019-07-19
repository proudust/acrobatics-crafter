package io.github.proudust.acrobaticscrafter

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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
