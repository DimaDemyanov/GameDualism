package com.spbstu.android.game;

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
    public Rope(Body bodyA){
        spriteRope = new Sprite(new Texture("rope.png"));
        def = new BodyDef();
        rDef = new RopeJointDef();
        def.type = BodyDef.BodyType.StaticBody;
        rDef.bodyA  = bodyA;

    }

    public void buildJoint(World world, float bodyY){
        H = yRopedBlock - bodyY;
        def.position.set((xRopedBlock - 2*PPM)/PPM , yRopedBlock/PPM );
        rDef.bodyB = world.createBody(def);
        rDef.maxLength = 0.6f*H/PPM;
        joint = world.createJoint(rDef);
        isExist = true;

    }

    public void destroyJoint(World world) {
        world.destroyJoint(joint);
        isExist = false;
    }
    public void render(SpriteBatch batch, Body body) { //рисую веревку, рисую, где хочу, законом не запрещено
        if(isRoped) {
        batch.begin();
            H = yRopedBlock - body.getPosition().y*PPM;
            L = (float)norm(body.getPosition().x *PPM, xRopedBlock,body.getPosition().y*PPM, yRopedBlock );
            alpha = (float)(Math.asin(H / L)/2/Math.PI*360);
            spriteRope.setOrigin(0,0);// задаю поворот относительно левого нижнего угла
            spriteRope.setBounds(body.getPosition().x * PPM, body.getPosition().y*PPM, 2,L);
            if(body.getPosition().x*PPM > xRopedBlock )
                alpha = -1*alpha +90;
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
