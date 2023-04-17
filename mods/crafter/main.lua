print("Hello world!")

crafter.registerBlock({
    internalName = "crafter:stone";
    readableName = "Stone";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {"stone.png","stone.png","stone.png","stone.png","stone.png","stone.png"};
    walkable = true;
})
crafter.registerBlock({
    internalName = "crafter:dirt";
    readableName = "Dirt";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {"dirt.png","dirt.png","dirt.png","dirt.png","dirt.png","dirt.png"};
    walkable = true;
})