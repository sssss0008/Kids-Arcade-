package com.example.domain.models

import com.example.ui.theme.*

object ArcadeCatalog {

    val games: List<Game> = listOf(
        // GAME 1
        Game(
            id = "balloon_pop",
            title = "Balloon Pop Deluxe",
            subtitle = "Float & Pop Extravaganza",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "🎈",
            accentColor = ArcadePink,
            description = "Balloons float upwards! Tap to pop vibrant balloons, chain combos, catch golden balloons, and avoid thorny spikes!",
            rules = "Tap balloons before they float away. Golden balloons grant +50 points. Avoid spiky mines!",
            isPopular = true,
            isNew = false
        ),
        // GAME 2
        Game(
            id = "bubble_burst",
            title = "Bubble Burst",
            subtitle = "Soothing Pop Mania",
            category = GameCategory.FOCUS,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "🫧",
            accentColor = ArcadeCyan,
            description = "Bubbles shimmer across the screen! Burst them quickly before they drift away. Trigger satisfying chain reactions!",
            rules = "Tap bubbles of various sizes. Smaller bubbles are worth double points!",
            isPopular = true,
            isNew = false
        ),
        // GAME 3
        Game(
            id = "catch_fish",
            title = "Catch the Fish",
            subtitle = "Deep Sea Net Adventure",
            category = GameCategory.COORDINATION,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🐠",
            accentColor = ArcadeTeal,
            description = "Guide your fishing net underwater to catch swift rainbow fish, golden sea turtles, and starfish while dodging electric jellies!",
            rules = "Drag or tap the net to scoop up swimming sea creatures. Avoid spiky urchins!",
            isPopular = true,
            isNew = false
        ),
        // GAME 4
        Game(
            id = "jumping_frog",
            title = "Jumping Frog",
            subtitle = "Lily Pad Precision Hopper",
            category = GameCategory.COORDINATION,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🐸",
            accentColor = ArcadeGreen,
            description = "Help the playful frog hop across moving lily pads, log bridges, and riverbanks. Time your jumps perfectly to build massive combos!",
            rules = "Tap to leap forward to the next pad. Perfect landing in the center gives +2x combo!",
            isPopular = true,
            isNew = false
        ),
        // GAME 5
        Game(
            id = "flying_bird",
            title = "Flying Bird",
            subtitle = "Gentle Sky Flight",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🐦",
            accentColor = ArcadeIndigoPrimary,
            description = "Tap to flutter your wings through fluffy cloud gates, gather golden feathers, and soar as far as you can across sunny skies!",
            rules = "Tap to flap upwards. Release to glide down gently. Fly through colorful cloud hoops!",
            isPopular = true,
            isNew = false
        ),
        // GAME 6
        Game(
            id = "monkey_swing",
            title = "Monkey Swing",
            subtitle = "Jungle Vine Acrobat",
            category = GameCategory.ADVENTURE,
            difficulty = GameDifficulty.ADVANCED,
            iconEmoji = "🐒",
            accentColor = ArcadeAmber,
            description = "Swing from vine to vine across a lush tropical canopy! Release at the peak of the pendulum to grab juicy bananas and stars!",
            rules = "Tap while swinging to jump to the next vine. Grab bananas for speed boost!",
            isPopular = false,
            isNew = true
        ),
        // GAME 7
        Game(
            id = "candy_catcher",
            title = "Candy Catcher",
            subtitle = "Sweet Sugar Rush",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "🍬",
            accentColor = ArcadePinkLight,
            description = "Delicious candies, lollipops, and cupcakes are raining from the sky! Catch the treats in your candy basket and dodge broccoli!",
            rules = "Slide the basket left and right to catch sweets. Golden cupcakes give 3x stars!",
            isPopular = true,
            isNew = false
        ),
        // GAME 8
        Game(
            id = "treasure_collector",
            title = "Treasure Collector",
            subtitle = "Secret Temple Explorer",
            category = GameCategory.ADVENTURE,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🗝️",
            accentColor = ArcadeGold,
            description = "Explore colorful temple tiles, unlock mysterious treasure chests with golden keys, gather diamonds, and discover ancient secrets!",
            rules = "Tap surrounding tiles to move your explorer. Collect keys to open chests and reveal rewards!",
            isPopular = false,
            isNew = true
        ),
        // GAME 9
        Game(
            id = "coin_hunter",
            title = "Coin Hunter",
            subtitle = "Hyper Dash Coin Dash",
            category = GameCategory.SPEED,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🪙",
            accentColor = ArcadeGoldDark,
            description = "Sprint along energetic arcade lanes, grab glistening coins, avoid road obstacles, and trigger high-speed magnetic coin trails!",
            rules = "Switch lanes to collect coins and dodge orange traffic cones. Maintain your streak!",
            isPopular = true,
            isNew = false
        ),
        // GAME 10
        Game(
            id = "star_catcher",
            title = "Star Catcher",
            subtitle = "Starlight Night Symphony",
            category = GameCategory.FOCUS,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "✨",
            accentColor = ArcadePurple,
            description = "Luminous shooting stars illuminate the night sky. Tap glowing golden, rainbow, and super nova stars before they vanish into twilight!",
            rules = "Tap stars as they shimmer into view. Rainbow stars give bonus combo multipliers!",
            isPopular = true,
            isNew = false
        ),
        // GAME 11
        Game(
            id = "color_tap",
            title = "Color Tap",
            subtitle = "Chroma Reflex Challenge",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🎨",
            accentColor = ArcadeOrange,
            description = "Rapidly tap the button that matches the target color name or swatch. Tests cognitive reflexes and sharp visual discernment!",
            rules = "Watch the target prompt and tap the matching color tile as quickly as you can!",
            isPopular = false,
            isNew = true
        ),
        // GAME 12
        Game(
            id = "fruit_catcher",
            title = "Fruit Catcher",
            subtitle = "Orchard Harvest Fun",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "🍎",
            accentColor = ArcadeLime,
            description = "Fresh apples, oranges, watermelons, and golden pineapples fall from the tree canopy. Fill up your fruit crate!",
            rules = "Catch falling fruit in your basket. Chain 5 fruits without missing for combo mode!",
            isPopular = false,
            isNew = false
        ),
        // GAME 13
        Game(
            id = "maze_run",
            title = "Maze Run",
            subtitle = "Labyrinth Quest",
            category = GameCategory.PUZZLE,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🌀",
            accentColor = ArcadeIndigoDark,
            description = "Navigate a playful labyrinth, collect shining stars scattered along the turns, and reach the glowing winner trophy!",
            rules = "Use the direction arrows or tap tiles to guide your player to the exit with all 3 stars!",
            isPopular = false,
            isNew = true
        ),
        // GAME 14
        Game(
            id = "fast_fingers",
            title = "Fast Fingers",
            subtitle = "Speed Tap Frenzy",
            category = GameCategory.SPEED,
            difficulty = GameDifficulty.CHALLENGE,
            iconEmoji = "⚡",
            accentColor = ArcadePink,
            description = "How fast can you tap in 15 seconds? Tap targets as they bounce to smash the personal speed record!",
            rules = "Tap the glowing target button as many times as you can before the clock expires!",
            isPopular = true,
            isNew = false
        ),
        // GAME 15
        Game(
            id = "animal_runner",
            title = "Animal Runner",
            subtitle = "Endless Meadow Sprint",
            category = GameCategory.ADVENTURE,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🦊",
            accentColor = ArcadeAmber,
            description = "Run along rolling green hills with cute friendly animal companions. Jump over rocks, slide under branches, and collect stars!",
            rules = "Tap to jump over fences and rocks. Time your leap to collect high star arcs!",
            isPopular = true,
            isNew = true
        ),
        // GAME 16
        Game(
            id = "endless_jump",
            title = "Endless Jump",
            subtitle = "Skyward Bounce",
            category = GameCategory.COORDINATION,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🚀",
            accentColor = ArcadeCyanLight,
            description = "Bounce upwards from platform to platform indefinitely. Reach into the stratosphere and beyond!",
            rules = "Tilt or tap left and right to guide your character onto bouncy platforms!",
            isPopular = false,
            isNew = false
        ),
        // GAME 17
        Game(
            id = "toy_collector",
            title = "Toy Collector",
            subtitle = "Playroom Treasure Hunt",
            category = GameCategory.FOCUS,
            difficulty = GameDifficulty.EASY,
            iconEmoji = "🧸",
            accentColor = ArcadeRose,
            description = "Find and tap the requested toy hidden among colorful blocks, teddy bears, robots, and toy cars!",
            rules = "Look at the target toy icon on top and tap it quickly in the toy grid!",
            isPopular = false,
            isNew = false
        ),
        // GAME 18
        Game(
            id = "racing_fun",
            title = "Racing Fun",
            subtitle = "Turbo Kart Grand Prix",
            category = GameCategory.RACING,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🏎️",
            accentColor = ArcadeRose,
            description = "Drive a colorful mini kart on an exciting 3-lane track! Steer around oil slicks, zoom through boost pads, and collect stars!",
            rules = "Tap Left and Right to switch lanes. Hit yellow turbo arrows for super boost!",
            isPopular = true,
            isNew = false
        ),
        // GAME 19
        Game(
            id = "reflex_master",
            title = "Reflex Master",
            subtitle = "Sub-Millisecond Lab",
            category = GameCategory.REFLEX,
            difficulty = GameDifficulty.CHALLENGE,
            iconEmoji = "⚡",
            accentColor = ArcadePurpleDeep,
            description = "The ultimate test of human reaction time! Wait for green, tap instantly, and get detailed millisecond breakdown!",
            rules = "Wait until the screen turns bright GREEN, then tap as fast as humanly possible!",
            isPopular = true,
            isNew = true
        ),
        // GAME 20
        Game(
            id = "memory_cards",
            title = "Memory Cards",
            subtitle = "Arcade Pair Matching",
            category = GameCategory.MEMORY,
            difficulty = GameDifficulty.NORMAL,
            iconEmoji = "🃏",
            accentColor = ArcadePurple,
            description = "Flip cards over and find matching pairs of arcade mascots, stars, controllers, and diamonds with the fewest flips!",
            rules = "Flip two cards at a time. Match all pairs before time runs out!",
            isPopular = true,
            isNew = false
        )
    )

