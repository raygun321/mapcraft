/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.material;

/**
 * An interface defining a {@link Material} that can be placed
 */
public interface Placeable {
	/**
	 * Called when this block is about to be placed (before {@link #onPlacement(Block, short, BlockFace, boolean)}), checking if placement is allowed or not.
	 *
	 * @param block to place
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @param isClickedBlock whether the block is to be placed at the clicked block
	 * @param cause the cause of the placement
	 * @return true if placement is allowed
	 */
	public boolean canPlace(Block block, short data, BlockFace against, Vector3f clickedPos, boolean isClickedBlock, Cause<?> cause);

	/**
	 * Called when this block is placed, handling the actual placement<br> This method should only change properties that rely on the face it is placed against, or in what way it is placed. All other
	 * logic should be performed in onCreate.
	 *
	 * @param block to affect
	 * @param data block data to use during placement
	 * @param against face against the block is placed
	 * @param clickedPos relative position the block was clicked to place this block
	 * @param isClickedBlock whether the block is being placed at the clicked block
	 * @param cause the cause of the placement
	 */
	public void onPlacement(Block block, short data, BlockFace against, Vector3f clickedPos, boolean isClickedBlock, Cause<?> cause);
}