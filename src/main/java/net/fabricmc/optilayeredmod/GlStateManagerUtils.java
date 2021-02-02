package net.fabricmc.optilayeredmod;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.optilayeredmod.mixin.GlStateManagerAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public abstract class GlStateManagerUtils {

    public static int texTarget = GL_TEXTURE_2D;

    public static void bindTexture(int target, int texture) throws IllegalAccessException {
        if (target != GL_TEXTURE_2D) { // resolve conflict with mods
            GlStateManager.Texture2DState[] TEXTURES = GlStateManagerAccess.getTextures();
            int activeTexture = GlStateManagerAccess.getActiveTexture();

            RenderSystem.assertThread((Supplier<Boolean>) ((Supplier) RenderSystem::isOnRenderThreadOrInit));
            if (texture != TEXTURES[activeTexture].boundTexture) {
                TEXTURES[activeTexture].boundTexture = texture;
                GL11.glBindTexture(target, (int) texture);
            }
        } else {
            GlStateManager.bindTexture(texture);
        }

        /* // NOT so good solution
        // use target hack
        GlStateManagerUtils.texTarget = target;
        GlStateManager.bindTexture(texture);
        GlStateManagerUtils.texTarget = GL_TEXTURE_2D;
        */
    }

    // INCOMPATIBLE WITH MOST MODS!
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