    val worlds: List<ArcadeWorld> = listOf(
        ArcadeWorld(
            id = "world_candy",
            name = "Candy World",
            emoji = "🍭",
            themeColor = ArcadePinkLight,
            description = "Sugary rolling hills, marshmallow clouds, and lollipop trees.",
            requiredStars = 0,
            isUnlocked = true,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_ocean",
            name = "Ocean World",
            emoji = "🌊",
            themeColor = ArcadeTeal,
            description = "Coral reefs, shimmering sea caves, and underwater treasure troves.",
            requiredStars = 25,
            isUnlocked = true,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_jungle",
            name = "Jungle World",
            emoji = "🌴",
            themeColor = ArcadeGreen,
            description = "Tropical vine canopies, cascading waterfalls, and playful monkeys.",
            requiredStars = 60,
            isUnlocked = false,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_space",
            name = "Space World",
            emoji = "🌌",
            themeColor = ArcadeIndigoPrimary,
            description = "Orbiting asteroid belts, neon starlight, and friendly alien robots.",
            requiredStars = 100,
            isUnlocked = false,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_treasure",
            name = "Treasure World",
            emoji = "💎",
            themeColor = ArcadeGold,
            description = "Ancient stone temples with sparkling rubies, gold coins, and secret doors.",
            requiredStars = 150,
            isUnlocked = false,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_toy",
            name = "Toy World",
            emoji = "🧸",
            themeColor = ArcadeAmber,
            description = "Giant building blocks, toy trains, and wind-up arcade machines.",
            requiredStars = 200,
            isUnlocked = false,
            gamesCount = 3
        ),
        ArcadeWorld(
            id = "world_racing",
            name = "Racing World",
            emoji = "🏁",
            themeColor = ArcadeRose,
            description = "Neon hyper-speed race tracks with looping ramps and fireworks.",
            requiredStars = 280,
            isUnlocked = false,
            gamesCount = 2
        )
    )

