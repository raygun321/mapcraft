/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mapcraft.api.gui.render;

/**
 *
 * @author rmalot
 */
public class ImmutableFace {
    private int[] vertexIndex = new int[3];
    private int[] textureIndex = new int[3];
    
    public ImmutableFace(int vertIndex0, int texIndex0, int vertIndex1, int texIndex1, int vertIndex2, int texIndex2 ) {
        vertexIndex[0] = vertIndex0;
        textureIndex[0] = texIndex0;
        vertexIndex[1] = vertIndex1;
        textureIndex[1] = texIndex1;
        vertexIndex[2] = vertIndex2;
        textureIndex[2] = texIndex2;
    }
    
    public int getVertexIndex(int index) {
        return vertexIndex[index];
    }
    
    public int getTextureIndex(int index) {
        return textureIndex[index];
    }
}
