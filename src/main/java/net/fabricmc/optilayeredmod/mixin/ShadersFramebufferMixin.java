package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.optilayeredmod.GlStateManagerUtils;
import net.fabricmc.optilayeredmod.ducks.FlipTexturesAccess;
import net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess;
import net.minecraft.client.util.math.Vector4f;
import net.optifine.shaders.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL45.*;

@Mixin(ShadersFramebuffer.class)
public abstract class ShadersFramebufferMixin implements ShadersFramebufferAccess {
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


}
