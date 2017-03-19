package com.spbstu.android.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.spbstu.android.game.GameDualism;

/**
 * @author shabalina-av
 */
public class ScreenLevel extends ScreenAdapter {
    private final GameDualism game;

    private final int height = Gdx.graphics.getHeight();
    private final int width = Gdx.graphics.getWidth();
    private int maxButtonsHeight = height / 6;
    private int maxButtonsWidth = width / 6;
    private final Stage stage = new Stage();
    private Button menuButton;

    private int maxButtonsSize = height / 6; // не размер, а коэффициент!


    public ScreenLevel(final GameDualism game,final MenuScreen menu) {

        this.game = game;
        stage.addActor(new Image(new Texture("back2.png")));
        menuButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/menu.png"))));

        stage.addActor(menuButton);
        menuButton.setBounds(width - maxButtonsSize, height - maxButtonsSize, maxButtonsSize * 3 / 4, maxButtonsSize * 3 / 4);
        menuButton.setVisible(true);


        Button buttonLevel1 = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/level1.png"))));
        buttonLevel1.setBounds(5/4*(width - maxButtonsWidth) / 2f, 5/4*(height - maxButtonsHeight) / 2f, maxButtonsWidth * 5/4, maxButtonsHeight*5/4);
        stage.addActor(buttonLevel1);


        menuButton.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(menu);
            }
        }
        );
        buttonLevel1.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("clicked");
                game.setScreen(new Level1Screen(game));
            }
        });

    }
    @Override
    public void show() {
        System.out.println("show");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    @Override
    public void pause() {
        System.out.println("pause");
    }

    @Override
    public void resume() {
        System.out.println("resume");
    }

    @Override
    public void hide() {
        System.out.println("hide");
    }

    @Override
    public void dispose() {
        stage.dispose();
        System.out.println("dispose");
    }
}