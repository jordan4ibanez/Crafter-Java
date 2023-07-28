// Testing out the actions API :D
//crafter.registerOnJoin((player) => {
//    print("Hello, " + player.getName() + "! Welcome to Crafter Classic!");
//});

// You can localize variables like so
//{
//    let x = 0;
//    crafter.registerOnTick((delta) => {
//        x += delta;
//        if (x > 1) {
//            print("tick");
//            x = 0;
//        }
//    });
//};

// This crashes, because x is not defined
// print("x is: " + x)

// This one runs only once
//crafter.registerOnTimer(
//    2,
//    false,
//    () => {
//        print("timer api test! (run once)");
//    }
//);

// This one runs forever(
//crafter.registerOnTimer(
//    3,
//    true,
//    (delta) => {
//        print("Timer api test! (running forever)");
//    }
//);

// I call this one "brick walker"
crafter.registerOnTimer(
    0.5,
    true,
    () => {
        // 2 ways to iterate

        // Functional
        crafter.getConnectedPlayers().forEach((player) => {
            const position = new Vector3f(player.getPosition());
            const distance = 5;
            for (x = position.x - distance; x <= position.x + distance; x += distance) {
                for (z = position.z - distance; z <= position.z + distance; z += distance) {
                    if (!crafter.isChunkLoaded(position.x + x, position.y, position.z + z)) {
                        return;
                    }
                }
            }

            const min = new Vector3i(
                math.floor(position.x - distance),
                math.floor(position.y - distance),
                math.floor(position.z - distance)
            );

            const max = new Vector3i(
                math.floor(position.x + distance),
                math.floor(position.y + distance),
                math.floor(position.z + distance)
            );

            print("hi")

            blockManipulator.setPositions(min,max);
            blockManipulator.readData();

            for (x = position.x - distance; x <= position.x + distance; x += distance) {
                for (z = position.z - distance; z <= position.z + distance; z += distance) {
                    for (y = position.y - distance; y <= position.y + distance; y += distance) {
                        var rawData = blockManipulator.getData(x,y,z);

                    }
                }
            }

//            if (crafter.isChunkLoaded(position)) {
//                position.y -= 1;
//                crafter.setBlockName(position, "crafter:brick");
////                print("bricked!" + math.random())
//            }
        })

        // Nashorn OOP style
//        for each (player in crafter.getConnectedPlayers()) {
//            print(player);
//        }
    }
)