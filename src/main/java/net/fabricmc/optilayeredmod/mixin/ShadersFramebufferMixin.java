package net.fabricmc.optilayeredmod.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.optilayeredmod.ducks.FlipTexturesAccess;
import net.fabricmc.optilayeredmod.GlStateManagerUtils;
import net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess;
import net.minecraft.client.util.math.Vector4f;
import net.optifine.shaders.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
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


    void glTexImage2D(int texture, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer data) throws IllegalAccessException {
        int texTarget = this.texTarget;

        if (internalformat == GL_DEPTH_COMPONENT32F && texTarget == GL_TEXTURE_3D) { texTarget = GL_TEXTURE_2D_ARRAY; };

        GlStateManagerUtils.bindTexture(texTarget, texture);
        if (this.texTarget == GL_TEXTURE_2D) {
            GL45.glTexImage2D(texTarget, level, internalformat, width, height, border, format, type, data);
        } else {
            GL45.glTexImage3D(texTarget, level, internalformat, width, height, this.layerCount, border, format, type, data);
        }
    }


    @Overwrite(remap = false)
    public void bindFramebuffer() {
        GlState.bindFramebuffer((ShadersFramebuffer)((Object)this));
    };

    @Overwrite(remap = false)
    public void generateDepthMipmaps(boolean[] depthMipmapEnabled) {
        this.bindFramebuffer();

        for(int i = 0; i < this.usedDepthBuffers; ++i) {
            if (depthMipmapEnabled[i]) {
                glGenerateTextureMipmap(this.depthTextures.get(i));
                glTextureParameteri(this.depthTextures.get(i), 10241, this.depthFilterNearest[i] ? 9984 : 9987);
            }
        }

    }

    @Overwrite(remap = false)
    public void generateColorMipmaps(boolean main, boolean[] colorMipmapEnabled) {
        this.bindFramebuffer();

        for(int i = 0; i < this.usedColorBuffers; ++i) {
            if (colorMipmapEnabled[i]) {
                glGenerateTextureMipmap(this.colorTexturesFlip.get(main, i));
                glTextureParameteri(this.colorTexturesFlip.get(main, i), 10241, this.colorFilterNearest[i] ? 9984 : 9987);
            }
        }

    }

    @Overwrite(remap = false)
    public void flipColorTexture(int index) throws IllegalAccessException {
        this.bindFramebuffer();
        this.colorTexturesFlip.flip(index);

        // bind flip
        GlStateManagerUtils.bindTextureUnit(this.colorTextureUnits[index], this.colorTexturesFlip.getA(index));
        this.setFramebufferTexture2D(GL_FRAMEBUFFER, this.attachOffset + index, this.texTarget, this.colorTexturesFlip.getB(index), 0);

        // force copy layers
        glNamedFramebufferReadBuffer(this.glFramebuffer, this.attachOffset + index);
        glCopyTextureSubImage3D(this.colorTexturesFlip.getA(index), 0, 0, 0, 0, 0, 0, this.width, this.height);
        glReadBuffer(0);
    }

    @Overwrite(remap = false)
    public void clearColorBuffers(boolean[] buffersClear, Vector4f[] clearColors) {
        this.bindFramebuffer();
        for(int i = 0; i < this.usedColorBuffers; ++i) {
            if (buffersClear[i]) {
                Vector4f col = clearColors[i];
                //for (int l=0;l<this.layerCount;l++) {
                    if (this.colorTexturesFlip.isChanged(i)) {
                        glClearTexImage(this.colorTexturesFlip.getB(i), 0, GL_RGBA, GL_FLOAT, new float[]{col.getX(), col.getY(), col.getZ(), col.getW()});
                    }
                    glClearTexImage(this.colorTexturesFlip.getA(i), 0, GL_RGBA, GL_FLOAT, new float[]{col.getX(), col.getY(), col.getZ(), col.getW()});
                //}
            }
        }
    }

    @Overwrite(remap = false)
    public void clearDepthBuffer(Vector4f col) {
        this.bindFramebuffer();
        //for (int l=0;l<this.layerCount;l++) {
            glClearTexImage(this.depthTextures.get(0), 0, GL_DEPTH_COMPONENT, GL_FLOAT, new float[]{col.getX(), col.getY(), col.getZ(), col.getW()});
        //}
    }

    @Overwrite(remap = false)
    public void setColorBuffersFiltering(int minFilter, int magFilter) {
        this.bindFramebuffer();

        for(int i = 0; i < this.usedColorBuffers; ++i) {
            GL45.glTextureParameteri(this.colorTexturesFlip.getA(i), GL_TEXTURE_MIN_FILTER, minFilter);
            GL45.glTextureParameteri(this.colorTexturesFlip.getA(i), GL_TEXTURE_MAG_FILTER, magFilter);
            GL45.glTextureParameteri(this.colorTexturesFlip.getB(i), GL_TEXTURE_MIN_FILTER, minFilter);
            GL45.glTextureParameteri(this.colorTexturesFlip.getB(i), GL_TEXTURE_MAG_FILTER, magFilter);
        }

    }

    @Overwrite(remap = false)
    public void setFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
        int colorIndex = attachment - this.attachOffset;
        if (this._isColorBufferIndex(colorIndex)) {
            this.drawColorTextures[colorIndex] = texture;
            if (colorIndex >= this.maxDrawBuffers) {
                int indexMapped = this.drawColorTexturesMap[colorIndex];
                if (!this._isDrawBufferIndex(indexMapped)) {
                    return;
                }

                attachment = this.attachOffset + indexMapped;
            }
        }

        this.bindFramebuffer();

        //
        glNamedFramebufferTexture(this._getGlFramebuffer(), attachment, texture, level);
    }

    @Overwrite(remap = false)
    public void bindDepthTextures(int[] depthTextureImageUnits) throws IllegalAccessException {
        this.bindFramebuffer();
        for(int i = 0; i < this.usedDepthBuffers; ++i) {
            GlStateManagerUtils.bindTextureUnit(depthTextureImageUnits[i], this.depthTextures.get(i));
        }
    }

    @Overwrite(remap = false)
    public void bindColorTextures(int startColorBuffer, int[] colorTextureImageUnits) throws IllegalAccessException {
        this.bindFramebuffer();
        for(int i = startColorBuffer; i < this.usedColorBuffers; ++i) {
            GlStateManagerUtils.bindTextureUnit(colorTextureImageUnits[i], this.colorTexturesFlip.getA(i));
        }
    }

    @Overwrite(remap = false)
    private void setDrawColorTexturesMap(int[] newColorTexturesMap) {
        this.bindFramebuffer();

        int i;
        int ai;
        for(i = 0; i < this.maxDrawBuffers; ++i) {
            if (this.dirtyColorTextures[i]) {
                ai = this.drawColorTextures[i];
                glNamedFramebufferTexture(this._getGlFramebuffer(), this.attachOffset+i, ai, 0);
                this.dirtyColorTextures[i] = false;
            }
        }

        this.drawColorTexturesMap = newColorTexturesMap;

        for(i = this.maxDrawBuffers; i < this.drawColorTexturesMap.length; ++i) {
            ai = this.drawColorTexturesMap[i];
            if (ai >= 0) {
                int texture = this.drawColorTextures[i];
                glNamedFramebufferTexture(this._getGlFramebuffer(), this.attachOffset+i, texture, 0);
                this.dirtyColorTextures[ai] = true;
            }
        }

    }

    @Overwrite(remap = false)
    public void setup() throws Exception {
        if (this._exists()) {
            this._delete();
        }

        //if (this.layerCount > 1) { this.texTarget = GL_TEXTURE_2D_ARRAY; }
        //if (this.layerCount <= 1) { this.texTarget = GL_TEXTURE_2D; }
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

        this.colorTexturesFlip = new FlipTextures(this.name + "ColorTexturesFlip", this.usedColorBuffers);
        this.depthTextures = BufferUtils.createIntBuffer(this.usedDepthBuffers);
        this.drawColorTextures = new int[this.usedColorBuffers];
        this.drawColorTexturesMap = new int[this.usedColorBuffers];
        this.dirtyColorTextures = new boolean[this.maxDrawBuffers];
        Arrays.fill(this.drawColorTextures, 0);
        Arrays.fill(this.drawColorTexturesMap, -1);
        Arrays.fill(this.dirtyColorTextures, false);

        int status;
        for(status = 0; status < this.drawBuffers.limit(); ++status) {
            this.drawBuffers.put(status, this.attachOffset + status);
        }

        //
        this.glReadFramebuffer = GL45.glCreateFramebuffers();
        this.glFramebuffer = GL45.glCreateFramebuffers();//EXTFramebufferObject.glGenFramebuffersEXT();
        GL45.glNamedFramebufferDrawBuffers(this.glReadFramebuffer, new int[]{this.attachOffset});

        //
        this.bindFramebuffer();
        GL30.glDrawBuffers(0);
        GL30.glReadBuffer(0);
        glCreateTextures(this.texTarget == GL_TEXTURE_2D ? GL_TEXTURE_2D : GL_TEXTURE_2D_ARRAY, (IntBuffer)this.depthTextures.clear().limit(this.usedDepthBuffers));
        ((FlipTexturesAccess)this.colorTexturesFlip.clear().limit(this.usedColorBuffers)).genTextures(this.texTarget);
        this.depthTextures.position(0);
        this.colorTexturesFlip.position(0);

        int filter;
        for(status = 0; status < this.usedDepthBuffers; ++status) {
            int texture = this.depthTextures.get(status);

            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_S, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_T, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_R, 33071);
            filter = this.depthFilterNearest[status] ? 9728 : 9729;
            GL45.glTextureParameteri(texture, 10241, filter);
            GL45.glTextureParameteri(texture, 10240, filter);
            if (this.depthFilterHardware[status]) {
                GL45.glTextureParameteri(texture, 34892, 34894);
            }
            this.glTexImage2D(this.depthTextures.get(status), 0, GL_DEPTH_COMPONENT32F, this.width, this.height, 0, 6402, 5126, (ByteBuffer)null);
        }

        this.setFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, this.texTarget, this.depthTextures.get(0), 0);
        //glNamedFramebufferRenderbuffer(this.glFramebuffer, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.glRenderbuffer);
        Shaders.checkGLError("FBS " + this.name + " depth");

        for(status = 0; status < this.usedColorBuffers; ++status) {
            int texture = this.colorTexturesFlip.getA(status);

            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_S, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_T, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_R, 33071);
            filter = this.colorFilterNearest[status] ? 9728 : 9729;
            GL45.glTextureParameteri(texture, 10241, filter);
            GL45.glTextureParameteri(texture, 10240, filter);
            this.glTexImage2D(texture, 0, this.buffersFormat[status], this.width, this.height, 0, Shaders.getPixelFormat(this.buffersFormat[status]), 33639, (ByteBuffer)null);

            this.setFramebufferTexture2D(GL_FRAMEBUFFER, this.attachOffset + status, this.texTarget, texture, 0);
            Shaders.checkGLError("FBS " + this.name + " colorA");
        }

        for(status = 0; status < this.usedColorBuffers; ++status) {
            int texture = this.colorTexturesFlip.getB(status);

            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_S, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_T, 33071);
            GL45.glTextureParameteri(texture, GL_TEXTURE_WRAP_R, 33071);
            filter = this.colorFilterNearest[status] ? 9728 : 9729;
            GL45.glTextureParameteri(texture, 10241, filter);
            GL45.glTextureParameteri(texture, 10240, filter);
            this.glTexImage2D(texture, 0, this.buffersFormat[status], this.width, this.height, 0, Shaders.getPixelFormat(this.buffersFormat[status]), 33639, (ByteBuffer)null);
            Shaders.checkGLError("FBS " + this.name + " colorB");
        }

        GlStateManager.bindTexture(0);
        if (this.usedColorBuffers > 0) {
            this._setDrawBuffers(this.drawBuffers);
            GL30.glReadBuffer(0);
        }

        status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status != 36053) {
            Shaders.printChatAndLogError("[Shaders] Error creating framebuffer: " + this.name + ", status: " + status);

            if (status == GL_FRAMEBUFFER_UNDEFINED ) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_UNDEFINED"); };
            if (status == GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"); };
            if (status == GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"); };
            if (status == GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER "); };
            if (status == GL_FRAMEBUFFER_UNSUPPORTED) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_UNSUPPORTED"); };
            if (status == GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS) { SMCLog.severe("Reason translate: " + "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS"); };

        } else {
            SMCLog.info("Framebuffer created: " + this.name);
        }

        //throw new Exception("TEST");

    }

}
