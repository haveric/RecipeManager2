  RecipeManager information file ( lastchanged: v1.27 | I'll mark changes in the file with (NEW), (UPDATED) or (REMOVED) )

Plugin's BukkitDev page: http://dev.bukkit.org/server-mods/recipemanager/

----------------------------------------------------------------------------------------------------

List of item names: http://jd.bukkit.org/apidocs/org/bukkit/Material.html
List of data values: http://www.minecraftwiki.net/wiki/Data_value#Data
List of enchantments: http://jd.bukkit.org/apidocs/org/bukkit/enchantments/Enchantment.html
Also consult aliases.yml for shortened itemdata values

In the 'recipes' folder you can create .txt files (with any names) which can contain recipes.
The plugin will also read sub-folders (except 'DISABLED' folder), there you can place recipes which you don't want to be read.
Also, the default recipes are stored in the 'default' folder, you can edit and/or remove them at will.

(NEW)
With the new 1.3's written books, each recipe file is now generated into a recipe book.
You can use the @book: title | author flag at the begining of the file to define the title and author, otherwise the title will be the path and filename and the author will be "Unknown".
Also, you can use the @getbook flag to get a recipe book as result or the @copy flag to copy existing books (works on any book, not just recipe books).
You can also use the '/rmgetbook title author' command to get the book items which is available for OPs.

What you need to know about the recipe files:
 - Everything is case insensitive, yoU CaN wRITe as YOU liKE.
 - Anything that starts with # or // is ignored (comment), /* and */ comments are not supported tough.
 - Data, amount and enchantments are completely optional in items.
 - TYPE:DATA can be replaced by an alias ! (defined in aliases.yml)
 - Not all items support amount and enchantments, read the recipe's syntax.
 - Enchanted items can't have more than 1 item, if one does, its amount will be set to 1.
 - '...' means that the previous statement can be repeated
 - You must leave an empty line between recipes
 - Chances for multiple results are auto-filled up to 100% with air
 --- example: if you define a recipe with result 5% cobble and 25% stone, the system will automatically make it 70% chance of failure
 - You can also define which item is the one that auto-fills
 --- example: if you define 5% cobble and stone (without percentage), the stone will automatically have 95% chance


----------------------------------------------------------------------------------------------------

	Shaped recipes

For workbench and inventory crafting. You can specify a minimum of 1 line and 1 item to a maximum of 3 lines and 3 items,
specifying maximum of 2 lines with 2 items each would allow recipe to be crafted inside the invenory as well.
Supports multiple results with success chance, the rest of the chance is automatically filled as fail chance
If multiple results are defined, the first one will be displayed when placing ingredients.

	CRAFT
	TYPE:DATA + TYPE:DATA + TYPE:DATA
	TYPE:DATA + TYPE:DATA + TYPE:DATA
	TYPE:DATA + TYPE:DATA + TYPE:DATA
	 = CHANCE% TYPE:DATA:AMOUNT | ENCHANTMENT:LEVEL, ...
	 = ...


Examples:

CRAFT
@minlevel: 3 | You're not old enough to play with that ! // at least level 3 with custom message
COBBLESTONE
COBBLESTONE + COBBLESTONE
= 30% COBBLESTONE_STAIRS:0:4
= 50% COBBLESTONE:0:2			// recipe has 20% failure chance

CRAFT
@permission: uber.perm.node
@command: /sethome														// command to player
@command: say {player} used a uber sword recipe and got {result} !!!	// command to server, {player} is replaced with player name and {result} with result item's name
@command: pex user {player} add rewarded.permission.node				// another command to server
DIAMOND
REDSTONE
STICK
= 75% DIAMOND_SWORD | FIRE_ASPECT:4, DAMAGE_ALL:MAX		// using MAX will set the maximum default level, you can go above the max level if you specify it
= DIAMOND_SWORD											// this is filled with the rest of percentage
														// recipe has 0% failure chance

----------------------------------------------------------------------------------------------------


	Shapeless recipes

For workbench and inventory crafting. Up to 9 items in total. Ingredients must be listed on one line.
Supports multiple results with success chance.
If multiple results are defined, the first one will be displayed when placing ingredients.


	COMBINE
	TYPE:DATA:AMOUNT + ...
	 = CHANCE% TYPE:DATA:AMOUNT | ENCHANTMENT:LEVEL, ...
	 = ...


Examples:

COMBINE
COBBLESTONE + LEAVES
= 99% MOSSY_COBBLESTONE
= 1% DIAMOND				// recipe has 0% failure chance

COMBINE
@worlds: world_nether
DIRT:*:9					// * or -1 means "item with any data value"
= DIRT:0:64


----------------------------------------------------------------------------------------------------


	Furnace smelting recipes

Recipes for furnaces. Time is optional, default 9.25, you can also specify one value to have a fixed time.
Data value is impossible to set for ingredients at the moment
Supports success chance (single result)


	SMELT
	ITEM % MINTIME-MAXTIME
	= CHANCE% TYPE:DATA:AMOUNT | ENCHANTMENT:LEVEL, ...


Examples:

SMELT
@minmoney: 100				// requires player to have 100 money to place the ingredient in the furnace
IRON_PICKAXE:* % 25-30		// random time betweee 25 and 30 seconds
= IRON_PICKAXE:0			// fully repaired pickaxe

SMELT
@command: say Smelt recipe made {result} !	// server command when smelt finished, {result} is replaced with the result item's name
IRON_INGOT % 600							// fixed time, 10 minutes
= 70% CHAINMAIL_CHESTPLATE					// 30% fail chance

----------------------------------------------------------------------------------------------------


	Furnace fuel recipes
	
Fuel for furnaces. You can specify one time value to have a fixed time.


	FUEL
	TYPE:DATA % MINTIME-MAXTIME


Examples:

FUEL
@minexp: 5000		// requires player to have 5000 exp to put this fuel in the furnace
SAND % 1-100		// will burn between 1 and 100 seconds

FUEL
WATER_BUCKET % 120	// defining only mintime will set a fixed time, this will also return the bucket if configured to do so in config.yml


----------------------------------------------------------------------------------------------------


    Recipe flags

(UPDATED)
The @flags can be use to add special behaviour for recipes when created
Flags can be defined before recipes to apply to all recipes that follow.
Flags can also be set for individual recipes, right after the recipe type (CRAFT, SMELT, etc) which also have priority over the flags set before the recipes
You can use the "false" value on any of the flags to disable them for individual recipes.

  About messages:
The "| <success message> | <fail message>" part is OPTIONAL for all flags.
Messages can be any text that will be printed to player or can be set to false to disable the message for recipe.
Any message can be set to nothing to skip it, example not setting fail message: @giveexp: -50 | | You lost 50 experience!
Some flags do not have success message or failure message, for example, giveexp with positive values doesn't have afail message but giveexp with negative values has since it checks if player has the required experience.
Absolutely all messages support colors, <red>, <green>, <gold>, <dark_blue>, etc.
You can also use variables in certain messages, like {amount}, {money}, see default messages (messages.yml file) for usage.

  The flags list:

@failmessage: message
  Specifies a custom fail by chance message, default: "Recipe failed! ({chance} chance)"
  You can use "false" to disable it.
  NOTE: The {chance} word will be replaced with the chance percent (with the % char!)

@permission: permission.node = true/false/op/non-op | <fail message>
  Specifies what permission is required for the recipe, optionally change its default value.
  The "true" value sets it by default to all players, "false" does the opposite, "op" only sets it to operators and "non-op" sets it only to players that are not operators.
  NOTE: The default value overwrites the previous default value (including permissions made by other plugins)

@groups: group, ... | <fail message>
  Permission groups that are required to use this recipe, players need to be in all listed groups to use the recipe.
  NOTE: Requires Vault plugin.

@anygroup: group, ... | <fail message>
  Permission groups that can use this recipe, players need to be in at least one group to use the recipe.
  NOTE: Requires Vault plugin.

@worlds: world, ... | <fail message>
  Specifies what worlds the recipe is allowed to be crafted in, worlds that are NOT in this list will be considered restricted.

@proximity: value | <fail message> | <warn message>
  Sets the required proximity of the smelting/fueling player to the furnace, does nothing for workbench recipes.
  Values:
    - a number of blocks distance between player and furnace required for smelting to work
    - online - player must be online for smelting/fueling to work
    - false - disable the feature
  If the value is not false and requirements are not met, the furnace will send the failure message to the player (if online) and stop smelting/fueling.
  NOTE: Smelters and fuelers are saved in furnacedata.dat between server restarts.
  NOTE: The <warn message> is displayed whenever someone places the ingredient in the furnace, warning them of proximity or online requirement.
        Its got a default message, no need to specify a custom one if you don't need it tough. Setting it to false will, of course, disable it.

@explode: when chance power fire | <message>
  Makes the player/workbench/furnace explode when recipe is crafted.
  'when' argument can be:
     fail - when recipe fails due to chance
     success - when recipe was succesfully crafted/smelted
     always - regardless of recipe state
  'chance' argument is the chance of explosion after "when" is triggered, number 1-100
  'power' argument is the explosion power (4 is TNT-like)
  'fire' argument is optional and can be true or false if the explsoion should set fire or not (default false)
  Example: @explode: fail 25 2  =>  when recipe fails there's a 25% chance of a power 4 explosion without fire
  NOTE: Fuel recipes never trigger on "fail" !
  NOTE: The <message> is printed whenever explosion occurs and overwrites the default ones from messages.yml

@giveexp: number | <fail message> | <success message>
  How much experience to give or subtract (negative values) from player when crafting
  NOTE: Works for furnaces but only if ingredient was placed after v1.22 was installed and player is online
  NOTE: Negative values automatically check if player has enough exp, you do NOT need to use @minexp!

@minexp: number | <fail message>
  Players that have less than this amount of experience can't craft it

@maxexp: number | <fail message>
  Players that have more than this amount of experience can't craft it


@givelevel: number | <fail message> | <success message>
  How many levels to give or subtract (negative values) from player when crafting
  NOTE: Works for furnaces but only if ingredient was placed after v1.22 was installed and player is online
  NOTE: Negative values automatically check if player has enough levels, you do NOT need to use @minlevel!

@minlevel: number | <fail message>
  Players that are a lower level than this can't craft it

@maxlevel: number | <fail message>
  Players that are a higher level than this can't craft it


@givemoney: float number | <fail message> | <success message>
  How much money to give or subtract (negative values) from player when crafting
  NOTE: Works for furnaces but only if ingredient was placed after v1.22 was installed and player is online
  NOTE: Requires a economy plugin/interface (Vault, iConomy) to work
  NOTE: Negative values automatically check if player has enough money, you do NOT need to use @minmoney!

@minmoney: float number | <fail message>
  Players that have less than this amount of money can't craft it
  NOTE: Requires an economy plugin/interface (Vault, iConomy) to work

@maxmoney: float number | <fail message>
  Players that have more than this amount of money can't craft it
  NOTE: Requires an economy plugin/interface (Vault, iConomy) to work


(NEW)
@needitems: style | item, ... | <fail message>
  Sets what items the player must have to be able to use the recipe (items won't be affected)
  'style' argument can be:
    any - at least one item is required (default)
    hand - at least one item is required to be hold in hand
    req - all items are required to be in inventory
  Item format is just like recipe results: type:data:amount | enchantment:level
  Example: @needitems: iron_axe, wool, stick:0:2  => player needs to have iron_axe, wool (any color) and 2 sticks
  NOTE: Works for furnaces but only if ingredient was placed after v1.22 was installed and player is online

(NEW)
@blocksnearby: style radius type | block, ... | <fail message>
  Makes the recipe require blocks to be near the workbench/furnace in order to work, block listing order is irelevant.
  'style' argument can be:
    any - at least one block is required to be in range (default)
    req - all blocks are required to be in range
  'radius' argument must be a number which defines the max distance in all direction that the blocks must be in (box shape search)
  'type' argument can be (optional):
    box - search blocks in a box shape (default) [http://1.bp.blogspot.com/-SBKHm4B-PVI/UA3tBPm5uII/AAAAAAAAIJ8/O-rrQT6pxps/s1600/box.gif]
    sphere - search blocks in a sphere shape [http://comp.uark.edu/~jgeabana/blochapps/sphere2.jpg]
  Block format is: type:data  (just like items, data value optional)
  Example: @blocksnearby: 5 | gold_block, gold_block, diamond_block  => requires 2 gold blocks and a diamond block in a 5m radius

(NEW)
@blockstop: style | block, ... | <fail message>
  Makes the recipe require blocks to be on top of the workbench/furnace in order to work.
  'style' argument can be:
    any - any of these blocks must be directly on top of the workbench/furnace
    stack - block order defines the order they need to be on top of the orkbench/furnace (default)
  Block format is: type:data  (just like items, data value optional)

(NEW)
@blocksunder: style | block, ... | <fail message>
  Makes the recipe require blocks to be underneath the workbench/furnace in order to work.
  'style' argument can be:
    any - any of these blocks must be directly on top of the workbench/furnace
    stack - block order defines the order they need to be on top of the orkbench/furnace (default)
  Block format is: type:data  (just like items, data value optional)


@command: command
  Send a command when recipe is crafted, if command starts with / it will be sent to the crafter, if not, it will be sent to the server
  NOTE: This flag can be repeated to add more commands !
  NOTE: Using "false" will remove *ALL* commands for the current recipe, altough you can (re)add them afterwards.
  You can use some variables inside the commands to be filled automatically:
    {player} - replaced with the crafter's name (works even for furnace recipes and fuels but NOT between server restarts!)
    {ingredient} - replaced with the "material:data" of the ingredient or fuel used (does not work for CRAFT or COMBINE !)
    {result} - replaced with the "material:data" of the result crafted or "nothing" if it's nothing (does not work for fuels, use {ingredient} instead)

@message: message
  Send a message to the crafter when recipe is crafted
  NOTE: This flag can be repeated to add more messages !
  NOTE: Using "false" will remove *ALL* messages for the current recipe, altough you can (re)add them afterwards.
  NOTE: You can use the same variables from @command!

@log: true/false
  Logs to server.log and console when recipe is triggered
  Log formats:
  YEAR-MM-DD HH:MM:SS [INFO] [RecipeManager] [@log] <log>
  And <log> can be...
  for craft: <player> crafted <ingredients> to make <result>
  for combine: <player> combined <ingredients> to make <result>
  for smelting: <player> smelted <ingredient> to make <result>
  for fuels: <player> fueled furnace with <ingredient>

(NEW)
@override: true/false
  Removes the original recipe and creates a custom one with custom flags and everything this plugin has to offer.
  It is critical that ingredients have the same type and data value as the original, otherwise it's a diferent recipe
  For CRAFT recipes it must exacly match the original shape.
  For COMBINE recipes the ingredients order doesn't matter.
  NOTE: Doesn't work for FUEL recipes because those aren't really recipes and they're overwrite the original regardless of this flag
  NOTE: You will be notified if the original recipe if using this flag and the original recipe wasn't found

(NEW)
@copy: ingredient
  Makes the specified ingredient (type:data format like the ingredient, but no amount) remain in the crafting grid and clone them in the result slot
  There's an example of this flag in the 'special recipes.txt' file in the recipes folder.
  NOTE: It only works for CRAFT and COMBINE recipes
  NOTE: The ingredient must be unique in the recipe
  NOTE: This flag overrides the result defined, so it doesn't matter what result or how many results you set to it.

(NEW)
@book: title | author
  Sets in what recipe book the recipe(s) are added to.
  If this flag is not set, recipes will be in the recipe book having the name of the file with "Unknown" author.
  Title is required but author is not, if not defined it will be set to "Unknown".
  Both title and author make the book unique, if you define a diferent author for 2 books with the same title, they'll be 2 diferent books.
  NOTE: Title and author support colors (<red>, <dark_green>, etc)
  NOTE: Setting this flag to "false" will prevent recipes from being in any recipe books.
  NOTE: Does not work for individual recipes, you can only set it before or between recipes (to affect all that follow).

(NEW)
@bookinfo: text
  Sets a description for the recipe viewable in its book.
  NOTE: Text supports colors (<red>, <dark_gray>, <green>, etc)
  NOTE: There's no actual limit to the text size but be aware of the recipe size on page.
  NOTE: This should be used for individual recipes, but it works for entire recipe files as well.

(NEW)
@getbook: title | author
  Makes the recipe result in the specified recipe book.
  NOTE: Title and author are case insensitive.
  NOTE: Title and author in this method must not contain colors, they are stripped when comparing.
  NOTE: If original book doesn't have author defined, you can also skip it here.
  NOTE: Does nothing for FUEL recipes.

You can see examples in the recipes syntaxes' examples.
I will add more flags when I get more ideas.