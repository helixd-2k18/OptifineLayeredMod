package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.optilayeredmod.GlStateManagerUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GlStateManager.class)
abstract public class GlStateManagerMixin {

    /* // NOT so good solution
    @Redirect(remap = true, method="bindTexture", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL11;glBindTexture(II)V", remap = false))
    private static void onBindTexture(int target, int texture) {
        GL11.glBindTexture(GlStateManagerUtils.texTarget, texture);
    }*/

}
