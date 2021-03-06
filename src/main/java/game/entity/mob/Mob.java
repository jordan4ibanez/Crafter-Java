package game.entity.mob;

import engine.time.Delta;
import game.chunk.Chunk;
import game.entity.Entity;
import game.entity.EntityContainer;
import game.entity.collision.Collision;
import game.player.Player;
import game.ray.Ray;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Random;

import static org.joml.Math.toRadians;

public abstract class Mob extends Entity {

    private final Random random = new Random();
    private final int[] dirArray = new int[]{-1,1};

    private final String name;
    private float hurtTimer = 0f;
    private float deathRotation = 0f;
    private float deathTimer = 0f;

    private float animationTimer = 0f;
    private float smoothRotation = 0f;

    private boolean standing = false;
    private boolean onGround = false;

    private int health;
    private int hurtAdder = 0;

    private float rotation;

    private final Vector3f[] bodyOffsets = new Vector3f[]{};

    private final Vector3f[] bodyRotations = new Vector3f[]{};

    private final MobInterface mobInterface = new MobInterface() {
        @Override
        public void onTick(Chunk chunk, Collision collision, Mob thisMob, Delta delta) {
            MobInterface.super.onTick(chunk, collision, thisMob, delta);
        }

        @Override
        public void onSpawn(Mob mob, Delta delta) {
            MobInterface.super.onSpawn(mob, delta);
        }

        @Override
        public void onRightClick(Mob thisMob, Delta delta) {
            MobInterface.super.onRightClick(thisMob, delta);
        }

        @Override
        public void onDeath(Mob thisMob, Delta delta) {
            MobInterface.super.onDeath(thisMob, delta);
        }

        @Override
        public void onPunch(Mob thisMob, Delta delta) {
            MobInterface.super.onPunch(thisMob, delta);
        }
    };

    public Mob(EntityContainer entityContainer, String name, Vector3d pos, Vector3f inertia, float width, float height, int health) {
        super(entityContainer, pos, inertia, width, height, false, true, false);
        this.name = name;
        this.health = health;

        rotation = (float) (Math.toDegrees(Math.PI * Math.random() * randomDir()));
    }

    public float getAnimationTimer(){
        return animationTimer;
    }

    public void setAnimationTimer(float animationTimer){
        this.animationTimer = animationTimer;
    }

