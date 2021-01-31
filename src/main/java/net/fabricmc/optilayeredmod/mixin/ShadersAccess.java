package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.optifine.shaders.DrawBuffers;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersFramebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

@Mixin(Shaders.class)
public interface ShadersAccess {

    @Accessor(value = "dfb", remap = false)
    static ShadersFramebuffer getDfb() {
        throw new AssertionError();
    }

    @Accessor(value = "sfb", remap = false)
    static ShadersFramebuffer getSfb() {
        throw new AssertionError();
    }
}
