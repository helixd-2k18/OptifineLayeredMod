package net.fabricmc.optilayeredmod;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.optilayeredmod.accessor.GlStateManagerAccess;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public interface GlStateManagerUtils {

    public static void bindTexture(int texture) throws IllegalAccessException {
        GlStateManager.Texture2DState[] TEXTURES = GlStateManagerAccess.getTextures();
        int activeTexture = GlStateManagerAccess.getActiveTexture();

        RenderSystem.assertThread((Supplier<Boolean>)((Supplier)RenderSystem::isOnRenderThreadOrInit));
        if (texture != TEXTURES[activeTexture].boundTexture) {
            TEXTURES[activeTexture].boundTexture = texture;
            GL11.glBindTexture(GL_TEXTURE_2D, (int)texture);
        }
    }

    public static void bindTexture(int target, int texture) throws IllegalAccessException {
        GlStateManager.Texture2DState[] TEXTURES = GlStateManagerAccess.getTextures();
        int activeTexture = GlStateManagerAccess.getActiveTexture();

        RenderSystem.assertThread((Supplier<Boolean>)((Supplier)RenderSystem::isOnRenderThreadOrInit));
        if (texture != TEXTURES[activeTexture].boundTexture) {
            TEXTURES[activeTexture].boundTexture = texture;
            GL11.glBindTexture(target, (int)texture);
        }
    }

    public static void bindTextureUnit(int unit, int texture) throws IllegalAccessException {
        GlStateManager.Texture2DState[] TEXTURES = GlStateManagerAccess.getTextures();
        int activeTexture = GlStateManagerAccess.getActiveTexture();

        RenderSystem.assertThread((Supplier<Boolean>)((Supplier)RenderSystem::isOnRenderThread));
        if (activeTexture != unit || texture != TEXTURES[unit].boundTexture) {
            TEXTURES[unit].boundTexture = texture;
            GlStateManagerAccess.setActiveTexture(unit);
            GL45.glBindTextureUnit(unit, texture);
        }
    }

}
