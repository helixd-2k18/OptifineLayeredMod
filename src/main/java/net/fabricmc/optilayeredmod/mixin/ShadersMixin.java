package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ducks.ShadersAccess;
import net.optifine.shaders.DrawBuffers;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderPackParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Shaders.class)
public abstract class ShadersMixin implements ShadersAccess {
    @Accessor(value = "drawBuffersColorAtt", remap = false)
    static public DrawBuffers[] getDrawBuffersColorAtt() {
        throw new AssertionError();
    }
}
