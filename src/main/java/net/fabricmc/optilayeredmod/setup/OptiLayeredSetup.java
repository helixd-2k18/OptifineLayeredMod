package net.fabricmc.optilayeredmod.setup;

import org.spongepowered.asm.mixin.Mixins;

public class OptiLayeredSetup implements Runnable {
    @Override
    public void run() {
        Mixins.addConfiguration("optilayered.mixins.json");
    }
}
