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
            print(player.getName());
        })

        // Nashorn OOP style
//        for each (player in crafter.getConnectedPlayers()) {
//            print(player);
//        }
    }
)