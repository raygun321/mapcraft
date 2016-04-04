/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.geo;

/**
 *
 * @author rmalot
 */
public enum LoadOption {
    /**
     * Do not load or generate chunk/region if not currently loaded
     */
    NO_LOAD(false, false),
    /**
     * Load chunk/region if not currently loaded, but do not generate it if it does not yet exist
     */
    LOAD_ONLY(true, false),
    /**
     * Load chunk/region if not currently loaded, and generate it if it does not yet exist
     */
    LOAD_GEN(true, true),
    /**
     * Don't load the chunk if it has already been generated, only generate if it does not yet exist
     */
    GEN_ONLY(false, true);
    private final boolean load;
    private final boolean generate;

    private LoadOption(boolean load, boolean generate) {
            this.load = load;
            this.generate = generate;
    }

    /**
     * Test if chunk/region should be loaded if not currently loaded
     *
     * @return true if yes, false if no
     */
    public final boolean loadIfNeeded() {
            return load;
    }

    /**
     * Test if chunk/region should be generated if it does not exist
     *
     * @return true if yes, false if no
     */
    public final boolean generateIfNeeded() {
            return generate;
    }
}
