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

local function defaultSwitch(input, default)
    if input == nil then
        return default;
    end
    return input;
end

crafter.registeredBlocks = {}

local Block = Object:extend();

local function nameSpaceCheck(internalName)
    if (internalName == "air")  then
        return true;
    end
    local _,colonCount = internalName:gsub(":","")
    assert(tonumber(colonCount) == 1, "ERROR! Block (" .. internalName .. ") is incorrectly named! Block name must have format (modNameSpace:blockName)!");
    local currentNameSpace = crafter.getNameSpace();
    local blockNameSpace = string.match(internalName, "(.*):");
    assert(currentNameSpace == blockNameSpace, "ERROR! Block (" .. internalName .. ") mod namespace does not match mod's (" .. currentNameSpace .. ")!");
end

function Block:new(definition)
    -- required data
    assert(type(definition) == "table", "registerBlock: ERROR! definition is a (" .. type(definition) .. ")! It must be a (table)!");
    assert(definition.internalName ~= nil and type(definition.internalName) == "string", "registerBlock: ERROR! definition internalName is invalid!");
    --assert(definition.ID == nil or type(definition.ID) == "number" and definition.ID > 0, "registerBlock: ERROR! definition ID is invalid!")
    if (definition.drawType ~= nil and definition.drawType ~= crafter.blockDrawTypes.AIR) then
        assert(definition.textures ~= nil or type(definition.textures) == "table" and #definition.textures == 6, "registerBlock: ERROR! texture definition needs to be a table with 6 strings!")
        for i = 1,6 do
            assert(definition.textures[i] ~= nil and type(definition.textures[i]) == "string", "registerBlock: ERROR! texture definition " .. tostring(i) .. " is invalid for block (" .. definition.internalName .. ")!")
        end
    end
    -- This needs to be implemented in java
    nameSpaceCheck(definition.internalName)

    -- End required data


    -- Private data
    local data = {
        --ID = definition.ID or -1;
        internalName = definition.internalName;
        textures = definition.textures;
        readableName = definition.readableName;
        walkable = defaultSwitch(definition.walkable, true);
        drawType = definition.drawType or crafter.blockDrawTypes.BLOCK;
        liquid = defaultSwitch(definition.liquid, false);
        liquidViscosity = definition.liquidViscosity or 0;
        climbable = defaultSwitch(definition.climbable, false);
        sneakJumpClimbable = defaultSwitch(definition.sneakJumpClimbable, false);
        falling = defaultSwitch(definition.falling, false);
        clear = defaultSwitch(definition.clear, false);
        damagePerSecond = definition.damagePerSecond or 0;
        light = definition.light or 0;
    }

    --function self:getID()
    --    return(data.ID);
    --end
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
    --print("adding: " .. newBlock:getInternalName());
    if (crafter.registeredBlocks[newBlock:getInternalName()] ~= nil) then
        error("registerBlock: ERROR! Tried to insert a duplicate of (" .. newBlock:getInternalName() .. ")!" );
    end
    crafter.registeredBlocks[newBlock:getInternalName()] = newBlock;
end

-- These are internal API use only, they become unavailable after api is done initializing
-- It WILL throw an error!

local function runExistenceCheck(blockName, fieldGetter)
    local gottenBlock = crafter.registeredBlocks[blockName];
    assert(gottenBlock ~= nil, "ERROR! Tried to get a nil block! (" .. tostring(blockName) .. ") is not a registered block!");
    assert(gottenBlock[fieldGetter] ~= nil, "ERROR! Tried to get a nil block definition field! (" .. tostring(fieldGetter) .. ") is not a getter!");
end

local javaIndex = 1;
local fieldIndex = 1;
local arrayIndex = 1;

-- getDrawType comes before getTextures to ensure a guarantee. The guarantee is if it's an air drawtype, ignore textures!
local fields = {
    "getDrawType", "getTextures", "getReadableName", "getWalkable", "getLiquid", "getLiquidViscosity",
    "getClimbable", "getSneakJumpClimbable", "getFalling", "getClear", "getDamagePerSecond", "getLight"
}

--local function checkAPILockout(functionName)
--    assert(not API_LOCKOUT, "Attempted to run function (" .. functionName .. ") externally! This is an internal only function!");
--end

-- These function allow java to dynamically intake lua block definitions
function crafter.getNextBlock()
    -- Fixme: Needs a lockout check
    --checkAPILockout("getNextBlock")
    local counter = 1;
    for _,v in pairs(crafter.registeredBlocks) do
        if counter == javaIndex then
            -- Automatically reset indices
            fieldIndex = 1;
            arrayIndex = 1;
            javaIndex = javaIndex + 1;
            return v:getInternalName();
        end
        counter = counter + 1;
    end
end

function crafter.getNextField()
    -- Fixme: Needs a lockout check
    --checkAPILockout("getNextField")
    if #fields < fieldIndex then
        return nil;
    end
    local returningValue = fields[fieldIndex];
    fieldIndex = fieldIndex + 1;
    return returningValue;
end

function crafter.getBlockData(blockName, fieldGetter)
    -- Fixme: Needs a lockout check
    runExistenceCheck(blockName, fieldGetter);
    return crafter.registeredBlocks[blockName][fieldGetter]();
end

function crafter.getBlockDataArray(blockName, fieldGetter, index)
    -- Fixme: Needs a lockout check
    runExistenceCheck(blockName, fieldGetter);
    local gottenTable = crafter.registeredBlocks[blockName][fieldGetter]();
    if gottenTable == nil then
        return nil;
    end
    if #gottenTable < index then
        return nil;
    end
    return gottenTable[index];
end

-- Whoosh, it's gone
--function crafter.closeAPI()
--    crafter.getNextBlock = nil
--    crafter.getBlockData = nil
--    crafter.getBlockDataArray = nil
--    print("Lua to Java API closed!")
--end


-- Air is a hardcode here, but you can always change it if you want to destroy the game
crafter.registerBlock({
    --ID = 0;
    internalName = "air";
    readableName = "air";
    --walkable = false;
    --textures = {"","","","","",""};
    drawType = crafter.blockDrawTypes.AIR;
})