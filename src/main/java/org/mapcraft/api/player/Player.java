/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.player;

import java.util.List;

/**
 *
 * @author rmalot
 */
public interface Player extends CommandSender {

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    @Override
    String getName();

    /**
     * Gets the player's display name.
     *
     * @return the player's display name
     */
    String getDisplayName();

    /**
     * Sets the player's display name.
     *
     * @param name the player's new display name
     */
    void setDisplayName(String name);

    /**
     * Gets if the player is online
     *
     * @return true if online
     */
    boolean isOnline();

    /**
     * Gets if the player has joined before
     *
     * @return true if joined before
     */
    boolean hasJoinedBefore();

    /**
     * Kicks the player without giving a reason, or forcing it.
     */
    void kick();

    /**
     * Kicks the player for the given reason.
     *
     * @param reason the message to send to the player.
     */
    void kick(String reason);

    /**
     * Bans the player without giving a reason.
     */
    void ban();

    /**
     * Bans the player for the given reason.
     *
     * @param kick whether to kick or not
     */
    void ban(boolean kick);

    /**
     * Bans the player for the given reason.
     *
     * @param kick whether to kick or not
     * @param reason for ban
     */
    void ban(boolean kick, String reason);

    /**
     * Immediately saves the players state to disk
     *
     * @return true if successful
     */
    boolean save();

    /**
     * If an entity is set as invisible, it will not be sent to the client.
     */
    void setVisible(Entity entity, boolean visible);

    /**
     * Retrieves a list of all invisible {@link Entity}'s to the player
     *
     * @return {@link List<{@link Entity}>} of invisible {@link Entity}'s
     */
    List<Entity> getInvisibleEntities();

    /**
     * Returns true if the {@link Entity} provided is invisible this this {@link Player}
     *
     * @param entity Entity to check if invisible to the {@link Player}
     * @return true if the {@link Entity} is invisible
     */
    boolean isInvisible(Entity entity);

    PlayerSnapshot snapshot();

    /**
     * Sends a command to be processed on the opposite Platform. This is basically a shortcut method to prevent the need to register a command locally with a {@link Command.NetworkSendType} of {@code
     * SEND}.
     *
     * @param command to send
     * @param args to send
     */
    void sendCommand(String command, String... args);

    PlayerNetwork getNetwork();

    TransformProvider getTransformProvider();

    void setTransformProvider(TransformProvider provider);

    public List<InputSnapshot> getInput();
}