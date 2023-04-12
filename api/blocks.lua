local Object = require("api.classic")

crafter.blockDrawTypes = {
    DEFAULT = -1;
    AIR = 0;
    BLOCK = 1;
    BLOCK_BOX = 2;
    TORCH = 3;
    LIQUID_SOURCE = 4;
    LIQUID_FLOW = 5;
    GLASS = 6;
    PLANT = 7;
    LEAVES = 8;
}

crafter.registeredBlocks = {}

local Block = Object:extend();

function Block:new(definition)
    assert(type(definition) == "table", "registerBlock: ERROR! definition is a (" .. type(definition) .. ")! It must be a (table)!");
    assert(definition.internalName ~= nil and type(definition.internalName) == "string", "registerBlock: ERROR! definition internalName is invalid!");
    assert(definition.ID == nil or type(definition.ID) == "number" and definition.ID > 0, "registerBlock: ERROR! definition ID is invalid!")
    if (definition.drawType ~= nil and definition.drawType ~= crafter.blockDrawTypes.AIR) then
        assert(definition.textures ~= nil or type(definition.textures) == "table" and #definition.textures == 6, "registerBlock: ERROR! texture definition needs to be a table with 6 strings!")
        for i = 1,6 do
            assert(definition.textures[i] ~= nil and type(definition.textures[i]) == "string", "registerBlock: ERROR! texture definition " .. tostring(i) .. " is invalid!")
        end
    end
    -- Private data
    local data = {
        ID = definition.ID or -1;
        internalName = definition.internalName;
        textures = definition.textures;
        readableName = definition.readableName;
        walkable = definition.walkable or true;
        drawType = definition.drawType or crafter.blockDrawTypes.BLOCK;
        liquid = definition.liquid or false;
        liquidViscosity = definition.liquidViscosity or 0;
        climbable = definition.climbable or false;
        sneakJumpClimbable = definition.sneakJumpClimbable or false;
        falling = definition.falling or false;
        clear = definition.clear or false;
        damagePerSecond = definition.damagePerSecond or 0;
        light = definition.light or 0;
    }

    function self:getID()
        return(data.ID);
    end
    function self:getInternalName()
        return(data.internalName);
    end
    function self:getTextures()
        return(data.textures);
    end
    function self:getReadableName()
        return(data.readableName);
    end
    function self:getWalkable()
        return(data.walkable);
    end
    function self:getDrawType()
        return(data.drawType);
    end
    function self:getLiquid()
        return(data.liquid);
    end
    function self:getLiquidViscosity()
        return(data.liquidViscosity);
    end
    function self:getClimbable()
        return(data.climbable);
    end
    function self:getSneakJumpClimbable()
        return(data.sneakJumpClimbable);
    end
    function self:getFalling()
        return(data.falling);
    end
    function self:getClear()
        return(data.clear);
    end
    function self:getDamagePerSecond()
        return(data.damagePerSecond);
    end
    function self:getLight()
        return(data.light);
    end
end

function crafter.registerBlock(definition)
    local newBlock = Block(definition);
    print("adding: " .. newBlock:getInternalName());
    if (crafter.registeredBlocks[newBlock:getInternalName()] ~= null) then
        error("registerBlock: ERROR! Tried to insert a duplicate of (" .. newBlock:getInternalName() .. ")!" );
    end
    crafter.registeredBlocks[newBlock:getInternalName()] = newBlock;
end


crafter.registerBlock({
    internalName = "air";
    --textures = {"","","","","",""};
    drawType = crafter.blockDrawTypes.AIR;

})