    val avatars: List<AvatarItem> = listOf(
        AvatarItem("fox", "Rusty Fox", "🦊", "Animals", true, "Default starter avatar"),
        AvatarItem("panda", "Bambu Panda", "🐼", "Animals", true, "Default starter avatar"),
        AvatarItem("lion", "Leo Lion", "🦁", "Animals", false, "Unlock at Rank: Player"),
        AvatarItem("frog", "Hoppy Frog", "🐸", "Animals", true, "Default starter avatar"),
        AvatarItem("robot", "Beep-Boop Bot", "🤖", "Robots", false, "Unlock at Rank: Pro Gamer"),
        AvatarItem("space_alien", "Cosmo Alien", "👾", "Space", false, "Unlock in Space World"),
        AvatarItem("astronaut", "Star Explorer", "👨‍🚀", "Space", false, "Collect 50 Stars"),
        AvatarItem("ninja", "Shadow Swift", "🥷", "Arcade", false, "Win 10 Reflex Games"),
        AvatarItem("superhero", "Arcade Hero", "🦸", "Arcade", false, "Unlock at Rank: Arcade Hero"),
        AvatarItem("wizard", "Mystic Star", "🧙", "Fantasy", false, "Score 2,000+ points"),
        AvatarItem("racer", "Turbo Kart Driver", "🏎️", "Sports", false, "Play Racing Fun 3 times"),
        AvatarItem("champion", "Crown Champion", "👑", "Arcade", false, "Reach Ultimate Champion")
    )

