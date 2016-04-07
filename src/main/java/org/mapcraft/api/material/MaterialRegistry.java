/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.material;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.mapcraft.api.Server;
import org.mapcraft.api.io.store.simple.BinaryFileStore;
import org.mapcraft.api.util.SyncedStringMap;
import org.mapcraft.io.store.simple.MemoryStore;
import org.mapcraft.math.MathUtil;

/**
 *
 * @author rmalot
 */
public abstract class MaterialRegistry {
    private final static ConcurrentHashMap<String, Material> nameLookup = new ConcurrentHashMap<>(1000);
    private final static int MAX_SIZE = 1 << 16;
    @SuppressWarnings ({"unchecked", "rawtypes"})
    private final static AtomicReference<Material[]>[] materialLookup = new AtomicReference[MAX_SIZE];
    private static boolean setup = false;
    private static SyncedStringMap materialRegistry;
    private final static Material[] NULL_MATERIAL_ARRAY = new Material[] {null};

    static {
        for (int i = 0; i < materialLookup.length; i++) {
            materialLookup[i] = new AtomicReference<>();
            materialLookup[i].set(NULL_MATERIAL_ARRAY);
        }
    }

    public static void setupServer(Server server) {
        if (setup) {
            throw new IllegalStateException("Can not setup material registry twice!");
        }
        Path serverItemMap = server.getWorldManager().getWorldFolder().resolve("materials.dat");
        BinaryFileStore store = new BinaryFileStore(serverItemMap);
        materialRegistry = SyncedStringMap.create(null, store, 1, Short.MAX_VALUE, Material.class.getName());
        if (Files.exists(serverItemMap)) {
            store.load();
        }

        setup = true;
    }

    public static void setupClient() {
        if (setup) {
            throw new IllegalStateException("Can not setup material registry twice!");
        }
        materialRegistry = SyncedStringMap.create(null, new MemoryStore<>(), 1, Short.MAX_VALUE, Material.class.getName());

        setup = true;
    }

    /**
     * Checks whether the MaterialRegistry has been set up. If this returns {@code true}, further attempts to set up the registry will result in {@link IllegalStateException}.
     *
     * @return whether the MaterialRegistry has been set up
     */
    public static boolean isSetup() {
        return setup;
    }

    /**
     * Registers the material in the material lookup service
     *
     * @param material to register
     * @return id of the material registered
     */
    protected static int register(Material material) {
        if (material.isSubMaterial()) {
            material.getParentMaterial().registerSubMaterial(material);
            nameLookup.put(formatName(material.getDisplayName()), material);
            return material.getParentMaterial().getId();
        } else {
            int id = materialRegistry.register(material.getName());
            Material[] subArray = new Material[] {material};
            if (!materialLookup[id].compareAndSet(NULL_MATERIAL_ARRAY, subArray)) {
                throw new IllegalArgumentException(materialLookup[id].get() + " is already mapped to id: " + material.getId() + "!");
            }

            nameLookup.put(formatName(material.getDisplayName()), material);
            return id;
        }
    }

    protected static AtomicReference<Material[]> getSubMaterialReference(short id) {
        return materialLookup[id];
    }

    /**
     * Registers the material in the material lookup service
     *
     * @param material to register
     * @return id of the material registered.
     */
    protected static int register(Material material, int id) {
        materialRegistry.register(material.getName(), id);
        Material[] subArray = new Material[] {material};
        if (!materialLookup[id].compareAndSet(NULL_MATERIAL_ARRAY, subArray)) {
            throw new IllegalArgumentException(materialLookup[id].get()[0] + " is already mapped to id: " + material.getId() + "!");
        }

        nameLookup.put(formatName(material.getName()), material);
        return id;
    }

    /**
     * Gets the material from the given id
     *
     * @param id to get
     * @return material or null if none found
     */
    public static Material get(short id) {
        if (id < 0 || id >= materialLookup.length) {
            return null;
        }
        return materialLookup[id].get()[0];
    }

    /**
     * Gets the material from the given id and data
     *
     * @param id to get
     * @param data to get
     * @return material or null if none found
     */
    public static Material get(short id, short data) {
        if (id < 0 || id >= materialLookup.length) {
            return null;
        }
        Material[] parent = materialLookup[id].get();
        if (parent[0] == null) {
            return null;
        }

        data &= parent[0].getDataMask();
        return materialLookup[id].get()[data];
    }

    /**
     * Gets the material for the given BlockFullState
     *
     * @param state the full state of the block
     * @return Material of the BlockFullState
     */
    public static Material get(BlockFullState state) {
        return get(state.getPacked());
    }

    /**
     * Gets the material for the given packed full state
     *
     * @param packedState the packed state of the block
     * @return Material of the id
     */
    public static BlockMaterial get(int packedState) {
        short id = BlockFullState.getId(packedState);
        if (id < 0 || id >= materialLookup.length) {
            return null;
        }
        Material[] material = materialLookup[id].get();
        if (material[0] == null) {
            return null;
        }
        return (BlockMaterial) material[BlockFullState.getData(packedState) & (material[0].getDataMask())];
    }

    /**
     * Returns all current materials in the game
     *
     * @return an array of all materials
     */
    public static Material[] values() {
        //TODO: This is wrong, need to count # of registered materials
        HashSet<Material> set = new HashSet<>(1000);
        for (AtomicReference<Material[]> aMaterialLookup : materialLookup) {
            if (aMaterialLookup.get() != null) {
                set.add(aMaterialLookup.get()[0]);
            }
        }
        return set.toArray(new Material[0]);
    }

    /**
     * Gets the associated material with its name. Case-insensitive.
     *
     * @param name to lookup
     * @return material, or null if none found
     */
    public static Material get(String name) {
        return nameLookup.get(formatName(name));
    }

    /**
     * Returns a human legible material name from the full material.
     *
     * This will strip any '_' and replace with spaces, strip out extra whitespace, and lowercase the material name.
     *
     * @return human legible name of the material.
     */
    private static String formatName(String matName) {
        return matName.trim().replaceAll(" ", "_").toLowerCase();
    }

    /**
     * Gets the minimum data mask required to account for all sub-materials of the material
     *
     * @param m the material
     * @return the minimum data mask
     */
    public static short getMinimumDatamask(Material m) {
        Material root = m;
        while (root.isSubMaterial()) {
            root = m.getParentMaterial();
        }

        if (root.getData() != 0) {
            throw new IllegalStateException("Root materials must have data set to zero");
        }
        Material[] subMaterials = root.getSubMaterials();

        short minimumMask = 0;

        for (Material sm : subMaterials) {
            minimumMask |= sm.getData() & 0xFFFF;
        }

        if (m.hasLSBDataMask()) {
            minimumMask = (short) (MathUtil.roundUpPow2(minimumMask + 1) - 1);
        }

        return minimumMask;
    }
}