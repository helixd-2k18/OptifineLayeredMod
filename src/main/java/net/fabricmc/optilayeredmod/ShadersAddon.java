package net.fabricmc.optilayeredmod;

import net.fabricmc.optilayeredmod.mixin.ShadersAccess;
import net.optifine.shaders.SMCLog;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

public abstract class ShadersAddon {

    public static int colorTexTarget = GL_TEXTURE_2D;
    public static int colorLayerCount = 1;
    public static int shadowTexTarget = GL_TEXTURE_2D;
    public static int shadowLayerCount = 1;


    public static void setShadowLayerCount(int layerCount) {
        ShadersAddon.shadowLayerCount = layerCount;
    }
    public static void setShadowTextureTarget(int texTarget) {
        if (texTarget == GL_TEXTURE_2D) {
            SMCLog.info("SETUP_TEXTURE_2D");
        } else
        if (texTarget == GL_TEXTURE_2D_ARRAY) {
            SMCLog.info("SETUP_TEXTURE_2D_ARRAY");
        }
        ShadersAddon.shadowTexTarget = texTarget;

        if (ShadersAddon.shadowTexTarget == GL_TEXTURE_2D && texTarget == GL_TEXTURE_2D_ARRAY) {
            SMCLog.info("BUT_SILL_2D");
        }
    }


    public static void setColorLayerCount(int layerCount) {
        ShadersAddon.colorLayerCount = layerCount;
    }
    public static void setColorTextureTarget(int texTarget) {
        if (texTarget == GL_TEXTURE_2D) {
            SMCLog.info("SETUP_TEXTURE_2D");
        } else
        if (texTarget == GL_TEXTURE_2D_ARRAY) {
            SMCLog.info("SETUP_TEXTURE_2D_ARRAY");
        }
        ShadersAddon.colorTexTarget = texTarget;

        if (ShadersAddon.colorTexTarget == GL_TEXTURE_2D && texTarget == GL_TEXTURE_2D_ARRAY) {
            SMCLog.info("BUT_SILL_2D");
        }
    }

}
