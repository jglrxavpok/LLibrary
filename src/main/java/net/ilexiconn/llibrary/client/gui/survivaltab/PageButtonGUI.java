package net.ilexiconn.llibrary.client.gui.survivaltab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class PageButtonGUI extends GuiButton {
    private GuiScreen screen;

    public PageButtonGUI(int id, int x, int y, GuiScreen screen) {
        super(id, x, y, 20, 20, id == -1 ? "<" : ">");
        this.screen = screen;
        this.updateState();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if (this.id == -1) {
            Minecraft mc = Minecraft.getInstance();
            mc.fontRenderer.drawString((SurvivalTabHandler.INSTANCE.getCurrentPage() + 1) + "/" + SurvivalTabHandler.INSTANCE.getSurvivalTabList().size() / 8 + 1, this.x + 31, this.y + 6, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return false;
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (this.id == -1) {
                int currentPage = SurvivalTabHandler.INSTANCE.getCurrentPage();
                if (currentPage > 0) {
                    SurvivalTabHandler.INSTANCE.setCurrentPage(currentPage - 1);
                    this.initGui();
                    this.updateState();
                }
            } else if (this.id == -2) {
                int currentPage = SurvivalTabHandler.INSTANCE.getCurrentPage();
                if (currentPage < SurvivalTabHandler.INSTANCE.getSurvivalTabList().size() / 8) {
                    SurvivalTabHandler.INSTANCE.setCurrentPage(currentPage + 1);
                    this.initGui();
                    this.updateState();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void updateState() {
        for (IGuiEventListener child : this.screen.getChildren()) {
            if(!(child instanceof GuiButton)) {
                continue;
            }
            GuiButton button = (GuiButton)child;
            if (button.id == -1) {
                button.enabled = SurvivalTabHandler.INSTANCE.getCurrentPage() >= SurvivalTabHandler.INSTANCE.getSurvivalTabList().size() / 8;
            } else if (button.id == -2) {
                button.enabled = SurvivalTabHandler.INSTANCE.getCurrentPage() <= 0;
            }
        }
    }

    public void initGui() {
        this.screen.getChildren().removeIf(guiButton -> guiButton instanceof SurvivalTabGUI);
        this.screen.setWorldAndResolution(Minecraft.getInstance(), screen.width, screen.height);
    }
}

