package com.fabbe50.infodump;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

public final class Infodump {
    public static final String MOD_ID = "infodump";

    public static void init() {
        // Write common init code here.
        CommandRegistrationEvent.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            commandDispatcher.register(Commands.literal("dumpinfo").executes(context -> dumpInfo(context.getSource())));
        });
    }

    public static int dumpInfo(CommandSourceStack commandSourceStack) {
        ServerLevel level = commandSourceStack.getLevel();

        try {
            RegistryAccess registryAccess = level.registryAccess();
            HolderLookup.RegistryLookup<Block> blocks = registryAccess.lookup(Registries.BLOCK).orElseThrow();
            HolderLookup.RegistryLookup<BlockEntityType<?>> blockEntities = registryAccess.lookup(Registries.BLOCK_ENTITY_TYPE).orElseThrow();
            HolderLookup.RegistryLookup<Item> items = registryAccess.lookup(Registries.ITEM).orElseThrow();
            HolderLookup.RegistryLookup<EntityType<?>> entities = registryAccess.lookup(Registries.ENTITY_TYPE).orElseThrow();
            HolderLookup.RegistryLookup<Biome> biomes = registryAccess.lookup(Registries.BIOME).orElseThrow();
            HolderLookup.RegistryLookup<Level> dimensions = registryAccess.lookup(Registries.DIMENSION).orElseThrow();

            printSet("blocks", blocks.listElementIds().collect(Collectors.toSet()));
            printSet("blockEntities", blockEntities.listElementIds().collect(Collectors.toSet()));
            printSet("items", items.listElementIds().collect(Collectors.toSet()));
            printSet("entities", entities.listElementIds().collect(Collectors.toSet()));
            printSet("biomes", biomes.listElementIds().collect(Collectors.toSet()));
            printSet("dimensions", dimensions.listElementIds().collect(Collectors.toSet()));
            commandSourceStack.sendSuccess(() -> Component.literal("Dumped info successfully. It can be found in the infodump folder located in the game directory."), true);
            return 1;
        } catch (Exception e) {
            System.out.println(e);
            commandSourceStack.sendFailure(Component.literal("Failed to dump info."));
            return 0;
        }
    }

    public static void printSet(String name, Set<ResourceKey<?>> resourceKeys) {
        File dir = new File(Platform.getGameFolder().toFile() + "/infodump");
        makeDirIfNotExists(dir);
        File file = new File(dir + "/" + name + ".txt");
        makeOrClearFile(file);

        try (PrintStream fos = new PrintStream(file)) {
            for (ResourceKey<?> key : resourceKeys) {
                fos.println(key.location());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void makeDirIfNotExists(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void makeOrClearFile(File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
