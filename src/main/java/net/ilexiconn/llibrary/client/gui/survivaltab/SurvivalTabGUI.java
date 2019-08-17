package net.ilexiconn.llibrary.client.gui.survivaltab;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.event.SurvivalTabClickEvent;
import net.ilexiconn.llibrary.server.network.SurvivalTabMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class SurvivalTabGUI extends GuiButton {
    public static final ResourceLocation TABS_TEXTURE = new ResourceLocation("llibrary", "textures/gui/survival_tab.png");

    private SurvivalTab survivalTab;

    public SurvivalTabGUI(int id, SurvivalTab survivalTab) {
        super(id, 0, 0, 0, 0, "");
        this.survivalTab = survivalTab;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiContainer container = (GuiContainer) mc.currentScreen;
        boolean isSelected = mc.currentScreen.getClass() == this.survivalTab.getContainer();
        mc.textureManager.bindTexture(SurvivalTabGUI.TABS_TEXTURE);

        String label = I18n.format(this.survivalTab.getLabel());
        int textWidth = mc.fontRenderer.getStringWidth(label) + 4;
        this.x = container.getGuiLeft() + (LLibrary.CONFIG.areTabsLeftSide() ? -textWidth : container.getXSize());
        this.y = container.getGuiTop() + this.survivalTab.getColumn() * 17 + 3;
        this.width = textWidth;
        this.height = 17;

        if (LLibrary.CONFIG.areTabsLeftSide()) {
            if (isSelected) {
                this.drawTexturedModalRect(this.x + textWidth, this.y, 4, 0, 3, 17);
                this.drawTexturedModalRect(this.x - 3, this.y, 0, 0, 4, 17);
                for (int i = 0; i < textWidth; i++) {
                    this.drawTexturedModalRect(this.x + i, this.y, 14, 0, 1, 17);
                }
            } else {
                this.drawTexturedModalRect(this.x - 3, this.y, 7, 0, 4, 17);
                for (int i = 0; i < textWidth; i++) {
                    this.drawTexturedModalRect(this.x + i, this.y, 11, 0, 1, 17);
                }
            }
        } else {
            if (isSelected) {
                this.drawTexturedModalRect(this.x - 3, this.y, 12, 0, 2, 17);
                this.drawTexturedModalRect(this.x - 1 + textWidth, this.y, 15, 0, 4, 17);
                for (int i = 0; i < textWidth; i++) {
                    this.drawTexturedModalRect(this.x - 1 + i, this.y, 14, 0, 1, 17);
                }
            } else {
                this.drawTexturedModalRect(this.x + textWidth, this.y, 20, 0, 4, 17);
                for (int i = 0; i < textWidth; i++) {
                    this.drawTexturedModalRect(this.x + i, this.y, 19, 0, 1, 17);
                }
            }
        }

        mc.fontRenderer.drawString(label, this.x + 2, this.y + 5, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return false;
        if (super.mouseClicked(mouseX, mouseY, button)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.currentScreen.getClass() != this.survivalTab.getContainer()) {
                MinecraftForge.EVENT_BUS.post(new SurvivalTabClickEvent(this.survivalTab.getLabel(), mc.player));
                LLibrary.NETWORK_WRAPPER.sendToServer(new SurvivalTabMessage(this.survivalTab.getLabel()));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandler) {

    }
}
