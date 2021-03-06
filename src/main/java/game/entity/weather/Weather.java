package game.entity.weather;

public class Weather {

    /*

    private final Map<Integer, RainDropEntity> rainDrops = new HashMap<>();
    private final Deque<Integer> deletionQueue = new ArrayDeque<>();
    private final Mesh rainDropMesh = createRainDropMesh();
    private int currentID = 0;

    public void rainDropsOnTick(){
        for (RainDropEntity thisParticle : rainDrops.values()){
            boolean onGround = applyParticleInertia(thisParticle.pos, thisParticle.inertia, true,true);
            thisParticle.timer += 0.01f;
            if (thisParticle.timer > 10f || onGround){
                deletionQueue.add(thisParticle.key);
            }
        }

        while (!deletionQueue.isEmpty()){
            rainDrops.remove(deletionQueue.pop());
        }
    }

    public void createRainDrop(Vector3d pos, Vector3f inertia){
        rainDrops.put(currentID, new RainDropEntity(pos, inertia, currentID));
        currentID++;
    }


    public Collection<RainDropEntity> getRainDrops(){
        return rainDrops.values();
    }

    public Mesh getRainDropMesh(){
        return rainDropMesh;
    }

    private Mesh createRainDropMesh() {
        float pixelScale = 0.25f;

        ArrayList<Float> positions = new ArrayList<>();
        ArrayList<Float> textureCoord = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Float> light = new ArrayList<>();


        int indicesCount = 0;


        //front
        positions.add(pixelScale);
        positions.add(pixelScale*2);
        positions.add(0f);

        positions.add(-pixelScale);
        positions.add(pixelScale*2);
        positions.add(0f);

        positions.add(-pixelScale);
        positions.add(0f);
        positions.add(0f);

        positions.add(pixelScale);
        positions.add(0f);
        positions.add(0f);

        //front
        float frontLight = 1f;//getLight(x, y, z + 1, chunkX, chunkZ) / maxLight;

        //front
        for (int i = 0; i < 12; i++) {
            light.add(frontLight);
        }
        //front
        indices.add(0);
        indices.add(1 + indicesCount);
        indices.add(2 + indicesCount);
        indices.add(0);
        indices.add(2 + indicesCount);
        indices.add(3 + indicesCount);

        //-x +x   -y +y
        // 0  1    2  3

        // 0, 1,  2, 3
        //-x,+x, -y,+y

        //front
        textureCoord.add(1f);//1
        textureCoord.add(0f);//2
        textureCoord.add(0f);//0
        textureCoord.add(0f);//2
        textureCoord.add(0f);//0
        textureCoord.add(1f);//3
        textureCoord.add(1f);//1
        textureCoord.add(1f);//3


        //convert the position objects into usable array
        float[] positionsArray = new float[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            positionsArray[i] = positions.get(i);
        }

        //convert the light objects into usable array
        float[] lightArray = new float[light.size()];
        for (int i = 0; i < light.size(); i++) {
            lightArray[i] = light.get(i);
        }

        //convert the indices objects into usable array
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        //convert the textureCoord objects into usable array
        float[] textureCoordArray = new float[textureCoord.size()];
        for (int i = 0; i < textureCoord.size(); i++) {
            textureCoordArray[i] = textureCoord.get(i);
        }

        return new Mesh(positionsArray, lightArray, indicesArray, textureCoordArray,new Texture("textures/raindrop.png"));
    }
     */
}
