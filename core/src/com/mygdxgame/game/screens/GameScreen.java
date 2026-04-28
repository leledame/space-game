package com.mygdxgame.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdxgame.game.ContactManager;
import com.mygdxgame.game.GameResources;
import com.mygdxgame.game.GameSession;
import com.mygdxgame.game.GameSettings;
import com.mygdxgame.game.MyGdxGame;
import com.mygdxgame.game.managers.MemoryManager;
import com.mygdxgame.game.objects.BulletObject;
import com.mygdxgame.game.objects.ShipObject;
import com.mygdxgame.game.objects.TrashObject;
import com.mygdxgame.game.views.ButtonView;
import com.mygdxgame.game.views.ImageView;
import com.mygdxgame.game.views.LiveView;
import com.mygdxgame.game.views.MovingBackgroundView;
import com.mygdxgame.game.views.TextView;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;
    GameSession gameSession;
    ShipObject shipObject;

    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;

    ContactManager contactManager;

    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    TextView recordTextView;
    TextView comboTextView;
    ButtonView pauseButton;
    Texture pauseTexture;
    Texture playTexture;

    int comboCount = 0;
    long lastDestroyTime = 0;
    boolean isGameOver = false;
    boolean isPaused = false;
    boolean wasPauseButtonPressed = false;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        gameSession = new GameSession();
        contactManager = new ContactManager(myGdxGame.world);
        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        shipObject = new ShipObject(
                GameSettings.SCREEN_WIDTH / 2, 150,
                GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
                GameResources.SHIP_IMG_PATH,
                myGdxGame.world
        );
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1215);

        ArrayList<Integer> records = MemoryManager.loadRecordsTable();
        int record = (records != null && !records.isEmpty()) ? records.get(0) : 0;
        recordTextView = new TextView(myGdxGame.commonWhiteFont, 50, 1250);

        comboTextView = new TextView(myGdxGame.comboFont, 285, 1024);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        pauseTexture = new Texture(GameResources.PAUSE_IMG_PATH);
        playTexture = new Texture(GameResources.PLAY_IMG_PATH);
    }

    @Override
    public void show() {
        gameSession.startGame();
        shipObject.reset();
        comboCount = 0;
        lastDestroyTime = 0;
        isGameOver = false;
        isPaused = false;
        wasPauseButtonPressed = false;
        pauseButton.texture = pauseTexture;
    }

    @Override
    public void render(float delta) {
        handlePauseButton();

        if (isPaused) {
            draw();
            return;
        }

        myGdxGame.stepWorld();
        handleInput();

        backgroundView.move();
        liveView.setLeftLives(shipObject.getLiveLeft());

        int score = gameSession.getScore();
        scoreTextView.setText("Score: " + score);

        ArrayList<Integer> records = MemoryManager.loadRecordsTable();
        int record = (records != null && !records.isEmpty()) ? records.get(0) : 0;
        int currentRecord = Math.max(record, score);
        recordTextView.setText("Record: " + currentRecord);
        if (currentRecord > record) {
            MemoryManager.saveRecord(currentRecord);
        }

        if (comboCount > 1) {
            comboTextView.setText("COMBO x" + comboCount + "!");
        } else {
            comboTextView.setText("");
        }

        if (gameSession.shouldSpawnTrash()) {
            TrashObject trashObject = new TrashObject(
                    GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                    GameResources.TRASH_IMG_PATH,
                    myGdxGame.world
            );
            trashArray.add(trashObject);
        }

        if (shipObject.needToShoot()) {
            myGdxGame.audioManager.playShootSound();
            BulletObject laserBullet = new BulletObject(
                    shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                    GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                    GameResources.BULLET_IMG_PATH,
                    myGdxGame.world
            );
            bulletArray.add(laserBullet);
        }

        if (!shipObject.isAlive() && !isGameOver) {
            isGameOver = true;
            myGdxGame.audioManager.playShipDestroySound();
            gameSession.endGame();
            myGdxGame.setScreen(myGdxGame.menuScreen);
            return;
        }

        updateTrash();
        updateBullets();

        draw();
    }

    private void handlePauseButton() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            float touchX = myGdxGame.touch.x;
            float touchY = myGdxGame.touch.y;

            boolean isPressOnButton = touchX >= pauseButton.x && touchX <= pauseButton.x + pauseButton.width
                    && touchY >= pauseButton.y && touchY <= pauseButton.y + pauseButton.height;

            if (isPressOnButton && !wasPauseButtonPressed) {
                isPaused = !isPaused;
                wasPauseButtonPressed = true;
                pauseButton.texture = isPaused ? playTexture : pauseTexture;
            }
        } else {
            wasPauseButtonPressed = false;
        }
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
        backgroundView.draw(myGdxGame.batch);
        for (TrashObject trash : trashArray) trash.draw(myGdxGame.batch);
        shipObject.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        scoreTextView.draw(myGdxGame.batch);
        recordTextView.draw(myGdxGame.batch);
        comboTextView.draw(myGdxGame.batch);
        liveView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        myGdxGame.batch.end();
    }

    private void updateTrash() {
        long currentTime = TimeUtils.millis();
        for (int i = 0; i < trashArray.size(); i++) {
            if (!trashArray.get(i).isInFrame()) {
                myGdxGame.world.destroyBody(trashArray.get(i).body);
                trashArray.remove(i--);
            } else if (!trashArray.get(i).isAlive()) {
                if (currentTime - lastDestroyTime < 2000) {
                    comboCount++;
                } else {
                    comboCount = 1;
                }
                lastDestroyTime = currentTime;
                gameSession.destructionRegistration(comboCount);
                myGdxGame.audioManager.playExplosionSound();
                myGdxGame.world.destroyBody(trashArray.get(i).body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }
}