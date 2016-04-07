/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api;

import java.util.Collection;
import org.mapcraft.api.geo.ServerWorldManager;
import org.mapcraft.api.player.Player;

/**
 * Represents the server part of an engine.
 */
public interface Server extends EnginePart {
    /**
     * Gets all players currently online
     *
     * @return array of all active players
     */
    Collection<Player> getOnlinePlayers();

    /**
     * Gets the maximum number of players this game can host, or -1 if infinite
     *
     * @return max players
     */
    int getMaxPlayers();

    /**
     * Broadcasts the given message to all players
     *
     * The implementation of broadcast is identical to iterating over {@link #getOnlinePlayers()} and invoking {@link Player#sendMessage(String)} for each player.
     *
     * @param message to send
     */
    void broadcastMessage(String message);

    /**
     * Broadcasts the given message to all players
     *
     * The implementation of broadcast is identical to calling a {@link com.flowpowered.api.event.server.permissions.PermissionGetAllWithNodeEvent} event, iterating over each element in getReceivers, invoking
     * {@link com.flowpowered.api.command.CommandSource#sendMessage(String)} for each CommandSource.
     *
     * @param permission the permission needed to receive the broadcast
     * @param message to send
     */
    void broadcastMessage(String permission, String message);

    /**
     * Gets the {@link Player} by the given username. <br/> <br/> If searching for the exact name, this method will iterate and check for exact matches. <br/> <br/> Otherwise, this method will iterate
     * over over all players and find the closest match to the given name, by comparing the length of other player names that start with the given parameter. <br/> <br/> This method is case-insensitive.
     *
     * @param name to look up
     * @param exact Whether to use exact lookup
     * @return Player if found, else null
     */
    Player getPlayer(String name, boolean exact);

    ServerWorldManager getWorldManager();
}
