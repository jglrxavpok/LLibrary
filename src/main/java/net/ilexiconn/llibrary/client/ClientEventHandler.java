package net.ilexiconn.llibrary.client;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.SnackbarGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.PageButtonGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTab;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTabGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTabHandler;
import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.ilexiconn.llibrary.server.event.SurvivalTabClickEvent;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.snackbar.SnackbarHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public enum ClientEventHandler {
    INSTANCE;

    private SnackbarGUI snackbarGUI;
    private boolean checkedForUpdates;
    private ModelBase voxelModel = new VoxelModel();

    public void setOpenSnackbar(SnackbarGUI snackbarGUI) {
        this.snackbarGUI = snackbarGUI;
    }

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiContainer && (LLibrary.CONFIG.areTabsAlwaysVisible() || SurvivalTabHandler.INSTANCE.getSurvivalTabList().size() > 1)) {
            GuiContainer container = (GuiContainer) event.getGui();
            boolean flag = false;
            for (SurvivalTab survivalTab : SurvivalTabHandler.INSTANCE.getSurvivalTabList()) {
                if (survivalTab.getContainer() != null && survivalTab.getContainer().isInstance(event.getGui())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                int count = 2;
                for (SurvivalTab tab : SurvivalTabHandler.INSTANCE.getSurvivalTabList()) {
                    if (tab.getPage() == SurvivalTabHandler.INSTANCE.getCurrentPage()) {
                        event.getButtonList().add(new SurvivalTabGUI(count, tab));
                    }
                    count++;
                }
                if (count > 7) {
                    int offsetY = (container.getYSize() - 136) / 2 - 10;
                    if (LLibrary.CONFIG.areTabsLeftSide()) {
                        event.getButtonList().add(new PageButtonGUI(-1, container.getGuiLeft() - 82, container.getGuiTop() + 136 + offsetY, container));
                        event.getButtonList().add(new PageButtonGUI(-2, container.getGuiLeft() - 22, container.getGuiTop() + 136 + offsetY, container));
                    } else {
                        event.getButtonList().add(new PageButtonGUI(-1, container.getGuiLeft() + container.getXSize() + 2, container.getGuiTop() + 136 + offsetY, container));
                        event.getButtonList().add(new PageButtonGUI(-2, container.getGuiLeft() + container.getXSize() + 62, container.getGuiTop() + 136 + offsetY, container));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientUpdate(TickEvent.ClientTickEvent event) {
        if (this.snackbarGUI == null && !ClientProxy.SNACKBAR_LIST.isEmpty()) {
            this.setOpenSnackbar(ClientProxy.SNACKBAR_LIST.get(0));
            ClientProxy.SNACKBAR_LIST.remove(this.snackbarGUI);
        }
        if (this.snackbarGUI != null) {
            this.snackbarGUI.updateSnackbar();
        }
    }

    @SubscribeEvent
    public void onRenderUpdate(TickEvent.RenderTickEvent event) {
        ClientUtils.updateLast();
    }

    @SubscribeEvent
    public void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (ClientProxy.MINECRAFT.currentScreen == null && this.snackbarGUI != null && event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            this.snackbarGUI.drawSnackbar();
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (this.snackbarGUI != null) {
            this.snackbarGUI.drawSnackbar();
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        if (LLibrary.CONFIG.hasPatreonEffects() && ClientProxy.PATRONS != null && (ClientProxy.MINECRAFT.gameSettings.thirdPersonView != 0 || player != ClientProxy.MINECRAFT.player)) {
            UUID id = player.getGameProfile().getId();
            if (id != null && ClientProxy.PATRONS.contains(id.toString())) {
                GlStateManager.pushMatrix();
                GlStateManager.translated(event.getX(), event.getY(), event.getZ());
                GlStateManager.depthMask(false);
                GlStateManager.disableLighting();
                GlStateManager.translatef(0.0F, 1.37F, 0.0F);
                this.renderVoxel(event, 1.1F, 0.23F);
                GlStateManager.depthMask(true);
                GlStateManager.enableLighting();
                GlStateManager.translatef(0.0F, 0.128F, 0.0F);
                this.renderVoxel(event, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderVoxel(RenderPlayerEvent.Post event, float scale, float color) {
        EntityPlayer player = event.getEntityPlayer();
        int ticksExisted = player.ticksExisted;
        float partialTicks = LLibrary.PROXY.getPartialTicks();
        float bob = MathHelper.sin(((float) ticksExisted + partialTicks) / 15.0F) * 0.1F;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.rotatef(-ClientUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, partialTicks), 0, 1.0F, 0);
        GlStateManager.color4f(color, color, color, 1.0F);
        GlStateManager.translatef(0.0F, -0.6F + bob, 0.0F);
        GlStateManager.rotatef((ticksExisted + partialTicks) % 360, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(0.75F, 0.0F, 0.0F);
        GlStateManager.rotatef((ticksExisted + partialTicks) % 360, 0.0F, 1.0F, 0.0F);
        GlStateManager.scalef(scale, scale, scale);
        this.voxelModel.render(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onSurvivalTabClick(SurvivalTabClickEvent event) {
        if (event.getLabel().equals("container.inventory")) {
            ClientProxy.MINECRAFT.displayGuiScreen(new GuiInventory(ClientProxy.MINECRAFT.player));
        }
    }
}
