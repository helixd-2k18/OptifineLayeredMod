package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ducks.FlipTexturesAccess;
import net.optifine.shaders.FlipTextures;
import net.optifine.shaders.config.ShaderPackParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.glCreateTextures;

@Mixin(FlipTextures.class)
public abstract class FlipTexturesMixin implements FlipTexturesAccess {

    @Shadow(remap = false) private IntBuffer texturesA;
    @Shadow(remap = false) private IntBuffer texturesB;

    @Override
    public void genTextures(int target) {
        glCreateTextures(target, this.texturesA);
        glCreateTextures(target, this.texturesB);
    }

}
