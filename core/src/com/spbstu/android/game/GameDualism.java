package com.spbstu.android.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.spbstu.android.game.Screens.MenuScreen;

public class GameDualism extends Game {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final java.lang.String TITLE = "Dualism";

    public AssetManager assetManager = new AssetManager();


    @Override
    public void create() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
