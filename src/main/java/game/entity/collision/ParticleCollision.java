package game.entity.collision;

import engine.time.Delta;
import game.blocks.BlockDefinitionContainer;
import game.chunk.Chunk;
import org.joml.Math;
import org.joml.*;


public class ParticleCollision {

    private final BlockDefinitionContainer blockDefinitionContainer = new BlockDefinitionContainer();
    private final CollisionObject collisionObject = new CollisionObject();
    private Delta delta;
    private Chunk chunk;

    public ParticleCollision(){

    }

    public void setDelta(Delta delta){
        if (this.delta == null){
            this.delta = delta;
        }
    }

    public void setChunk(Chunk chunk){
        if (this.chunk == null){
            this.chunk = chunk;
        }
    }


    private double adjustedDelta;

    private final Vector3d oldPos = new Vector3d();
    private final Vector3i fPos = new Vector3i();

    public boolean applyInertia(Vector3d pos, Vector3f inertia, boolean gravity, boolean applyCollision){

        double delta = this.delta.getDelta();

        int loops = 1;

        if (delta >  0.001f){
            loops = (int)Math.floor(delta / 0.001f);
            adjustedDelta = (delta/(double)loops);
        } else {
            adjustedDelta = delta;
        }

        boolean onGround = false;

        for (int i = 0; i < loops; i++) {

            if(gravity) {
                inertia.y -= 30f * adjustedDelta; //gravity
            }

            //limit speed on x axis
            if (inertia.x <= -30f) {
                inertia.x = -30f;
            } else if (inertia.x > 30f) {
                inertia.x = 30f;
            }

            //limit speed on y axis
            if (inertia.y <= -50f) {
                inertia.y = -50f;
            } else if (inertia.y > 30f) {
                inertia.y = 30f;
            }

            //limit speed on z axis
            if (inertia.z <= -30f) {
                inertia.z = -30f;
            } else if (inertia.z > 30f) {
                inertia.z = 30f;
            }

            if (applyCollision) {
                onGround = collisionDetect(pos, inertia);
            } else {
                pos.add(inertia.x * adjustedDelta,inertia.y * adjustedDelta,inertia.z * adjustedDelta);
            }

            //apply friction
            if (onGround) {
                // do (10 - 9.5f) for slippery!
                inertia.sub((float)(inertia.x * adjustedDelta * 10d),0,(float)(inertia.z * adjustedDelta * 10d));
            }
        }
        return onGround;
    }

    //normal collision

    private boolean collisionDetect(Vector3d pos, Vector3f inertia){
        boolean onGround = false;

        oldPos.set(pos);

        pos.y += inertia.y * adjustedDelta;

        floorPos(pos);

        switch (inertiaToDir(inertia.y)) {
            case -1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    onGround = collideYNegative(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
            case 1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    collideYPositive(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
        }


        //todo: begin X collision detection
        pos.x += inertia.x * adjustedDelta;

        floorPos(pos);

        switch (inertiaToDir(inertia.x)) {
            case 1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    collideXPositive(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
            case -1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    collideXNegative(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
        }



        //todo: Begin Z collision detection
        pos.z += inertia.z * adjustedDelta;
        floorPos(pos);

        switch (inertiaToDir(inertia.z)) {
            case 1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    collideZPositive(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
            case -1 -> {
                byte cachedBlock = chunk.getBlock(fPos);
                byte rot = chunk.getBlockRotation(fPos);
                if (cachedBlock > 0 && blockDefinitionContainer.getWalkable(cachedBlock)) {
                    collideZNegative(fPos.x, fPos.y, fPos.z, rot, pos, inertia, cachedBlock);
                }
            }
        }

        return onGround;
    }

   
    private void collideYPositive(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                pos.y = blockBox[1] + blockPosY - 0.00001d;
                inertia.y = 0;
            }
        }
    }
    
    private boolean collideYNegative(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        boolean onGround = false;
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                onGround = true;
                pos.y = blockBox[4] + blockPosY + 0.00001d;
                inertia.y = 0;
            }
        }
        return onGround;
    }

    private void collideXPositive(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                pos.x = blockBox[0] + blockPosX - 0.00001d;
                inertia.x = 0;
            }
        }
    }

    private void collideXNegative(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                pos.x = blockBox[3] + blockPosX + 0.00001d;
                inertia.x = 0;
            }
        }
    }


    private void collideZPositive(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                pos.z = blockBox[2] + blockPosZ - 0.00001d;
                inertia.z = 0;
            }
        }
    }

    private void collideZNegative(int blockPosX, int blockPosY, int blockPosZ, byte rot, Vector3d pos, Vector3f inertia, byte blockID){
        for (float[] blockBox : blockDefinitionContainer.getShape(blockID, rot)) {
            collisionObject.setAABBBlock(blockBox, blockPosX, blockPosY, blockPosZ);
            if (collisionObject.pointIsWithinBlock(pos.x, pos.y, pos.z)) {
                pos.z = blockBox[5] + blockPosZ + 0.00001d;
                inertia.z = 0;
            }
        }
    }


    private int inertiaToDir(float thisInertia){
        if (thisInertia > 0.0001f){
            return 1;
        } else if (thisInertia < -0.0001f){
            return -1;
        }

        return 0;
    }

    //simple type cast bolt on to JOML
    //converts floored Vector3d to Vector3i
    private void floorPos(Vector3d input){
        fPos.set((int) Math.floor(input.x), (int)Math.floor(input.y),(int)Math.floor(input.z));
    }
}