package com.spbstu.android.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.spbstu.android.game.GameDualism;
import com.spbstu.android.game.MapParser;
import com.spbstu.android.game.Player;
import com.spbstu.android.game.Rope;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import static com.spbstu.android.game.MapParser.PPM;


public class Level1Screen extends ScreenAdapter {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private final GameDualism game;
    private final Stage stage = new Stage();
    private Button rightButton;
    private Button leftButton;
    private Button upButton;
    private Button pauseButton;
    private Button playButton;
    private Button menuButton;
    private Button changeBroButton;
    private Boolean isItPause = false;
    private OrthographicCamera camera;
    private final int height = Gdx.graphics.getHeight();
    private final int width = Gdx.graphics.getWidth();
    private int maxButtonsSize = height / 6; // не размер, а коэффициент!
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Player player;
    private boolean trapsMap[][];//массив ловушек
    private boolean blocksMap[][];// массив блоков

    private int kolWidthBlocks;
    private int kolHeightBlocks;
    private float g = 20f; //   гравитация
    private Rope rope;

    public Level1Screen(GameDualism game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 4.5f, Gdx.graphics.getHeight() / 4.5f);
        map = new TmxMapLoader().load("Maps/Level-1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        box2DDebugRenderer = new Box2DDebugRenderer();
        game.assetManager.load("Textures/character.png", Texture.class);
        game.assetManager.finishLoading();
        batch = new SpriteBatch();
        world = new World(new Vector2(0, -g), false);
        player = new Player(16f / (2 * PPM),
                16f / (2 * PPM) + 16 / PPM * 3,
                (16 / PPM - 0.1f) / 2, world, game.assetManager);

        rope = new Rope(player.body);
        MapParser.parseMapObjects(map.getLayers().get("Line").getObjects(), world);
        actionButtons();
        kolWidthBlocks = map.getProperties().get("width", Integer.class);
        kolHeightBlocks = map.getProperties().get("height", Integer.class);
        trapsMap = new boolean[kolHeightBlocks][kolWidthBlocks];
        blocksMap = new boolean[kolHeightBlocks][kolWidthBlocks];
        initTrapsMap();
        initBlocks();
        listeners();

    }


    public void maxButtonsSizeDeterminate() {// у новых крутых мобильников очень большие разрешения,( 3840x2160 и больше), разрешение картинки кнопок конечно, эта функция учитывает это
        if (maxButtonsSize > leftButton.getWidth())
            maxButtonsSize = (int) leftButton.getWidth();
    }

