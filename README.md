# Optifine layering mini-mod (Fabric)

## What is it?

This is mod, that adds multi-layered framebuffers option into Optifine shaders. This is experimental mod, and adds currently unlimited variable layer count.

### `shaders.properties`

I added those really working options.

```
layers.color.enable=true # valid usage
layers.color.count=<N>
layers.shadow.enable=true # valid usage
layers.shadow.count=<N>
```

### Issue tracker of Optifine

- [Optifine feature request](https://github.com/sp614x/optifine/issues/5259)
- [Stackoverflow explanation](https://stackoverflow.com/a/18287271)
- [Geometry shader layers](https://www.khronos.org/opengl/wiki/Geometry_Shader#Layered_rendering)

## Setup

1. Download package from [here](https://github.com/helixd2s/OptifineBlendMod/releases), and unzip archive
2. Put `optifine-layered-mod-<version>` into `mods/` directory, or use MultiMC.
3. Install Optifabric and Optifine as `mods/` (tested with `OptiFine_1.16.5_HD_U_G7_pre4sc3`). 

## License

TODO...