    val initialAchievements: List<Achievement> = listOf(
        Achievement("first_game", "First Game", "Play your very first arcade mini-game!", "🎮", 100, 10, true, 1, 1),
        Achievement("first_record", "Record Breaker", "Set your first personal high score record!", "🏆", 150, 15, false, 0, 1),
        Achievement("arcade_explorer", "Arcade Explorer", "Play 10 different mini-games in the arcade!", "🧭", 250, 25, false, 3, 10),
        Achievement("combo_king", "Combo King", "Reach a 5x combo multiplier in any game!", "🔥", 200, 20, false, 1, 5),
        Achievement("speed_star", "Speed Star", "Complete a speed challenge with 80%+ accuracy!", "⚡", 200, 20, false, 0, 1),
        Achievement("collector", "Star Collector", "Collect a total of 100 shining arcade stars!", "⭐", 300, 30, false, 35, 100),
        Achievement("reflex_pro", "Reflex Pro", "Score under 280ms in the Reflex Master lab!", "⚡", 250, 25, false, 0, 1),
        Achievement("daily_streak", "Streak Master", "Play 3 days in a row!", "📅", 200, 20, false, 2, 3),
        Achievement("arcade_hero", "Arcade Hero", "Reach the prestigious Arcade Hero rank!", "🌟", 500, 50, false, 0, 1),
        Achievement("ultimate_champion", "Ultimate Champion", "Become the Ultimate Arcade Champion!", "👑", 1000, 100, false, 0, 1)
    )

    val dailyMissions: List<DailyMission> = listOf(
        DailyMission("m1", "Arcade Explorer", "Play 3 different mini-games today", 1, 3, 120, 15),
        DailyMission("m2", "Score Hunter", "Score over 400 points in any game", 0, 1, 100, 10),
        DailyMission("m3", "Star Collector", "Gather 20 stars from games", 8, 20, 150, 15),
        DailyMission("m4", "Combo Master", "Perform a 4x combo in any game", 0, 1, 120, 12)
    )

    val dailyChallenge: DailyChallenge = DailyChallenge(
        id = "dc_bubbles",
        title = "Today's Daily Challenge",
        description = "Pop 30 bubbles in Bubble Burst or balloons in Balloon Pop!",
        targetGameId = "bubble_burst",
        currentCount = 14,
        targetCount = 30,
        xpReward = 200,
        starsReward = 25,
        isCompleted = false
    )

    val weeklyChallenge: WeeklyChallenge = WeeklyChallenge(
        id = "wc_mastery",
        title = "Weekly Arcade Master",
        description = "Play 7 different arcade mini-games and earn 50 stars this week!",
        currentCount = 4,
        targetCount = 7,
        xpReward = 500,
        starsReward = 50,
        isCompleted = false
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            category = "Reflex & Colors",
            question = "Which color was the highest scoring balloon in Balloon Pop Deluxe?",
            options = listOf("Golden Balloon", "Blue Balloon", "Green Balloon", "Purple Balloon"),
            correctIndex = 0,
            explanation = "Golden balloons always give bonus points and extra stars!",
            emoji = "🎈"
        ),
        QuizQuestion(
            id = "q2",
            category = "Coordination",
            question = "What should the frog land on to cross the river safely?",
            options = listOf("Spikes", "Lily Pads", "Water Waves", "Electric Urchins"),
            correctIndex = 1,
            explanation = "Frogs love bouncing from one safe green lily pad to the next!",
            emoji = "🐸"
        ),
        QuizQuestion(
            id = "q3",
            category = "Pattern Recognition",
            question = "What comes next in the arcade pattern: Red, Blue, Red, Blue, ...?",
            options = listOf("Green", "Yellow", "Red", "Purple"),
            correctIndex = 2,
            explanation = "The pattern alternates between Red and Blue, so Red is next!",
            emoji = "🎨"
        ),
        QuizQuestion(
            id = "q4",
            category = "Animal Knowledge",
            question = "What delicious treat does the swinging monkey collect in the jungle canopy?",
            options = listOf("Apples", "Bananas", "Candy", "Carrots"),
            correctIndex = 1,
            explanation = "Monkeys in the jungle love sweet ripe bananas!",
            emoji = "🍌"
        ),
        QuizQuestion(
            id = "q5",
            category = "Speed & Memory",
            question = "How many bottom navigation tabs exist in Kids Arcade Mania?",
            options = listOf("Three", "Exactly Four", "Five", "Six"),
            correctIndex = 1,
            explanation = "Kids Arcade Mania features exactly four bottom tabs: Home, Games, Practice, and About!",
            emoji = "📱"
        )
    )
}
