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
    textures = {};
    walkable = true;
})
crafter.registerBlock({
    internalName = "crafter:dirt";
    readableName = "Dirt";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {};
    walkable = true;
})
crafter.registerBlock({
    internalName = "crafter:grass";
    readableName = "Grass";
    drawType = crafter.blockDrawTypes.BLOCK;
    textures = {"grass.png","grass.png","grass.png","grass.png","dirt.png","grass.png"}
})