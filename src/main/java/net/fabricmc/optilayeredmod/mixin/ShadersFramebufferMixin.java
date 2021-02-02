package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.optilayeredmod.GlStateManagerUtils;
import net.optifine.shaders.*;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.glFramebufferTexture3D;

@Mixin(ShadersFramebuffer.class)
public abstract class ShadersFramebufferMixin implements net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess {
    @Shadow(remap = false) private String name;
    @Shadow(remap = false) private int width;
    @Shadow(remap = false) private int height;
    @Shadow(remap = false) private int usedColorBuffers;
    @Shadow(remap = false) private int usedDepthBuffers;
    @Shadow(remap = false) private int maxDrawBuffers;
    @Shadow(remap = false) private boolean[] depthFilterNearest;
    @Shadow(remap = false) private boolean[] depthFilterHardware;
    @Shadow(remap = false) private boolean[] colorFilterNearest;
    @Shadow(remap = false) private int[] buffersFormat;
    @Shadow(remap = false) private int[] colorTextureUnits;
    @Shadow(remap = false) private int[] depthTextureUnits;
    @Shadow(remap = false) private int glFramebuffer;
    @Shadow(remap = false) private FlipTextures colorTexturesFlip;
    @Shadow(remap = false) private IntBuffer depthTextures;
    @Shadow(remap = false) private DrawBuffers drawBuffers;
    @Shadow(remap = false) private DrawBuffers activeDrawBuffers;
    @Shadow(remap = false) private int[] drawColorTextures;
    @Shadow(remap = false) private int[] drawColorTexturesMap;
    @Shadow(remap = false) private boolean[] dirtyColorTextures;

    @Unique private int glReadFramebuffer = 0;
    @Unique private int glRenderbuffer = 0;
    @Unique private int attachOffset = GL_COLOR_ATTACHMENT0;
    @Unique private int texTarget = GL_TEXTURE_2D;
    @Unique private int layerCount = 1;

    @Override
    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    @Override
    public void setTextureTarget(int texTarget) {
        this.texTarget = texTarget;
    }

    @Invoker(value="getGlFramebuffer", remap = false)
    abstract public int _getGlFramebuffer();

    @Invoker(value="isColorBufferIndex", remap = false)
    abstract public boolean _isColorBufferIndex(int colorIndex);

    @Invoker(value="isDrawBufferIndex", remap = false)
    abstract public boolean _isDrawBufferIndex(int drawBufferIndex);

    @Invoker(value="exists", remap = false)
    abstract public boolean _exists();

    @Invoker(value="delete", remap = false)
    abstract public void _delete();

    @Invoker(value="setDrawBuffers", remap = false)
    abstract public void _setDrawBuffers(DrawBuffers drawBuffersIn);


