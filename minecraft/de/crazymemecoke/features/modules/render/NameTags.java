package de.crazymemecoke.features.modules.render;

import de.crazymemecoke.Client;
import de.crazymemecoke.manager.clickguimanager.settings.Setting;
import de.crazymemecoke.manager.eventmanager.Event;
import de.crazymemecoke.manager.eventmanager.impl.EventRender;
import de.crazymemecoke.manager.fontmanager.UnicodeFontRenderer;
import de.crazymemecoke.manager.modulemanager.Category;
import de.crazymemecoke.manager.modulemanager.Module;
import de.crazymemecoke.utils.render.Colors;
import de.crazymemecoke.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", Keyboard.KEY_NONE, Category.RENDER, -1);

        Client.main().setMgr().newSetting(new Setting("Show Armor", this, true));
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender) {
            for (EntityPlayer entity : this.mc.theWorld.playerEntities) {
                if ((entity != this.mc.thePlayer) && (!entity.isInvisible())) {
                    String name = RenderUtils.removeColorCode(entity.getDisplayName().getFormattedText());

                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glDisable(2929);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.enableBlend();
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553);

                    float partialTicks = this.mc.timer.renderPartialTicks;

                    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks -
                            RenderManager.renderPosX;
                    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks -
                            RenderManager.renderPosY;
                    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks -
                            RenderManager.renderPosZ;
                    y += this.mc.thePlayer.getDistanceToEntity(entity) * 0.02F;
                    if ((Double.isNaN(entity.getHealth())) || (Double.isInfinite(entity.getHealth()))) {
                        entity.setHealth(20.0F);
                    }
                    BigDecimal bigDecimal = new BigDecimal(entity.getHealth());
                    bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);

                    String USERNAME = name;
                    double HEALTH = bigDecimal.doubleValue();
                    int COLOR = -1;
                    String alias = "";
                    USERNAME = USERNAME + " " + String.valueOf(HEALTH);

                    String FRIEND_NAME = USERNAME;

                    float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
                    float SCALE = Math.min(Math.max(1.2F * (DISTANCE * 0.15F), 1.25F), 6.0F) * 0.02F;

                    GlStateManager.translate((float) x,
                            (float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float) z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

                    GL11.glScalef(-SCALE, -SCALE, SCALE);
                    Tessellator tesselator = Tessellator.getInstance();
                    WorldRenderer worldRenderer = tesselator.getWorldRenderer();

                    UnicodeFontRenderer fontRenderer = Client.main().fontMgr().font("Comfortaa", 20, Font.PLAIN);

                    String CLIENT_USER = "Slowly Gang Member";

                    int STRING_WIDTH = fontRenderer.getStringWidth(USERNAME) / 2;
                    int STRING_WIDTH_CLIENTUSER = fontRenderer.getStringWidth(CLIENT_USER) / 2;
                    int i = 0;
                    RenderUtils.drawBorderedRect(-STRING_WIDTH - 1.0F, -2.0F, STRING_WIDTH + 2.0F, 9.0F, 1.0F,
                            new Color(0, 0, 0).getRGB(), RenderUtils.reAlpha(new Color(0, 0, 0).getRGB(), 0.5F));

                    GL11.glEnable(3553);

                    String s = RenderUtils.removeColorCode(entity.getDisplayName().getFormattedText());

                    fontRenderer.drawString(s, -fontRenderer.getStringWidth(USERNAME) / 2, -fontRenderer.getStringHeight(name) / 32, Colors.main().grey.getRGB());

                    fontRenderer.drawString(String.valueOf(HEALTH), fontRenderer.getStringWidth(USERNAME) / 2 - fontRenderer.getStringWidth(String.valueOf(HEALTH)), -fontRenderer.getStringHeight(name) / 32 + 0.5F, Colors.main().green.getRGB());
                    if (Client.main().setMgr().settingByName("Show Armor", this).getBool()) {
                        if ((entity.getCurrentArmor(0) != null) && ((entity.getCurrentArmor(0).getItem() instanceof ItemArmor))) {
                            renderItem(entity.getCurrentArmor(0), 26, i, 0);
                        }
                        if ((entity.getCurrentArmor(1) != null) && ((entity.getCurrentArmor(1).getItem() instanceof ItemArmor))) {
                            renderItem(entity.getCurrentArmor(1), 13, i, 0);
                        }
                        if ((entity.getCurrentArmor(2) != null) && ((entity.getCurrentArmor(2).getItem() instanceof ItemArmor))) {
                            renderItem(entity.getCurrentArmor(2), 0, i, 0);
                        }
                        if ((entity.getCurrentArmor(3) != null) && ((entity.getCurrentArmor(3).getItem() instanceof ItemArmor))) {
                            renderItem(entity.getCurrentArmor(3), -13, i, 0);
                        }
                        if (entity.getHeldItem() != null) {
                            renderItem(entity.getHeldItem(), -26, i, 0);
                        }
                    }
                    GL11.glEnable(2929);
                    GlStateManager.disableBlend();
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glNormal3f(1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void renderItem(ItemStack item, int xPos, int yPos, int zPos) {
        GL11.glPushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(item);
        mc.getRenderItem().textureManager.bindTexture(TextureMap.locationBlocksTexture);

        GlStateManager.scale(16.0F, 16.0F, 0.0F);

        GL11.glTranslated((xPos - 7.85F) / 16.0F, (-5 + yPos) / 16.0F, zPos / 16.0F);

        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();

        ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
        if (ibakedmodel.isBuiltInRenderer()) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(item);
        } else {
            mc.getRenderItem().renderModel(ibakedmodel, -1, item);
        }
        GlStateManager.enableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }
}