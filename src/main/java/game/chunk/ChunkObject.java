package game.chunk;

import engine.disk.PrimitiveChunkObject;
import engine.graphics.Mesh;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class ChunkObject{

    private final Vector2i pos     = new Vector2i();

    private final byte[] block     = new byte[32768];
    private final byte[] rotation  = new byte[32768];
    private final byte[] light     = new byte[32768];
    private final byte[] heightMap = new byte[32768];

    private final Mesh[] normalMesh  = new Mesh[8];
    private final Mesh[] liquidMesh  = new Mesh[8];
    private final Mesh[] allFaceMesh = new Mesh[8];

    private boolean saveToDisk = false;
    private float hover        = -128;

    public ChunkObject(Vector2i pos, byte[] block, byte[] rotation, byte[] light, byte[] heightMap){
        this.pos.set(pos);
        System.arraycopy(block, 0, this.block, 0, block.length);
        System.arraycopy(rotation, 0, this.rotation, 0, rotation.length);
        System.arraycopy(light, 0, this.light, 0, light.length);
        System.arraycopy(heightMap, 0, this.heightMap, 0, heightMap.length);
    }

    public ChunkObject(PrimitiveChunkObject primitiveChunkObject){
        this.pos.set(primitiveChunkObject.pos);
        System.arraycopy(primitiveChunkObject.block, 0, this.block, 0, primitiveChunkObject.block.length);
        System.arraycopy(primitiveChunkObject.rotation, 0, this.rotation, 0, primitiveChunkObject.rotation.length);
        System.arraycopy(primitiveChunkObject.light, 0, this.light, 0, primitiveChunkObject.light.length);
        System.arraycopy(primitiveChunkObject.heightMap, 0, this.heightMap, 0, primitiveChunkObject.heightMap.length);
    }

    public Vector2i getPos(){
        return this.pos;
    }

    public Mesh getNormalMesh(int yHeight){
        return normalMesh[yHeight];
    }

    public Mesh getLiquidMesh(int yHeight){
        return liquidMesh[yHeight];
    }

    public Mesh getAllFaceMesh(int yHeight){
        return allFaceMesh[yHeight];
    }

    public Mesh[] getNormalMeshArray(){
        return normalMesh;
    }

    public Mesh[] getLiquidMeshArray(){
        return liquidMesh;
    }

    public Mesh[] getAllFaceMeshArray(){
        return allFaceMesh;
    }

    public byte[] getBlock() {
        return this.block;
    }

    public byte[] getRotation() {
        return this.rotation;
    }

    public byte[] getLight() {
        return this.light;
    }

    public byte[] getHeightMap() {
        return this.heightMap;
    }

    public boolean getSaveToDisk() {
        return this.saveToDisk;
    }

    public void setSaveToDisk(boolean truth){
        this.saveToDisk = truth;
    }

    public float getHover(){
        return this.hover;
    }

    public void setHover(float hover){
        this.hover = hover;
    }

    public void replaceOrSetNormalMesh(int yHeight, Mesh newMesh){
        Mesh currentMesh = normalMesh[yHeight];
        if (currentMesh != null){
            currentMesh.cleanUp(false);
        }
        normalMesh[yHeight] = newMesh;

    }

    public void replaceOrSetLiquidMesh(int yHeight, Mesh newMesh){
        Mesh currentMesh = liquidMesh[yHeight];
        if (currentMesh != null){
            currentMesh.cleanUp(false);
        }
        liquidMesh[yHeight] = newMesh;
    }

    public void replaceOrSetAllFaceMesh(int yHeight, Mesh newMesh){
        Mesh currentMesh = allFaceMesh[yHeight];
        if (currentMesh != null){
            currentMesh.cleanUp(false);
        }
        allFaceMesh[yHeight] = newMesh;
    }

    //internal 3D/2D to 1D calculations

    //internal chunk math
    private Vector3i indexToPos(int i ) {
        final int z = i / 2048;
        i -= (z * 2048);
        final int y = i / 16;
        final int x = i % 16;
        return new Vector3i( x, y, z);
    }

    private int posToIndex2D(int x, int z){
        return (z * 16) + x;
    }

    private int posToIndex2D(Vector2i pos){
        return (pos.y * 16) + pos.x;
    }

    private int posToIndex( int x, int y, int z ) {
        return (z * 2048) + (y * 16) + x;
    }

    private int posToIndex( Vector3i pos ) {
        return (pos.z * 2048) + (pos.y * 16) + pos.x;
    }
}