    public void actionButtons() {

        rightButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/rightButton.png"))));
        leftButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/leftButton.png"))));
        upButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/upButton.png"))));
        pauseButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/pause.png"))));
        playButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/playButton.png"))));
        menuButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/menu.png"))));
        changeBroButton = new ImageButton(new TextureRegionDrawable(
                new TextureRegion(new Texture("Buttons/changebrobutton.png"))));
        maxButtonsSizeDeterminate();
        stage.addActor(rightButton);


        rightButton.setBounds(width / 10 + maxButtonsSize / 2, maxButtonsSize / 4, maxButtonsSize, maxButtonsSize);

        stage.addActor(leftButton);
        leftButton.setBounds(width / 10 - maxButtonsSize * 3 / 4, maxButtonsSize / 4, maxButtonsSize, maxButtonsSize);
        stage.addActor(upButton);
        upButton.setBounds(width - maxButtonsSize * 3 / 2, maxButtonsSize / 4, maxButtonsSize, maxButtonsSize);

        upButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (rope.isExist == true){
                    rope.isRoped = false;
                    rope.inFlight = true;
                    rope.destroyJoint(world);
                }
                player.jump();
                return true;
            }
        });

        stage.addActor(playButton);
        playButton.setBounds((width - maxButtonsSize * 3 / 4) / 2, (height - maxButtonsSize * 3 / 4) * 3 / 4, maxButtonsSize * 3 / 4, maxButtonsSize * 3 / 4);
        playButton.setVisible(false);

        stage.addActor(pauseButton);
        pauseButton.setBounds(width - maxButtonsSize, height - maxButtonsSize, maxButtonsSize * 3 / 4, maxButtonsSize * 3 / 4);
        pauseButton.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseMode();
                pause();
                game.setScreen(new PlayPauseScreen(game, Level1Screen.this));
            }
        });

        stage.addActor(menuButton);
        menuButton.setBounds(width - maxButtonsSize, height - maxButtonsSize, maxButtonsSize * 3 / 4, maxButtonsSize * 3 / 4);
        menuButton.setVisible(false);
        menuButton.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        stage.addActor(changeBroButton);
        changeBroButton.setBounds(width - maxButtonsSize * 3 / 2, 1.5f * maxButtonsSize, maxButtonsSize, maxButtonsSize);
    }

    @Override
    public void pause() {
        rightButton.setVisible(false);
        leftButton.setVisible(false);
        upButton.setVisible(false);
        pauseButton.setVisible(false);
        menuButton.setVisible(true);
        playButton.setVisible(true);
        changeBroButton.setVisible(false);
        isItPause = true;
    }

    @Override
    public void resume() {
        rightButton.setVisible(true);
        leftButton.setVisible(true);
        upButton.setVisible(true);
        pauseButton.setVisible(true);
        playButton.setVisible(false);
        menuButton.setVisible(false);
        changeBroButton.setVisible(true);
        isItPause = false;
    }

    public void pauseMode() {
        playButton.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
            }
        });
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    public void listeners() {
        stage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {// создаю слушатаеля касания к экрану
                if ((!rope.isRoped) && (y / height * camera.viewportHeight + camera.position.y - camera.viewportHeight / 2 > player.body.getPosition().y * PPM + 1.5 * PPM)
                        && (Math.abs(x / width * camera.viewportWidth + camera.position.x - camera.viewportWidth / 2 - player.body.getPosition().x * PPM) < 7 * PPM)
                        && (blocksMap[(int) Math.ceil((y / height * camera.viewportHeight + camera.position.y - camera.viewportHeight / 2) / PPM) - 1]
                        [(int) Math.ceil((x / width * camera.viewportWidth + camera.position.x - camera.viewportWidth / 2) / PPM) - 1])) {
                    rope.xRopedBlock = x / width * camera.viewportWidth + camera.position.x - camera.viewportWidth / 2;
                    rope.yRopedBlock = y / height * camera.viewportHeight + camera.position.y - camera.viewportHeight / 2;
                    rope.isRoped = true;
                    rope.buildJoint(world, player.body.getPosition().y * PPM);
                }
                return true;
            }
        });

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 4.5f, height / 4.5f);
        moveCamera();
        camera.update();
    }

    @Override
    public void render(float delta) {
        if (!isItPause) {
            inputUpdate(delta);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glClearColor(2f / 256f, 23f / 256f, 33f / 256f, 1f);
            cameraUpdate();
            batch.setProjectionMatrix(camera.combined);
            renderer.setView(camera);
            renderer.render();
            stage.act(delta);
            world.step(delta, 6, 2);
            stage.draw();
            rope.render(batch, player.body);
            player.render(batch);
            //box2DDebugRenderer.render(world, camera.combined.scl(PPM));//надо только в дебаге
            //box2DDebugRenderer.setDrawJoints(true);
            handleTrapsCollision(player.getTileX(), player.getTileY());


        } else {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            renderer.render();
            stage.act(delta);
            stage.draw();
            player.render(batch);
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        box2DDebugRenderer.dispose();
        batch.dispose();
        stage.dispose();
    }

    private void cameraUpdate() {
        moveCamera();
        camera.update();
    }

    public void inputUpdate(float delta) {
        if (player.jumpTimer > 0) {
            player.jumpTimer--;
        }

        if (player.isGrounded(world) && player.jumpTimer == 0) {
            player.jumpNumber = 1;
            rope.inFlight = false;
        }

        if (!(rightButton.isPressed()) && !(leftButton.isPressed()) &&  (!rope.isRoped) && (!rope.inFlight)) {
            player.stop();
        }

        if (rightButton.isPressed()) {
            player.moveRight();
        }

        if (leftButton.isPressed()) {
            player.moveLeft();
        }


    }

    private void moveCamera() {
        camera.position.set(player.body.getPosition().x * PPM, player.body.getPosition().y * PPM, camera.position.z);

        if (player.body.getPosition().x - Gdx.graphics.getWidth() / (9f * PPM) < 0)
            camera.position.set(Gdx.graphics.getWidth() / 9f, camera.position.y, camera.position.z);

        if (player.body.getPosition().x + Gdx.graphics.getWidth() / (9f * PPM) > kolWidthBlocks * 16 / PPM)
            camera.position.set(kolWidthBlocks * 16f - Gdx.graphics.getWidth() / 9f, camera.position.y, camera.position.z);

        if (player.body.getPosition().y - Gdx.graphics.getHeight() / (9f * PPM) < 0)
            camera.position.set(camera.position.x, Gdx.graphics.getHeight() / 9f, camera.position.z);

        if (player.body.getPosition().y + Gdx.graphics.getHeight() / (9f * PPM) > kolHeightBlocks * 16f / PPM)
            camera.position.set(camera.position.x, kolHeightBlocks * 16f - Gdx.graphics.getHeight() / 9f, camera.position.z);
    }

    private void restart() {
        player.body.setLinearVelocity(0f, 0f);
        player.jumpNumber = 1;
        player.jumpTimer = 0;
        player.body.setTransform(16f / (2 * PPM), 16f / (2 * PPM) + 16 / PPM * 3, player.body.getAngle());
    }

    private void handleTrapsCollision(int playerX, int playerY) {
        if (trapsMap[playerY][playerX] == true) {
            // restart();
        }
    }

    private void initBlocks() {
        final TiledMapTileLayer blocks;
        blocks = (TiledMapTileLayer) map.getLayers().get(" Main obstacles");
        for (int i = 0; i < kolHeightBlocks; i++)
            for (int j = 0; j < kolWidthBlocks; j++)
                blocksMap[i][j] = (blocks.getCell(j, i) != null);


    }

    private void initTrapsMap() {
        TiledMapTileLayer traps[] = new TiledMapTileLayer[3];

        traps[0] = (TiledMapTileLayer) map.getLayers().get("Background-Water&amp;Lava");
        traps[1] = (TiledMapTileLayer) map.getLayers().get("Traps-second-bro");
        traps[2] = (TiledMapTileLayer) map.getLayers().get("Traps-first-bro");

        for (int i = 0; i < kolHeightBlocks; i++) {
            for (int j = 0; j < kolWidthBlocks; j++) {
                trapsMap[i][j] = (traps[0].getCell(j, i) != null || traps[1].getCell(j, i) != null || traps[2].getCell(j, i) != null);
            }
        }
    }
}

