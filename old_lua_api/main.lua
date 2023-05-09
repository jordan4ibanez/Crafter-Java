--print("testing works :D")

crafter.registerBlock({
    internalName = "testing:aTest";
    readableName = "A Test";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {"dirt.png", "stone.png", "glass.png", "iron_block.png", "ice.png", "gold_block.png"};
    walkable = false;
})