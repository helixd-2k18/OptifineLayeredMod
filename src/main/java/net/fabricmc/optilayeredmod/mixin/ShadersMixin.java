package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess;
import net.optifine.shaders.DrawBuffers;
import net.optifine.shaders.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@Mixin(Shaders.class)
public abstract class ShadersMixin {
    @Accessor(value = "drawBuffersColorAtt", remap = false)
    static public DrawBuffers[] getDrawBuffersColorAtt() {
        throw new AssertionError();
    }

    @Inject(method="loadShaderPackProperties", remap = false, at = @At("HEAD"))
    private static void resetLayersCount(CallbackInfo info) {
        if (ShadersAccess.getDfb() != null) {
            ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setTextureTarget(GL_TEXTURE_2D);
            ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setLayerCount(1);
        }
        if (ShadersAccess.getSfb() != null) {
            ((ShadersFramebufferAccess) ShadersAccess.getSfb()).setTextureTarget(GL_TEXTURE_2D);
            ((ShadersFramebufferAccess) ShadersAccess.getSfb()).setLayerCount(1);
        }
    }
}
