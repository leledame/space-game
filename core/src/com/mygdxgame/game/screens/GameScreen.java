package com.mygdxgame.game.screens;

import com.badlogic.gdx.Screen;
import com.mygdxgame.game.GameResources;
import com.mygdxgame.game.GameSettings;
import com.mygdxgame.game.MyGdxGame;

public class GameScreen implements Screen {
    MyGdxGame myGdxGame;
    


    public static final float STEP_TIME = 1f / 60;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 6;



    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

}