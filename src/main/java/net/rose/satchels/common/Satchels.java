package net.rose.satchels.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Satchels implements ModInitializer {
    public static final String MOD_ID = "satchels";

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
    }
}
