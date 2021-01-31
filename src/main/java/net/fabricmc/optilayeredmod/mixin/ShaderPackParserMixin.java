package net.fabricmc.optilayeredmod.mixin;

import net.fabricmc.optilayeredmod.ducks.ShaderPackParserAccess;
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

@Mixin(ShaderPackParser.class)
public abstract class ShaderPackParserMixin implements ShaderPackParserAccess {



}
