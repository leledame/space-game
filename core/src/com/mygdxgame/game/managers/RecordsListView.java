package com.mygdxgame.game.views;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mygdxgame.game.GameSettings;

import java.util.ArrayList;

public class RecordsListView extends TextView {

    private BitmapFont ownFont;

    public RecordsListView(BitmapFont font, float y) {
        super(font, 0, y, "");
        this.ownFont = font;
    }

    public void setRecords(ArrayList<Integer> recordsList) {
        String newText = "";
        int countOfRows = Math.min(recordsList.size(), 5);
        for (int i = 0; i < countOfRows; i++) {
            System.out.println(recordsList.get(i));
            newText += (i + 1) + ". - " + recordsList.get(i) + "\n";
        }
        setText(newText);
        GlyphLayout glyphLayout = new GlyphLayout(ownFont, newText);
        x = (GameSettings.SCREEN_WIDTH - glyphLayout.width) / 2;
    }

}