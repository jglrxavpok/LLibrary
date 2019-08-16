package net.ilexiconn.llibrary.client.render.entity;

import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class PartRenderer extends Render<PartEntity> {
    protected PartRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(PartEntity entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<PartEntity> {
        @Override
        public Render<? super PartEntity> createRenderFor(RenderManager manager) {
            return new PartRenderer(manager);
        }
    }
}
