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
public abstract class GlStateManagerAccess {

    //@Accessor("TEXTURES")
    //public static GlStateManager.Texture2DState[] getTextures() throws IllegalAccessException {
    //    return (GlStateManager.Texture2DState[]) FieldUtils.readField(GlStateManager.class, "field_20483", true);
    //}

    //@Accessor("activeTexture")
    //public static int getActiveTexture() throws IllegalAccessException {
    //    throw new AssertionError();
        //return (int) FieldUtils.readField(GlStateManager.class, "field_20482", true);
    //}

    //@Accessor("activeTexture")
    //public static void setActiveTexture(int activeTexture) throws IllegalAccessException {
        //throw new AssertionError();
        //FieldUtils.writeField(GlStateManager.class, "field_20482", activeTexture, true);
    //}

}
