package com.mygdxgame.game;

import com.badlogic.gdx.physics.box2d.World;

public class ShipObject extends GameObject {
    public ShipObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, world);
    }
}
