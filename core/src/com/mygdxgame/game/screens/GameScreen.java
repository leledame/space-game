package com.mygdxgame.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdxgame.game.GameResources;
import com.mygdxgame.game.GameSettings;
import com.mygdxgame.game.MyGdxGame;
import com.mygdxgame.game.objects.ShipObject;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    ShipObject shipObject;


    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world
        );
    }

    @Override
    public void render(float delta) {

        myGdxGame.stepWorld();
        handleInput();
        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            shipObject.move(myGdxGame.touch);
        }
    }

    private void draw() {

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();
        shipObject.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }
}