    // WARNING! May contain other usages of that method
    @Redirect(remap = false, method="setFramebufferTexture2D", at=@At(value = "INVOKE", target = "Lorg/lwjgl/opengl/EXTFramebufferObject;glFramebufferTexture2DEXT(IIIII)V", remap=false))
    private void onSetFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
        if (this.texTarget == GL_TEXTURE_2D) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, texTarget, texture, level);
        } else {
            GL32.glFramebufferTexture(target, attachment, texture, level);
        }

        //SMCLog.info("Tried to set framebuffer texture");
    }


    @Redirect(remap = false, method="generateDepthMipmaps", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glTexParameteri(III)V", remap = false))
    private void onGenerateDepthMipmapsTexParameter(int target, int pname, int param) {
        GL30.glTexParameteri(this.texTarget, pname, param);
    }

    @Redirect(remap = false, method="generateDepthMipmaps", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glGenerateMipmap(I)V", remap = false))
    private void onGenerateDepthMipmapsGL(int target) {
        GL30.glGenerateMipmap(this.texTarget);
    }

    @Redirect(remap = false, method="generateDepthMipmaps", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onGenerateDepthMipmapsBindTexture(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }

    @Redirect(remap = false, method="generateColorMipmaps", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glTexParameteri(III)V", remap = false))
    private void onGenerateColorMipmapsTexParameter(int target, int pname, int param) {
        GL30.glTexParameteri(this.texTarget, pname, param);
    }

    @Redirect(remap = false, method="generateColorMipmaps", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glGenerateMipmap(I)V", remap = false))
    private void onGenerateColorMipmapsGL(int target) {
        GL30.glGenerateMipmap(this.texTarget);
    }

    @Redirect(remap = false, method="generateColorMipmaps", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onGenerateColorMipmapsBindTexture(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }

    @Redirect(remap = false, method="flipColorTexture", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onFlipColorTextureBindTexture(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }


    @Redirect(remap = false, method="setup", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onSetupBindTexture(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);

        //SMCLog.info("Tried to bind texture");
    }

    @Redirect(remap = false, method="setup", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glTexParameteri(III)V", remap = false))
    private void onSetupTexParameter(int target, int pname, int param) {
        GL30.glTexParameteri(this.texTarget, pname, param);

        //SMCLog.info("Tried to set texture parameter");
    }

    @Redirect(remap = false, method="setup", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glTexImage2D(IIIIIIIILjava/nio/ByteBuffer;)V", remap = false))
    private void onSetupTexImage(int target, int level, int internalformat, int width, int height, int border, int format, int type, @Nullable java.nio.ByteBuffer pixels) {
        if (this.texTarget == GL_TEXTURE_2D) {
            GL30.glTexImage2D(this.texTarget, level, internalformat, width, height, border, format, type, pixels);
        } else {
            GL30.glTexImage3D(this.texTarget, level, internalformat, width, height, this.layerCount, border, format, type, pixels);
        }

        //SMCLog.info("Tried to make texture");
    }

    @Redirect(remap = false, method="setup", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL30;glTexImage2D(IIIIIIIILjava/nio/FloatBuffer;)V", remap = false))
    private void onSetupTexImage(int target, int level, int internalformat, int width, int height, int border, int format, int type, @Nullable java.nio.FloatBuffer pixels) {
        if (this.texTarget == GL_TEXTURE_2D) {
            GL30.glTexImage2D(this.texTarget, level, internalformat, width, height, border, format, type, pixels);
        } else {
            GL30.glTexImage3D(this.texTarget, level, internalformat, width, height, this.layerCount, border, format, type, pixels);
        }

        //SMCLog.info("Tried to make texture");
    }


    @Redirect(remap = false, method="setColorBuffersFiltering", at=@At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL11;glTexParameteri(III)V", remap = false))
    private void onSetColorBuffersFilteringTexParameter(int target, int pname, int param) {
        GL11.glTexParameteri(this.texTarget, pname, param);
    }

    @Redirect(remap = false, method="setColorBuffersFiltering", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onSetColorBuffersFilteringBindTexture(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }


    @Redirect(remap = false, method="bindDepthTextures", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onBindDepthTextures(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }

    @Redirect(remap = false, method="bindColorTextures", at=@At(value = "INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;bindTexture(I)V", remap = true))
    private void onBindColorTextures(int texture) throws IllegalAccessException {
        GlStateManagerUtils.bindTexture(this.texTarget, texture);
    }


    //
    @Inject(remap = false, method="setup", at = @At("HEAD"))
    private void onSetup(CallbackInfo info) {
        if (this.texTarget == GL_TEXTURE_2D) { this.layerCount = 1; };

        if (this.texTarget == GL_TEXTURE_2D) {
            SMCLog.info("USED_TEXTURE_2D");
        } else
        if (this.texTarget == GL_TEXTURE_3D) {
            SMCLog.info("OMG! USED_TEXTURE_3D");
        }

        if (this.layerCount == 1) {
            SMCLog.info("Something wrong...");
        } else {
            SMCLog.info("Layer count defined correctly...");
        }
    }


    // DEBUG PURPOSE!
    @Inject(remap = false, method="setup", at = @At("TAIL"))
    private void onSetupTail(CallbackInfo info) throws Exception {

    }


}
