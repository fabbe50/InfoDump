package com.fabbe50.infodump.neoforge;

import com.fabbe50.infodump.Infodump;
import net.neoforged.fml.common.Mod;

@Mod(Infodump.MOD_ID)
public final class InfodumpNeoForge {
    public InfodumpNeoForge() {
        // Run our common setup.
        Infodump.init();
    }
}
