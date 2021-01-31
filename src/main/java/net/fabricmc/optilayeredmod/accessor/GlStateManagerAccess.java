package net.fabricmc.optilayeredmod.accessor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Mixin(GlStateManager.class)
public interface GlStateManagerAccess {

    @Accessor("TEXTURES")
    static GlStateManager.Texture2DState[] getTextures() {
        throw new AssertionError();
    }

    @Accessor("activeTexture")
    static int getActiveTexture() {
        throw new AssertionError();
    }

    @Accessor("activeTexture")
    static void setActiveTexture(int activeTexture) {

    }
}
