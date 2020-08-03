package de.crazymemecoke.features.modules.movement;

import de.crazymemecoke.manager.clickguimanager.settings.Setting;
import de.crazymemecoke.manager.clickguimanager.settings.SettingsManager;
import de.crazymemecoke.Client;
import de.crazymemecoke.manager.modulemanager.Category;
import de.crazymemecoke.manager.modulemanager.Module;
import de.crazymemecoke.utils.Values;
import de.crazymemecoke.utils.entity.EntityUtils;
import de.crazymemecoke.utils.entity.PlayerUtil;
import de.crazymemecoke.utils.time.TimeHelper;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Fly extends Module {
    ArrayList<String> flyMode = new ArrayList<>();
    ArrayList<String> glideMode = new ArrayList<>();
    ArrayList<String> mode = new ArrayList<>();
    SettingsManager sM = Client.getInstance().getSetmgr();
    TimeHelper timer = new TimeHelper();
    private int delay = 0;
    public double motion;
    public boolean speed;
    public int time = 0;
    public int dtime = 0;

    public Fly() {
        super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT, -1);

        mode.add("Fly");
        mode.add("Glide");

        flyMode.add("Vanilla");
        flyMode.add("Jetpack");
        flyMode.add("Hypixel");
        flyMode.add("Motion");
        flyMode.add("AAC 1.9.8");
        flyMode.add("AAC 1.9.10 Old");
        flyMode.add("AAC 1.9.10 New");
        flyMode.add("AAC 3.0.5");

        glideMode.add("Old");
        glideMode.add("New");

        sM.rSetting(new Setting("Mode", this, "Fly", mode));
        sM.rSetting(new Setting("Fly Mode", this, "Jetpack", flyMode));
        sM.rSetting(new Setting("Glide Mode", this, "New", glideMode));
    }

    @Override
    public void onDisable() {
        if (sM.getSettingByName("Mode", this).getValString().equalsIgnoreCase("Glide")) {
            if (sM.getSettingByName("Glide Mode", this).getValString().equalsIgnoreCase("New")) {
                time = 0;
                dtime = 0;
            }
        } else if (sM.getSettingByName("Mode", this).getValString().equalsIgnoreCase("Fly")) {
            if (sM.getSettingByName("Fly Mode", this).getValString().equalsIgnoreCase("Vanilla")) {
                mc.thePlayer.capabilities.isFlying = false;
            }
        }
    }

    @Override
    public void onEnable() {
        if (Client.getInstance().getSetmgr().getSettingByName("Mode", this).getValString().equalsIgnoreCase("Glide")) {
            if (Client.getInstance().getSetmgr().getSettingByName("Glide Mode", this).getValString().equalsIgnoreCase("New")) {
                time = 0;
                dtime = 0;
                mc.thePlayer.setSprinting(false);
                double x = mc.thePlayer.posX;
                double y = mc.thePlayer.posY;
                double z = mc.thePlayer.posZ;
                double[] d = {0.2D, 0.24D};
                for (int a = 0; a < 100; a++) {
                    for (int i = 0; i < d.length; i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                                mc.thePlayer.posY + d[i], mc.thePlayer.posZ, false));
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        String flyMode = sM.getSettingByName("Fly Mode", this).getValString();
        String glideMode = sM.getSettingByName("Glide Mode", this).getValString();
        String mode = sM.getSettingByName("Mode", this).getValString();
        if (getState()) {
            if (mode.equalsIgnoreCase("Fly")) {
                if (flyMode.equalsIgnoreCase("Vanilla")) {
                    mc.thePlayer.capabilities.isFlying = true;
                } else if (flyMode.equalsIgnoreCase("Motion")) {
                    mc.thePlayer.onGround = true;
                    mc.thePlayer.motionY = 0.0D;
                    if (mc.gameSettings.keyBindForward.isPressed() || mc.gameSettings.keyBindLeft.isPressed() || mc.gameSettings.keyBindRight.isPressed() || mc.gameSettings.keyBindBack.isPressed()) {
                        PlayerUtil.setSpeed(1.0D);
                    }
                    if (mc.gameSettings.keyBindSneak.pressed) {
                        mc.thePlayer.motionY -= 0.5D;
                    } else if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY += 0.5D;
                    }
                } else if (flyMode.equalsIgnoreCase("Hypixel")) {
                    mc.thePlayer.motionY = 0.0D;
                    mc.thePlayer.onGround = true;
                    mc.thePlayer.motionX *= 1.1D;
                    mc.thePlayer.motionZ *= 1.1D;
                } else if (flyMode.equalsIgnoreCase("AAC 3.0.5")) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.INTERACT));
                    if (delay == 0) {
                        mc.timer.timerSpeed = 1.1F;
                    }

                    if (delay == 2) {
                        mc.thePlayer.motionX *= 1.1D;
                        mc.thePlayer.motionZ *= 1.1D;
                        mc.thePlayer.motionY = 0.1D;
                    } else if (delay > 2) {
                        mc.timer.timerSpeed = 1.0F;
                        delay = 0;
                    }
                    ++delay;
                } else if (flyMode.equalsIgnoreCase("AAC 1.9.10 Old")) {
                    if ((double) mc.thePlayer.fallDistance > 2.5D) {
                        ++mc.thePlayer.motionY;
                        mc.thePlayer.fallDistance = 0.0F;
                    }
                } else if (flyMode.equalsIgnoreCase("Jetpack")) {
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.jump();
                        mc.thePlayer.moveForward = 0.8F;
                    }
                } else if (flyMode.equalsIgnoreCase("AAC 1.9.10 New")) {
                    double var6;
                    mc.thePlayer.jumpMovementFactor = 0.024F;
                    if (timer.hasReached(500L)) {
                        if (!mc.thePlayer.onGround) {
                            if (mc.thePlayer.fallDistance > 4.0F) {
                                mc.thePlayer.motionX *= 0.6D;
                                mc.thePlayer.motionZ *= 0.6D;
                                var6 = mc.thePlayer.posY + 6.0D;
                                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, var6, mc.thePlayer.posZ);
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                                timer.reset();
                            }
                        }
                    } else {
                        mc.thePlayer.motionX *= 1.3D;
                        mc.thePlayer.motionZ *= 1.3D;
                        mc.thePlayer.motionY = -0.3D;
                    }
                } else if (flyMode.equalsIgnoreCase("AAC 1.9.8")) {
                    mc.thePlayer.motionY = 0.2D;
                    if (timer.hasReached(100L)) {
                        mc.thePlayer.motionY = -0.3D;
                        timer.reset();
                    }
                }
            } else if (mode.equalsIgnoreCase("Glide")) {
                if (glideMode.equalsIgnoreCase("New")) {
                    motion = 0f;
                    motion = 0.0;
                    speed = true;
                    if (mc.thePlayer.isSneaking()) {
                        mc.thePlayer.motionY = -0.5;
                    } else {
                        if ((mc.thePlayer.motionY < 0.0D) && (mc.thePlayer.isAirBorne) && (!mc.thePlayer.isInWater())
                                && (!mc.thePlayer.isOnLadder()) && (!mc.thePlayer.isInsideOfMaterial(Material.lava))) {
                            mc.thePlayer.setSprinting(false);
                            mc.thePlayer.motionY = -motion;
                            mc.thePlayer.jumpMovementFactor *= 1.21337F;
                        }
                    }
                } else if (glideMode.equalsIgnoreCase("Old")) {
                    EntityUtils.damagePlayer(1);
                    if ((mc.thePlayer.motionY <= -Values.getValues().glidespeed) && (!mc.thePlayer.isInWater())
                            && (!mc.thePlayer.onGround) && (!mc.thePlayer.isOnLadder())) {
                        mc.thePlayer.motionY = (-Values.getValues().glidespeed);
                    }
                }
            }
        }
    }
}