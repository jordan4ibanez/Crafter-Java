-- Object is available for everyone to utilize - classic is pretty neat
Object = require("api.classic")

crafter = {}

local nameSpace;
local verifier = 0;


function crafter.setNameSpace(newNameSpace)
    nameSpace = newNameSpace
    verifier = verifier + 1;
    --print("LUA new namespace: ", nameSpace, " | delta: ", verifier);
    return verifier;
end
function crafter.getNameSpace()
    return nameSpace;
end
function crafter.getVerifier()
    return verifier;
end

require("api.blocks")