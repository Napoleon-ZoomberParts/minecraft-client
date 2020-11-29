package de.crazymemecoke.features.modules.impl.player;

import de.crazymemecoke.Client;
import de.crazymemecoke.features.modules.ModuleInfo;
import de.crazymemecoke.manager.settingsmanager.Setting;
import de.crazymemecoke.manager.eventmanager.Event;
import de.crazymemecoke.manager.eventmanager.impl.EventUpdate;

import de.crazymemecoke.features.modules.Category;
import de.crazymemecoke.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "FastUse", category = Category.PLAYER, description = "Lets you use items real fast")
public class FastUse extends Module {

    public Setting delay = new Setting("Delay", this, 15, 0, 20, false);
    public Setting ncp = new Setting("NCP", this, true);

    @Override
    public void onToggle() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void shootBow() {
        int item = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255,
                mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(item));
        for (int i = 0; i < 20; i++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.fromAngle(-1.0D)));
    }

    private boolean isSword(Item item) {
        return item instanceof ItemSword;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            double delay = Client.main().setMgr().settingByName("Delay", this).getCurrentValue();

            try {
                if (isSword(mc.thePlayer.inventory.getCurrentItem().getItem())) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            if (mc.thePlayer.getItemInUseDuration() > delay) {
                for (int i = 0; i < 20; i++) {
                    Minecraft.mc().thePlayer.sendQueue
                            .addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                }
                if ((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) {
                    if (!ncp.isToggled()) {
                        shootBow();
                        return;
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.UP));
                    mc.thePlayer.stopUsingItem();
                } else {
                    mc.thePlayer.stopUsingItem();
                }
            }
        }
    }
}