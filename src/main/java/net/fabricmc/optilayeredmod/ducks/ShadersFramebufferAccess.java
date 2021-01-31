package net.fabricmc.optilayeredmod.ducks;

import net.optifine.shaders.DrawBuffers;
import org.spongepowered.asm.mixin.gen.Invoker;

public interface ShadersFramebufferAccess {
    public int _getGlFramebuffer();
    public boolean _isColorBufferIndex(int colorIndex);
    public boolean _isDrawBufferIndex(int drawBufferIndex);
    public boolean _exists();
    public void _delete();
    public void _setDrawBuffers(DrawBuffers drawBuffersIn);
}
