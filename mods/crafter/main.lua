--print("Hello world!")

--[[
note: Textures go:
(right handed coordinate system)
front, back, left, right, bottom, top
]]

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
crafter.registerBlock({
    internalName = "crafter:grass";
    readableName = "Grass";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {"grass.png","grass.png","grass.png","grass.png","dirt.png","grass.png"}
})