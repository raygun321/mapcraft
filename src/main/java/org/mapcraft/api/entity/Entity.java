/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.entity;

import java.util.UUID;
import mapcraft.block.Chunk;
import org.mapcraft.api.aspect.AspectOwner;
import org.mapcraft.api.tickable.Tickable;

/**
 * Represents an entity, which may or may not be spawned into the world.
 * 
 * @author rmalot
 */
public interface Entity extends Tickable, AspectOwner {
    /**
     * Gets the current ID of this entity within the current game session
     *
     * @return The entity's id.
     */
    public int getId();

    /**
     * Gets the entity's persistent unique id. <p> Can be used to look up the entity and persists between starts.
     *
     * @return persistent {@link UUID}
     */
    public UUID getUID();

//    /**
//     * Gets the {@link Engine} that spawned and is managing this entity
//     *
//     * @return {@link Engine}
//     */
//    public Engine getEngine();

    /**
     * Removes the entity. This takes effect at the next snapshot.
     */
//    @DelayedWrite
//    @LiveRead
    public void remove();

    /**
     * True if the entity is removed.
     *
     * @return removed
     */
//    @SnapshotRead
    public boolean isRemoved();

    /**
     * Returns true if this entity is spawned.
     *
     * @return spawned
     */
    public boolean isSpawned();

    /**
     * Sets whether or not the entity should be saved.<br/>
     *
     * @param savable True if the entity should be saved, false if not
     */
//    @DelayedWrite
    public void setSavable(boolean savable);

    /**
     * Returns true if this entity should be saved.
     *
     * @return savable
     */
//    @SnapshotRead
    public boolean isSavable();

    /**
     * Gets the {@link Chunk} this entity resides in, or null if removed.
     *
     * @return {@link Chunk} the entity is in, or null if removed.
     */
//    @SnapshotRead
    public Chunk getChunk();

//    /**
//     * Gets the region the entity is associated and managed with, or null if removed.
//     *
//     * @return {@link Region} the entity is in.
//     */
//    @SnapshotRead
//    public Region getRegion();

//    /**
//     * Gets the {@link org.spout.api.component.entity.PhysicsComponent} which is the representation of this Entity within space. <p> It provides the {@link Transform} which is Position, Rotation, Scale
//     * as well as physics to manipulate the entity in respect to the environment. </p>
//     *
//     * @return The physics component.
//     */
//    public PhysicsComponent getPhysics();
//
//    /**
//     * Gets a {@link NetworkComponent} for the entity. The {@link NetworkComponent} is special in that it has methods that get called in preSnapshot and finalizeTick
//     *
//     * @return The network component
//     */
//    public NetworkComponent getNetwork();
//
//    /**
//     * Creates an immutable snapshot of the entity state at the time the method is called
//     *
//     * @return immutable snapshot
//     */
//    public EntitySnapshot snapshot();

}


