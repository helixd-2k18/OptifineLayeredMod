package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.optifine.shaders.DrawBuffers;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersFramebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

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
