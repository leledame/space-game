package com.mygdxgame.game;

// Импорт модулей из фреймворка

import static com.mygdxgame.game.screens.GameScreen.POSITION_ITERATIONS;
import static com.mygdxgame.game.screens.GameScreen.STEP_TIME;
import static com.mygdxgame.game.screens.GameScreen.VELOCITY_ITERATIONS;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdxgame.game.screens.GameScreen;

public class MyGdxGame extends Game {

    public SpriteBatch batch;
    public World world;
    public OrthographicCamera camera;

    public GameScreen gameScreen;
    float accumulator = 0;

    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        gameScreen = new GameScreen(this);

        setScreen(gameScreen);
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += delta;

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}