    public float getRotation(){
        return rotation;
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public boolean isOnGround(){
        return onGround;
    }

    public void setOnGround(boolean onGround){
        this.onGround = onGround;
    }

    public MobInterface getMobInterface(){
        return mobInterface;
    }

    public Vector3f[] getBodyOffsets(){
        return bodyOffsets;
    }

    public Vector3f[] getBodyRotations(){
        return bodyRotations;
    }

    public float getSmoothRotation(){
        return smoothRotation;
    }

    public void setSmoothRotation(float smoothRotation){
        this.smoothRotation = smoothRotation;
    }

    public String getName(){
        return name;
    }

    public int getHurtAdder(){
        return hurtAdder;
    }

    public int getHealth(){
        return health;
    }

    public boolean getIfStanding(){
        return standing;
    }

    public void setStanding(boolean standing){
        this.standing = standing;
    }

    public void onTick(Chunk chunk, Delta delta, Player player, Ray ray){
        super.onTick(chunk, delta);
        double dtime = delta.getDelta();

        /*
        if (getMobHealth(thisMob) > 0) {
            mobSoftPlayerCollisionDetect(thisMob, thisMobPos, thisMobHeight, thisMobWidth);
            mobSoftCollisionDetect(thisMob, thisMobPos, thisMobHeight, thisMobWidth);
        }
         */

        //fallen out of world
        if (this.getPos().y < 0){
            this.delete();
            return;
        }

        //mob is now dead
        if (health <= 0){
            //mob dying animation
            if (deathRotation < 90) {
                //System.out.println(thisMobDeathRotation);
                deathRotation += dtime * 300f;
                if (deathRotation >= 90) {
                    deathRotation = 90;
                }
            //mob will now sit there for a second
            } else {
                deathTimer += dtime;
            }
        }

        if (health <= 0 && deathTimer >= 0.5f){
            this.delete();
            return;
        }

        //count down hurt timer
        if(hurtTimer > 0f && health > 0){
            hurtTimer -= dtime;
            if (hurtTimer <= 0){
                hurtTimer = 0;

                hurtAdder = 0;
            }
        }

        mobSmoothRotation(delta);
        doHeadCode(player, ray);
    }


    public void hurt(int damage){
        this.health -= damage;
        this.hurtAdder = 15;
        this.hurtTimer = 0.5f;
    }

    //mob utility code
    private final Vector3d headPos = new Vector3d();
    private final Vector3d headTurn = new Vector3d();
    private final Vector3d adjustedHeadPos = new Vector3d();

    public void doHeadCode(Player player, Ray ray){

        //this is a pointer object
        Vector3d thisMobPos = getPos();

        //yet another pointer object
        Vector3f[] thisMobBodyOffsets = getBodyOffsets();

        //look another pointer object
        Vector3f[] thisMobBodyRotations = getBodyRotations();

        float thisMobSmoothRotation = getSmoothRotation();

        float smoothToRad = toRadians(thisMobSmoothRotation + 90f);

        //silly head turning
        headPos.set(thisMobPos.x, thisMobPos.y, thisMobPos.z);
        adjustedHeadPos.set(Math.cos(-smoothToRad), 0,Math.sin(smoothToRad));
        adjustedHeadPos.mul(thisMobBodyOffsets[0].z).add(0,thisMobBodyOffsets[0].y,0);
        headPos.add(adjustedHeadPos);

        //check if the mob can actual "see" the player
        if (!ray.lineOfSight(headPos, player.getPlayerPosWithEyeHeight())){
            return;
        }

        //this is debug code for creating a new mob
        //createParticle(headPos.x, headPos.y, headPos.z, 0.f,0.f,0.f, (byte) 7); //debug

        headTurn.set(player.getPlayerPosWithEyeHeight()).sub(headPos);
        //headTurn.normalize();

        float headYaw = (float) Math.toDegrees(Math.atan2(headTurn.z, headTurn.x)) + 90 - thisMobSmoothRotation;
        float pitch = (float)Math.toDegrees(Math.atan2(Math.sqrt(headTurn.z * headTurn.z + headTurn.x * headTurn.x), headTurn.y) + (Math.PI * 1.5));

        //correction of degrees overflow (-piToDegrees to piToDegrees) so it is workable
        if (headYaw < -180) {
            headYaw += 360;
        } else if (headYaw > 180){
            headYaw -= 360;
        }

        //a temporary reset, looks creepy
        if (headYaw > 90 || headYaw < -90){
            headYaw = 0;
            pitch = 0;
        }

        //weird OOP application
        thisMobBodyRotations[0].set(pitch,headYaw,0);
    }


    //todo: shortest distance
    public void mobSmoothRotation(Delta delta){
        double dtime = delta.getDelta();

        float thisMobRotation = getRotation();
        float thisMobSmoothRotation = getRotation();

        float diff = thisMobRotation - thisMobSmoothRotation;

        //correction of degrees overflow (-piToDegrees to piToDegrees) so it is workable
        if (diff < -180) {
            diff += 360;
        } else if (diff > 180){
            diff -= 360;
        }

        /*
        this is basically brute force inversion to correct the yaw
        addition and make the mob move to the shortest rotation
        vector possible
         */

        if (Math.abs(diff) < dtime * 500f){
            thisMobSmoothRotation = thisMobRotation;
        } else {
            if (Math.abs(diff) > 180) {
                if (diff < 0) {
                    thisMobSmoothRotation += dtime * 500f;
                } else if (diff > 0) {
                    thisMobSmoothRotation -= dtime * 500f;
                }

                //correction of degrees overflow (-piToDegrees to piToDegrees) so it is workable
                if (thisMobSmoothRotation < -180) {
                    thisMobSmoothRotation += 360;
                } else if (thisMobSmoothRotation > 180) {
                    thisMobSmoothRotation -= 360;
                }

            } else {
                if (diff < 0) {
                    thisMobSmoothRotation -= dtime * 500f;
                } else if (diff > 0) {
                    thisMobSmoothRotation += dtime * 500f;
                }
            }
        }

        setSmoothRotation(thisMobSmoothRotation);
    }

    public float randomDir(){
        return dirArray[random.nextInt(2)];
    }
}
