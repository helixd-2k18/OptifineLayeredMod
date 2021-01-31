package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ducks.ShaderPackParserAccess;
import net.fabricmc.optilayeredmod.ducks.ShadersFramebufferAccess;
import net.optifine.Config;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.Program;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderPackParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Properties;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

@Mixin(ShaderPackParser.class)
public abstract class ShaderPackParserMixin implements ShaderPackParserAccess {

    @Inject(method="parseBuffersFlip", remap = false, at = @At("TAIL"))
    private static void parseLayerCount(Properties props, CallbackInfo info) {
        for (String s : (Set<String>)(Set<?>)props.keySet())
        {
            String[] astring = Config.tokenize(s, ".");

            if (astring.length == 3)
            {
                String s1 = astring[0];
                String s2 = astring[1];
                String s3 = astring[1];

                if (s1.equals("layers"))
                {
                    if (s2.equals("color")) {
                        if (ShadersAccess.getDfb() != null) {
                            if (s3.equals("enable")) {
                                String s4 = props.getProperty(s).trim();
                                if (s4.equals("true")) {
                                    ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setTextureTarget(GL_TEXTURE_2D_ARRAY);
                                } else {
                                    ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setTextureTarget(GL_TEXTURE_2D);
                                }
                            }

                            if (s3.equals("count")) {
                                String s4 = props.getProperty(s).trim();
                                ((ShadersFramebufferAccess) ShadersAccess.getDfb()).setLayerCount(parseInt(s4));
                            }
                        } else {
                            SMCLog.severe("Framebuffer doesn't exists");
                        }
                    } else
                    if (s2.equals("shadow")) {
                        if (ShadersAccess.getSfb() != null) {
                            if (s3.equals("enable")) {
                                String s4 = props.getProperty(s).trim();
                                if (s4.equals("true")) {
                                    ((ShadersFramebufferAccess) ShadersAccess.getSfb()).setTextureTarget(GL_TEXTURE_2D_ARRAY);
                                } else {
                                    ((ShadersFramebufferAccess) ShadersAccess.getSfb()).setTextureTarget(GL_TEXTURE_2D);
                                }
                            }

                            if (s3.equals("count")) {
                                String s4 = props.getProperty(s).trim();
                                ((ShadersFramebufferAccess) ShadersAccess.getSfb()).setLayerCount(parseInt(s4));
                            }
                        } else {
                            SMCLog.severe("Shadow framebuffer doesn't exists");
                        }
                    }
                }
            }
        }
    }

}
