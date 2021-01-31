package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ShadersAddon;
import net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess;
import net.optifine.shaders.DrawBuffers;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

@Mixin(Shaders.class)
public abstract class ShadersMixin {
    @Accessor(value = "drawBuffersColorAtt", remap = false)
    static public DrawBuffers[] getDrawBuffersColorAtt() {
        throw new AssertionError();
    }

    @Inject(method="loadShaderPackProperties", remap = false, at = @At("HEAD"))
    private static void resetLayersCount(CallbackInfo info) {
        ShadersAddon.setColorTextureTarget(GL_TEXTURE_2D);
        ShadersAddon.setColorLayerCount(1);
        ShadersAddon.setShadowTextureTarget(GL_TEXTURE_2D);
        ShadersAddon.setShadowLayerCount(1);
    }

    @Inject(method="setupFrameBuffer", remap = false, at = @At(value="INVOKE", target="Lnet/optifine/shaders/ShadersFramebuffer;setup()V", shift = At.Shift.BEFORE))
    private static void setFramebufferLayers(CallbackInfo info) {
        SMCLog.info("Trying to take framebuffers...");
        if (ShadersAccess.getDfb() != null) {
            ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setLayerCount(ShadersAddon.colorLayerCount);
            ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setTextureTarget(ShadersAddon.colorTexTarget);
        }
    }
}
