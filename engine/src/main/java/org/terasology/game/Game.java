/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.game;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.config.ModuleConfig;
import org.terasology.engine.ComponentSystemManager;
import org.terasology.engine.TerasologyEngine;
import org.terasology.engine.module.ModuleManager;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.module.Module;
import org.terasology.registry.CoreRegistry;
import org.terasology.engine.EngineTime;
import org.terasology.engine.paths.PathManager;
import org.terasology.persistence.StorageManager;
import org.terasology.world.WorldProvider;
import org.terasology.world.biomes.Biome;
import org.terasology.world.biomes.BiomeManager;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Immortius
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private EngineTime time;

    private String name = "";
    private String seed = "";

    private TerasologyEngine terasologyEngine;

    public Game(TerasologyEngine terasologyEngine, EngineTime time) {
        this.terasologyEngine = terasologyEngine;
        this.time = time;
    }

    public void load(GameManifest manifest) {
        this.name = manifest.getTitle();
        this.seed = manifest.getSeed();
        try {
            PathManager.getInstance().setCurrentSaveTitle(manifest.getTitle());
        } catch (IOException e) {
            logger.error("Failed to set save path", e);
        }
        time.setGameTime(manifest.getTime());
    }

    public void save() {
        save(true);
    }

    public void save(boolean flushAndShutdownStorageManager) {
        ComponentSystemManager componentSystemManager = CoreRegistry.get(ComponentSystemManager.class);
        for (ComponentSystem sys : componentSystemManager.iterateAll()) {
            sys.preSave();
        }
        terasologyEngine.stopThreads();

        StorageManager storageManager = CoreRegistry.get(StorageManager.class);
        if (storageManager != null) {
            GameManifest gameManifest = createGameManifest();
            saveGameManifest(gameManifest);
            try {
                storageManager.flush();
            } catch (IOException e) {
                logger.error("Failed to save game", e);
            }
            storageManager.shutdown();
        }

        terasologyEngine.restartThreads();

        for (ComponentSystem sys : componentSystemManager.iterateAll()) {
            sys.postSave();
        }


    }

    public void saveGameManifest(GameManifest gameManifest) {
        try {
            GameManifest.save(PathManager.getInstance().getCurrentSavePath().resolve(GameManifest.DEFAULT_FILE_NAME), gameManifest);
        } catch (IOException e) {
            logger.error("Failed to save world manifest", e);
        }
    }

    public GameManifest createGameManifest() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        BiomeManager biomeManager = CoreRegistry.get(BiomeManager.class);
        WorldProvider worldProvider = CoreRegistry.get(WorldProvider.class);

        GameManifest gameManifest = new GameManifest(name, seed, time.getGameTimeInMs());
        for (Module module : CoreRegistry.get(ModuleManager.class).getEnvironment()) {
            gameManifest.addModule(module.getId(), module.getVersion());
        }

        List<String> registeredBlockFamilies = Lists.newArrayList();
        for (BlockFamily family : blockManager.listRegisteredBlockFamilies()) {
            registeredBlockFamilies.add(family.getURI().toString());
        }
        gameManifest.setRegisteredBlockFamilies(registeredBlockFamilies);
        gameManifest.setBlockIdMap(blockManager.getBlockIdMap());
        List<Biome> biomes = biomeManager.getBiomes();
        Map<String, Short> biomeIdMap = new HashMap<>(biomes.size());
        for (Biome biome : biomes) {
            short shortId = biomeManager.getBiomeShortId(biome);
            String id = biomeManager.getBiomeId(biome);
            biomeIdMap.put(id, shortId);
        }
        gameManifest.setBiomeIdMap(biomeIdMap);
        gameManifest.addWorld(worldProvider.getWorldInfo());
        return gameManifest;
    }
}
