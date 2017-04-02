package com.spbstu.android.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;

import static com.spbstu.android.game.MapParser.PPM;

/**
 * Created by Администратор on 24.03.2017.
 */

public class Rope {
    private Joint joint = null;
    private RopeJointDef rDef;
    private BodyDef def;
    public boolean isExist;
    public boolean inFlight = false;
    public boolean isRoped = false;
    private Sprite spriteRope;
    public float yRopedBlock, xRopedBlock;
    private float H, L, alpha;

    public Rope(Body bodyA) {
        spriteRope = new Sprite(new Texture("rope.png"));
        def = new BodyDef();
        rDef = new RopeJointDef();
        def.type = BodyDef.BodyType.StaticBody;
        rDef.bodyA = bodyA;

    }

    public void buildJoint(World world, float x, float y, Body playerBody, boolean blocksMap[][]) {
        float possibleX ,possibleY;
        if ((y > playerBody.getPosition().y * PPM + 1.5 * PPM) && (Math.abs(x - playerBody.getPosition().x * PPM) < 7 * PPM)) {
            possibleX = playerBody.getPosition().x*PPM;
            possibleY = playerBody.getPosition().y*PPM;
            L = (float) norm(playerBody.getPosition().x * PPM, x, playerBody.getPosition().y * PPM, y);
            H = y - playerBody.getPosition().y * PPM;
            alpha = (float) (Math.asin(H / L));
            if (playerBody.getPosition().x * PPM > x)
                alpha = (float) (Math.PI - alpha);
            for (int i = 0; i < 2*L / PPM; i++) {
                possibleX += PPM/2 * Math.cos(alpha);
                possibleY += PPM/2 * Math.sin(alpha);
                if(possibleY > (blocksMap.length-1) *PPM)
                    possibleY =  (blocksMap.length-1) *PPM;
                if(possibleX > (blocksMap[0].length-1) *PPM)
                    xRopedBlock =  (blocksMap[0].length-1) *PPM;
                if(possibleX < 0)
                    xRopedBlock =  0;
                if (blocksMap[(int) Math.floor((possibleY) / PPM)][(int) Math.floor((possibleX) / PPM) ]) {
                    xRopedBlock = (float) (Math.floor(possibleX / PPM ) * PPM + 0.5 * PPM);
                    yRopedBlock = (float) (Math.floor(possibleY / PPM  ) * PPM + 0.5 * PPM);
                    def.position.set((xRopedBlock - 2 * PPM) / PPM, yRopedBlock / PPM);
                    rDef.bodyB = world.createBody(def);
                    rDef.maxLength = 0.8f * L / PPM;
                    if(isRoped)
                        world.destroyJoint(joint);
                    isRoped = true;
                    joint = world.createJoint(rDef);
                    isExist = true;
                    break;
                }
            }
        }
    }
    public void destroyJoint(World world) {
        world.destroyJoint(joint);
        isExist = false;
    }

    public void render(SpriteBatch batch, Body body) { //рисую веревку, рисую, где хочу, законом не запрещено
        if (isRoped) {
            batch.begin();
            H = yRopedBlock - body.getPosition().y * PPM;
            L = (float) norm(body.getPosition().x * PPM, xRopedBlock, body.getPosition().y * PPM, yRopedBlock);
            alpha = (float) (Math.asin(H / L) / 2 / Math.PI * 360);
            spriteRope.setOrigin(0, 0);// задаю поворот относительно левого нижнего угла
            spriteRope.setBounds(body.getPosition().x * PPM, body.getPosition().y * PPM, 2, L);
            if (body.getPosition().x * PPM > xRopedBlock)
                alpha = -1 * alpha + 90;
            else
                alpha -= 90;
            spriteRope.setRotation(alpha);
            spriteRope.draw(batch);
            batch.end();
        }
    }

    public static double norm(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}
