## Change log

### v2.31.0
* REMOVED support for Minecraft 1.8 - 1.11
* REMOVED FLAG: 1.20.5+: `@LocalizedName`
* REMOVED `@Summon` arguments: `elder`, `horse`, `skeleton`, `zombievillager`
* REMOVED `@PotionItem` arguments: `level`, `extended`
  * The type argument is also now required
* REMOVED `@IngredientCondition`, `@HoldItem` argument: `potion` options for `level`, `extended`
* REMOVED `@IngredientCondition`, `@HoldItem` argument: `banner` color option
* UPDATED: `messages.yml`: `nopotion` replaced with `nopotiontype`
* REMOVED: `messages.yml`: Removed `nobannercolor`
* RENAMED: `@ItemName` to `@DisplayName`. Alias `@Name` is still recommended.
* NEW FLAG: 1.20.5+: `@ItemName`: Sets the item's name
  * Item name differs from display name in that it is cannot be edited by an anvil, is not styled with italics, and does not show labels.
* NEW FLAG: 1.20.5+: `@HideTooltip`: Hides the result's tooltip
* NEW FLAG: 1.20.5+: `@EnchantmentGlintOverride`: Makes the result glint, even without enchantments
* NEW FLAG: 1.20.5+: `@FireResistant`: Makes the result immune to burning in fire
* NEW FLAG: 1.20.5+: `@MaxStackSize`: Changes result's stack size (from 1 to 99)
* NEW FLAG: 1.20.5+: `@Rarity`: Sets the item's rarity, which affects the name's default color
* NEW FLAG: 1.20.5+: `@Food <nutrition> | [arguments]`: Adds a food component to an item, making it consumable
* NEW FLAG: 1.20.5+: `@FoodPotionEffect <chance> | <potionEffect>`: Adds a potion effect as a food component to an item
* NEW FLAG: 1.20.5+: `@OminousBottleItem <amplifier>`: Sets the amplifier amount for an Ominous Bottle's bad omen effect
* FIX: 1.16+: `@Summon`: Add `strider` as allowed saddle argument

### v2.30.0
* FIX: Fixed duplicate lores on brewing recipes
* FIX: Fixed repair and repair-metadata special recipes
* FIX: Fuel recipes can now be set to as low as 1 tick instead of 1 second
* FIX: Campfire recipe group option is now applied correctly
* FIX: 1.19.3: Campfire recipes support for random cook times
* NEW: 1.19.3: Add support for recipe book categories
* NEW: Added recipe book category to crafting and cooking recipe parsers
* FIX: Fix brewing recipes not starting when shift clicking or dragging items into the brewing stand
* FIX: 1.12 and below: Fixed /rmextract
* FIX: `@SkullOwner`: Fixed the dynamic `{player}` argument
* FIX: 1.18.1+: `@SkullOwner`: Replace texture handling with player profiles
  * It is recommended to replace `texture <base64>` with `textureurl <url>`
* FIX: 1.19: Fixed scaffolding fuel amount
* NEW: 1.19.2: `@Summon`: Added `allayCanDuplicate [true/false]` and `allayDuplicateCooldown <ticks>`
* NEW: 1.19: `@Summon`: Added `zombiecanbreakdoors [true/false]` for setting if a zombie can break doors
* NEW: 1.20: Added new furnace fuels
* NEW: 1.20: Added new composting items
* NEW: 1.20: Added `special-recipes.decorated-pot` for disabling decorated pots recipes
* NEW: 1.20: Added `special-recipes.smithing.armor-trim` for disabling individual armor trim recipes
* NEW: 1.20: FlagHide: Added support for hiding armor trim with `armortrim` argument
* NEW: 1.20: Add support for smithing recipes with 3 ingredients

### v2.29.2
* FIX: `@LightLevel`: Fixed light level check for solid blocks, such as crafting tables
* NEW: `@LightLevel`: Allow multiple conditions where either passing is valid: `0-3 blocks, 12-15 sun`
* FIX: 1.15+ Fixed disabling special recipes

### v2.29.1
* FIX 1.18+ `@height` values below zero
* FIX 1.15-1.16 `special-recipes.repair:false` wasn't disabling recipes

### v2.29.0
* REMOVED Enchantment alias support. These have been outdated for a while and no longer are valid
  * If you're using any aliases, you will need to update them to match the enchantment names in `name index.html#enchantment`
  * You can safely delete `enchant aliases.yml` within your RecipeManager folder after updating any references in your recipes
* NEW: Added 1.19 fuels and compost items
* FIX: 1.13+ planks as fuel
* NEW: `@Summon`: Added `wardenanger <level>` for setting warden's anger towards the player
* NEW: `@Summon`: Added `frog <variant>` to set the frog's variant type
* NEW: `@Summon`: Added `goathornleft <true or false>` and `goathornright <true or false>` to set the goat's horns
* NEW: `@Summon`: Added `vindicatorjohnny` for setting johnny state of vindicators
* NEW: `@Summon`: Added `skeletonhorsetrapped` and `skeletonhorsetrappedticks <ticks>`
* FIX: HTML Docs: Correct sorting of tags in name index
* FIX: HTML Docs: Fix sorting of permissions in commands & permissions

### v2.28.0
* NEW FLAG: `@AxolotlBucketItem <variant>`
* NEW FLAG: `@StoreEnchantment`: Similar to `@ApplyEnchantment`, but for storing enchantments in enchanted books
* NEW: `@ApplyEnchantment`: Added `smallest` action to use the lowest enchantment
* FIX: `@NeedExp` error on load for `Player.getLevel()`
* FIX: `@Explode fail` for crafting recipes
* FIX: Updated grass_path to dirt_path in `item aliases.yml`
* FIX: Replaced temporary Smithing recipe support with new events
* FIX: `@IngredientCondition`: Add Smithing, Cartography, and Grindstone support
* FIX: `@CloneIngredient`: Add Smithing support
* FIX: `@ApplyEnchantment`: Add Smithing, Cartography, and Grindstone support
* FIX: `@LaunchFirework`, `@FireworkItem`: Fix max power of 127
* FIX: `@Hide`: Updated potioneffects argument description to be more accurate
* FIX: 1.13 and below: Fixed errors related to custom model data

### v2.27.1
* FIX: 1.13+ `@Summon`: Fixed error when summoning a non-breedable entity
* NEW: `@Summon`: Added support for item recipes within equipment slots
* NEW: `@Summon`: Added support for tropicalfishcolor, tropicalfishpattern, tropicalfishpatterncolor, and wanderingtraderdespawndelay
* FIX: NullPointerException when clicking on a smithing result on a newly placed smithing table
* FIX: /rmrecipes: Brewing recipes weren't printing
* FIX: /rmrecipes: Added `this` to tab complete
* NEW: Added /rmnext and /rmprev commands for easier /rmrecipes navigation
* FIX: Brewing recipes weren't using file flags
* NEW: `@KeepItem`: Added item recipe support for replace
* FIX: `@KeepItem`: Added anvil and brewing ingredient support
* FIX: Brewing Recipes: Improved support for `@IngredientCondition` including amount for the ingredient

### v2.27.0
* Fixed usage of `flag.ingredientconditions.nounbreakable` message
* NEW: `@Summon`: Added support for `axolotl <variant>`, `axolotlplayingdead`, `glowsquiddarkticksremaining <ticks>`, and `goatscreaming`
* NEW: `@Summon`: Added support for `freezeticks <ticks>` and `visualfire`
* FIX: `@Summon`: Remove entity type validation on adult, agelock, baby, nobreed, and pet
* FIX: `@DisplayResult`: Data wasn't being set correctly if the display result also had data
* NEW: Added 1.17 furnace fuels: azalea and flowering azalea
* NEW: Added 1.17 composting items and missed shroomlight for 1.16
* NEW: Added item recipe: Allows saving an item with flags to be used in other recipes/flags
* NEW: Added support for item recipes in any result or ingredient choice, including item recipes for chaining
* NEW: `@DisplayResult`: Added item recipe support
* NEW FLAG: `@BundleItem <item>[:data][:amount]`: Adds items to a bundle
  * `@BundleItem item:<name>` can be used to reference an item recipe
* NEW FLAG: `@CrossbowItem <item>[:data][:amount]`: Add charged projectiles to a crossbow
  * `@CrossbowItem item:<name>` can be used to reference an item recipe

### v2.26.0
* NEW FLAG: `@KnowledgeBookItem <namespace:key>, [...]`
* NEW FLAG: `@TropicalFishBucketItem <arguments> | [...]`
  * Arguments: `bodycolor <dyecolor>, pattern <pattern>, patterncolor <dyecolor>`
* NEW FLAG: `@MapItem <arguments>`
  * See your 'recipe flags.html' file for usage information
* NEW: /rmextract: Added a comment with namespace:key format to help with `@KnowledgeBookItem`
* NEW: `BookItem`: Added generation argument
* NEW: `@EnchantItem` and `@EnchantedBook`: Added `{rand #1-#2}` support
* NEW: `@ItemNBT`: Added full variable support
* NEW: Random numbers from `{rand}` can now be reused throughout a recipe instead of just the flag they are in with the `{rand #1}` format
* FIX: Random integers from `{rand #1-#2}` arguments weren't properly random
* FIX: Recipes with variables weren't processing the variables for simple recipes
* FIX: /rmcreaterecipe and /rmextract: Increased support for most flags
* FIX: /rmcreaterecipe: Improved `@ingredientcondition` support
* FIX: `@BannerItem`: Validate recipe has a banner item when parsing the recipe
* FIX: `@RepairCost`: Add option to customize prepareLore message and disable by default
* NEW: `@Summon`: Added support for arrowcooldown, arrowsinbody, invisible, persistent, and nocollision
* NEW: `@Summon`: Added support for bees: `beeanger <ticks>, beecannotenterhiveticks <ticks>, beehasnectar, beehasstung`
* FIX: `@Summon`: Guardian elder property wasn't being set
* FIX: `@ForChance`: Fix flag appending
* NEW: `@ForChance`: Allow any flag to be appended to a group, not just the same flag
* FIX: FuelRecipe: Fixed placing custom fuels
* FIX: FuelRecipe: Fixed an error when moving fuels out using a number key
* FIX: 1.13+ FuelRecipe: Fixed processing custom fuels
* NEW: `@Cooldown`: Cooldowns are now saved and reloaded on server start
* NEW: `@Cooldown`: Added day suffix
* NEW: Added 'save-frequency' config options: brewingstands, campfires, composters, cooldowns, and furnaces
  * Defaults to 30 minutes. Will only save if any data is changed in the given interval
* NEW: Block data is saved on /rmreload

### v2.25.0
* NOTE: Any result in a chance recipe (multiple results without using `@individualresults`) that fails will cause the recipe to no longer craft
  * This may break some recipes if you're relying on `@ingredientcondition` to define multiple resulting recipes instead of using `@individualresults`
* FIX: Removing recipes should now always be done before adding recipes
* FIX: Removing a furnace recipe and adding a simple recipe now works correctly
* FIX: 1.13+: Furnace recipe removal throwing ClassCastException
* FIX: Unexpected directive warning text no longer allows colors to wrap to the next line
* FIX: `@ItemName`: "Defaulting to set name in both locations" message no longer allows colors to wrap to the next line
* FIX: Recipes specifying air were not matching
* FIX: Simple recipes with chance weren't applying the chance
* FIX: Some grindstone recipes were not working
* FIX: Anvil renaming wasn't working after crafting a RecipeManager recipe
* FIX: `@ForChance`: Allow `@NoResult` flag
* FIX: `@FlagCooldown`: Fix functionality when used as a result flag
* FIX: `@IndividualResults`: Fix failure chance when shift clicking
* FIX: `@IndividualResults`: Can now be set as a file flag
* FIX: `@World`: Allowing multiple worlds was not working
* NEW: `@World`: Allow partial matching with ~
  * Examples: `@world ~hardcore`, `@world !~hardcore`
* FIX: messages.yml: Added need and mod flags' prepare lores, and renamed existing messages under craftmessage
* FIX: messages.yml: Added flag.repaircost.preparelore and flag.individualresults.preparelore.failchance
* FIX: Right click placing blocks into a grindstone, cartography table, or smithing table could stack higher than normal
* NEW: Improved Brewing support
  * Brewing recipes can be made with any ingredient now
  * Add custom brew lengths similar to furnace recipes
* REMOVED: 1.15+: /rmreload no longer requires confirmation. 1.12 - 1.14 will still require confirmation as they have known reload issues

### v2.24.1
* FIX: 1.16+: Blasting, Smoking, Campfire, and Stonecutting recipe removal
* FIX: /rmcreaterecipe will now export to "/RecipeManager/extracted/" similar to /rmextract
* FIX: `@ingredientcondition` error showing the incorrect recipe line
* FIX: Outdated config message was missing color on "NOTE"
* FIX: Error file color was wrapping to the next line

### v2.24.0
* NEW: 1.16.2: `@Hide`: Added support for hiding dyes from colored leather armors
* NEW: 1.16.1: New `@compass` flag to set compass locations
  * Format: `@compass` <worldName> <x> <y> <z> | [requiresLodestone]
* NEW: 1.16: Add Soul Campfire support to Campfire recipes
* NEW: 1.16: Add nether compost items
* NEW: 1.16: Smithing Recipe support
* NEW: Added new config "update-check.log-new-only" that defaults to true to reduce log spam
* FIX: 1.16: `@SkullOwner` not setting texture
* FIX: 1.13: Combine recipe book header
* FIX: 1.13: Recipe book ingredients for craft/combine recipes
* FIX: 1.12 and below: Fixed result flags in simple recipes
* FIX: `@ForChance`, `@ForDelay`, `@ForRepeat`: error message was using the child flag in errors
* FIX: `@ForDelay`, `@ForRepeat`: some flags weren't being allowed that should be
* FIX: Handle IllegalStateException if somehow adding a duplicate recipe, allowing for other recipes to still work
* FIX: Grindstone and Cartography recipes: Fixed crafting single items when in a stack
* FIX: Compost recipes
* FIX: "/RecipeManager/recipes/disabled/" has been moved and renamed to "/RecipeManager/extracted/"
  * This should auto convert if upgrading from an older version
  * This also means you can use  "/RecipeManager/recipes/disabled/" for your own recipes now
* FIX: Update checker being disabled shouldn't block /rmupdate from checking for updates

### v2.23.1
* FIX: 1.12 and below: Some recipes are giving NoClassDefFoundError
* FIX: 1.13+ Fixed recipe/override matching with recipes including air
* FIX: Allow `@Hide` to work in ingredients

### v2.23.0
* FIX/NEW: 1.13+ Re-added durability support in recipes.
    * Please check your recipes and modify them if necessary, mainly COMBINE recipes that rely on the amount shorthand
* NEW: 1.13+ Recipe formats that allow for patterns and ingredient flags (See "basic recipes.html")
    * Both formats are valid, so you should not need to change anything to a pattern format
    * Ingredients in the new formats can now have some flags set on them, such as `@name`, `@lore`, etc.
    * NOTE: `@ingredientcondition` will not work on ingredients that have flags, only use flags if you want an exact ingredient
    * Knowledge Book improvements to show ingredients and results with prepared flags
    * Furnace type recipes can have multiple ingredients with differing flags that all work for the same recipe (See "basic recipes.html")
* NEW: Recipes that do not require RecipeManager modification no longer are processed through RecipeManager events
* NEW: 1.13+ Knowledge Book groups can be defined for recipes
* NEW: 1.13+ Xp can be set for Furnace, Blast Furnace, Smoker, and Campfire recipes
* FIX: `@ItemNBT`: FlagType was using `@ItemName`
* FIX: `@IngredientCondition`: Multiple declarations will not override previous ones
* FIX: Anvil, Grindstone, Cartography recipes didn't prevent recipe from crafting in some cases
* FIX: Fixed extended potions not matching correctly causing a NullPointerException
* FIX: Buckets/Bottles being returned in crafting tables (from milk buckets, water buckets, lava buckets, honey bottles, and dragon breath)
* FIX: `@KeepItem` now supports stackable items, such as dirt, grass_block, etc. and gives items to the inventory if the stack doesn't match
* FIX: HTML Docs: Fixed composter levels description
* FIX: HTML Docs: Sort tags in name index

### v2.22.3
* FIX: 1.13+ Recipe Book air display for craft recipes
* FIX: 1.13+ Removal of furnace/blast furnace/smoker recipes with tags
* FIX: 1.8-1.11 Removal of furnace recipes
* FIX: 1.8 Fixed error about failing to register events for Anvil Recipes (which aren't supported on 1.8)
* FIX: 1.8-1.13 Fixed disable error for RMCampfires
* FIX: /rmdebug versioning issues and add RM and Bukkit versions

### v2.22.2
* FIX/NEW: HTML Docs: Fixed some item names and added visual inventories
* FIX: HTML Docs: `@ForDelay` and `@ForRepeat` examples were switched
* FIX: IllegalArgumentException when an ingredient has no choices, such as from a missing tag
* NEW: /rmdebug command for checking permissions and block data

### v2.22.1
* FIX: `@Biome` example references causing NoSuchFieldError before 1.13
* FIX: NoSuchMethodError for getStorageContents() in 1.8

### v2.22.0
* FIX: `@PotionItem`, `@PotionEffect`, `@SuspiciousStewItem`: Fixed potion and potion effect parsing
* FIX: `@MonsterSpawner`: Removed "Set min delay" message on parse
* FIX: HTML Docs: Made the nav consistent across static and generated files
* FIX: HTML Docs: Added links in 'recipe flags.html' for 'name index.html' for quicker referencing
* NEW: `@ApplyEnchantment`: Add 'anvil' action to increase levels by one if they match or take the max of the two
* NEW: `@ApplyEnchantment`: Add 'maxlevel' argument to restrict the enchantment levels
* NEW: `@CloneIngredient`: Add 'allowedtypes' argument to clone from ingredients other than the result type
* NEW FLAG: `@ForDelay`, alias 'delay': Run other flags after a delay
    * Format: `@ForDelay <delay> @<flag declaration>`
* NEW FLAG: `@ForRepeat`, alias 'repeat': Run other flags multiple times with an optional delay between them
    * Format: `@ForRepeat <times to repeat> <delay per repeat> @<flag declaration>`
* REMOVE: `@Cooldown`: Removed 'delay' alias in favor of `@ForDelay`
* REMOVE: `@SpawnParticle`: Removed 'delay' and 'repeat' arguments in favor of `@ForDelay` and `@ForRepeat`

### v2.21.0
* FIX/NEW: `@PotionEffect` flag updates:
    * Replaced 'morefx' with 'ambient'
    * Added options for particles and icon. Changed defaults to true
    * Removed chance. Use `@ForChance` flag to do the same thing
    * Changed default duration to 1.0
* FIX/NEW: `@PotionItem` flag updates:
    * Effect Type must now come first for custom effects
    * Replaced 'amplify' with 'amplifier'
    * Added options for particles and icon. Changed defaults to true
* FIX/NEW: `@Summon` updates:
    * Add Bee to 'adult', 'baby', 'agelock', and 'nobreed' options
    * Add 'parrot' option for setting a parrot's variant
    * Add 'pandamaingene' and 'pandahiddengene' option for setting a panda's genes
    * Add 'fox', 'foxcrouching', 'foxfirsttrustedplayer', 'foxsecondtrustedplayer', and 'foxsleeping' options
* FIX/NEW: `@MonsterSpawner` Add 'delay', 'mindelay', 'maxdelay', 'maxnearbyentities', 'playerrange', 'spawnrange', and 'spawncount' options
* NEW: `@IngredientCondition`, `@HoldItem`: Add potioneffect's particles and icon checks
* NEW: `@IngredientCondition`, `@HoldItem`: Add suspiciousstew potioneffect options
* FIX: `@IngredientCondition`, `@HoldItem`: Fix potioneffect functionality
* FIX: `@HoldItem`: Add missing potion, potioneffect, and banner documentation
* FIX: `@CloneIngredient`: Add support for Anvils, Cartography Tables, and Grindstones
* FIX: `@CloneIngredient`: Using allmeta with a data attribute will respect data adjustments
* NEW FLAG: `@SuspiciousStewItem`: Adds potion effects to a suspicious stew

### v2.20.0
* NEW: Grindstone and Cartography Table recipe support
    * See 'basic recipes.html' for format
    * Backup and update your 'config.yml' for special recipe configurations similar to anvils
* FIX: `@ingredientcondition` and `@getrecipebook` now shows recipe file and line number for registration errors/warnings
* FIX: NoSuchElementException after using /rmreload when clear-recipes set to true
* FIX: When an `@remove` recipe cannot be found, the recipe is no longer added
* FIX: 1.15 `@remove` functionality

### v2.19.0
* FIX: color-console config changes didn't take effect early enough for setting reloads
* FIX: settings no longer call init twice on first load
* FIX: update boat and scaffolding smelt times for 1.15
* FIX: `@KeepItem` functionality
* FIX: `@ApplyEnchantment`: Add support for anvil and brew recipes
* FIX: Config for special-recipes.anvil
    * Materials and enchantment lists were not clearing on /rmreload
    * "repair-material.enabled: false" material list was broken
    * enchant functionality wasn't working correctly
* NEW: 1.15 support

### v2.18.1
* FIX: Missing messages fail more gracefully
* FIX: Error on /rmreload for missing/removed recipes
* FIX: Basic anvil support for 1.9 and 1.10
* FIX: Vault Permission and Economy hooks may not have been detected correctly
* NEW: Add Block/Item to Materials in 'name index.html' for 1.12+

### v2.18.0
* NEW: Add vanilla fuel and compost recipes to /rmextract
* NEW: Compost recipes with full flag, multi-result, and hopper support
    * Format: <ingredient> % [chance per level] % [levels], where [chance per level] is >0 and <=100 and [levels] >0 and <=7
    * Both [chance per level] and [levels] can support decimal inputs (meaning you can do fractional levels)
    * While the composter is being used for a recipe, it will be locked to that recipe (or the same result from another recipe), so no conflicting recipes can be made
    * Vanilla compost recipes can be overridden/removed
* NEW: Anvil recipes with full flag and multi-result support
    * Format: <material[, ...]> + <material[, ...]> % [experience levels] % [allowrename or true] % [anvil damage chance]
* NEW: Added special recipes for disabling vanilla anvil behavior
    * anvil.combine-item: ability to combine items of the same type to repair or improve enchantments
    * anvil.enchant: ability to enchant items with books
    * anvil.repair-material: ability to repair using a material ingredient
    * anvil.renaming: ability to rename items in an anvil
    * enabled option: enable/disable the functionality
    * material option: materials to allow/disallow. Ex: iron_sword, wooden_sword. Supports a:<aliasname> and tag:<tagname>
    * enchant option (anvil.enchant only): enchantments to allow/disallow. Ex: sweeping_edge, durability:1, damage_all:1-3

### v2.17.2
* FIX: `@IngredientCondition`, `@HoldItem`, `@CloneIngredient` enchant and bookenchant conditions
* FIX: Bug with multi-result recipes (without set percentages) not actually adding up to their specified amounts

### v2.17.1
* FIX: Custom smelting times in 1.12 and older
* FIX: Smelting recipes with custom fuel wasn't checking the last fuel correctly
* FIX: `@SkullOwner` and `@NBT` will be called first to avoid overwriting meta. These flags no longer need to be the first flag
* FIX: `@NoResult` shift clicking was still giving results
* FIX: `@PotionItem`: base potion effect wasn't being set correctly. Conditions no longer care about order
* FIX/NEW: `@PotionItem`: added base potion color support. Format: "color <r> <g> <b>"
* FIX: `@IngredientCondition`, `@HoldItem`, `@CloneIngredient`: Fixed setting rgb and rgb range in color condition
* FIX: /rmfinditem throwing errors due to old material ids
* FIX/NEW: Add tab complete to /rmfinditem, /rmextract, /rmgetbook, /rmrecipes

### v2.17.0
* FIX: Sorted 'recipe flags.html'
* FIX: `@SkullOwner`: No longer needs data 3 in 1.13 and 1.14
* NEW: `@SkullOwner`: Added aliases 'skull' and 'head'
* NEW: `@SkullOwner`: Added texture <base64> option
* NEW: `@ItemNBT <nbtRaw>`: Sets raw nbt data on the result.
    * See 'recipe flags.html' before using
* NEW: Add tag support to ingredients in recipes (Use 'tag:' or 't:' before the tag name)
    * Tags are vanilla or datapack collections of materials (Tag names can be found in 'name index.html')
    * Example: t:oak_logs (or t:minecraft:oak_logs) would be equivalent to "OAK_WOOD, STRIPPED_OAK_WOOD, STRIPPED_OAK_LOG, OAK_LOG"
* NEW: Added 'choice aliases.yml' to create custom ingredient choice lists similar to tags
    * Instead of t: or tag:, use a: or alias: for these
    * Choice aliases can chain tags or previously defined aliases
* FIX/NEW: 'name index.html': Added Tags, alphabetize lists, and improved formatting
* FIX/NEW: Improved special recipe support. Added banner-duplicate, firework-star-fade, and suspicious-stew to config.yml

### v2.16.1
* FIX: nometa condition check on pre 1.14 servers (error with customModelData)
* FIX: nometa condition check on pre 1.12 servers (error with localizedname)
* FIX: noname and nolocalizedname condition checks
* FIX: update 'item aliases.yml' to include names for id conversions to prevent id being used in messages
* FIX: `@CustomModelData` wasn't being loaded correctly
* FIX: `@IndividualResults` wasn't setting names/lores correctly and now supports shift click crafting
* FIX: Multi-result recipes flag support and now supports shift click crafting
    * This should be the last of recipes being disabled from shift clicking
* FIX: Improved shift click crafting overall. If the inventory doesn't have room, results will be dropped on the ground
* FIX: Move recipe error messages from player messages to the item lore and add clearer messages
* FIX: `@Override`, `@Remove`, `@Restrict`: Fixed shaped recipe matching for 1.12 and below
* FIX: `@Secret`: Fixed secret result not showing when set on recipe or all results. Added singular message variant
* NEW: `@ItemName`, `@ItemLore`, `@Command`, `@Message`, `@Broadcast`: Added ability to reuse random numbers
    * {rand n} reuse a random output, where n is the nth {rand} used excluding this format
* Misc. cleanup and bugfixes

### v2.16.0
* FIX: data/durability not being set in 1.13/1.14 shaped/shapeless recipes
* FIX: `@ItemName`, `@ItemLore`, `@Command`, `@Message`, `@Broadcast`: Fixed multiple {x}, {y}, or {z} variables from crashing the server
* FIX/NEW: `@IngredientCondition`, `@HoldItem`, `@CloneIngredient`: Double pipes '||' can be used in arguments and will be replaced by a single '|' to better support regex
* FIX: Campfire and stonecutting index weren't being removed correctly
    * Example: `@ingredientcondition iron_nugget | lore regex:one||two` will now correctly match one or two
* NEW: `@ItemName`, `@ItemLore`, `@Command`, `@Message`, `@Broadcast`: Added ability to output random numbers.
    * {rand #1-#2} Outputs a random integer between #1 and #2. Example: {rand 5-10} will output an integer from 5-10
    * {rand #1-#2, #3} Outputs a random number between #1 and #2, with decimal places of #3. Example: {rand 1.5-2.5, 2} will output a number from 1.50 to 2.50
* NEW FLAG: `@RepairCost` - Sets the default item repair cost
* NEW FLAG: `@LocalizedName` - Works the same as `@ItemName`, but sets the localized name instead
* NEW FLAG: `@CustomModelData` - Sets the custom model data that can be used in conjuction with data packs for custom textures
* NEW: `@IngredientCondition`, `@HoldItem` - Add conditions for localizedname and custommodeldata
* NEW: `@ItemName`, `@ItemLore` - Added 'display' and 'result' parameters to show in only one location. Defaults to showing in both with no parameter
* NEW: `@ItemName`, `@ItemLore` - Can now be added in the recipe section as well as results
    * In `@ItemName`, this acts as a default and any result `@ItemNames` will override, unless only setting one of the 'display' or 'result' parameters
    * In `@ItemLore`, this will add lore lines to the beginning of all results

### v2.15.0
* 1.13 & 1.14 Update
* Recipes now support MaterialChoice ingredients
    * Example: dirt,grass + dirt,grass would allow for dirt or grass to be used in either slot
    * Try rmextract to see examples of existing recipes or see 'basic recipes.html' for updated descriptions
* New "campfire" recipes
    * Single ingredient and result
* New "blasting" and "smoking" recipes
    * Work exactly the same as furnaces/smelt recipes
* New "stonecutting" recipes
    * Can create many recipes with the same ingredient, as long as there are different results
    * No Flag support yet
* `@NoResult`: Shift clicking will no longer give results
* Removed need for "Recipe is special, shift-clicking will only craft it once" restriction
* New Flag: `@ItemAttribute`
    * Add attributes such as max_health and armor to your results

### v2.14.1
* `@IngredientCondition`: Fixed issue when unbreakable was not set

### v2.14.0
* Fixed furnace smelt times not updating
* Added support for relative coordinates in x,y,z variables {x-1},{y+7},{z+12}
* `@Inventory`: Added multiple title
* `@IngredientCondition` and `@HoldItem`: Added unbreakable option
* `@SkullOwner`: Added uuid support (only checks the offline player storage)
* Fixed NamespacedKey warnings for PaperSpigot

### v2.13.2
* `@ItemLore`: Allow empty lines without needing colors
* `@ItemLore`, `@ItemName`, `@Message`, `@Broadcast`, `@BookItem`, and more
    * Added exact string option using quotes
* `@Summon`: Fixed spread option
* Fixed messages set to false still sending a message

### v2.13.1
* `@SpawnParticle`: Only trigger once per shift click
* `@LaunchFirework`: Switch to trigger once per shift click instead of preventing shift click
* `@ForPermission`: Fixed flags not working when they haven't been used yet.
* Fixed shapeless recipes not always matching existing recipes

### v2.13.0
* Fixed fail chances not removing ingredients on `@IndividualResults` recipes
* Fixed null names in recipe books. Use first ingredientcondition lore line as an assumed fallback name
* Fixed chance recipes not showing results in MC 1.11
* `@Summon`: Updated and fixed setting items on entities
* `@Summon`: Added better backwards support for equipment
* `@Inventory`: Handle colored titles
* New Flag: `@ApplyEnchantment`
    * Copies enchantments from ingredients to the result

### v2.12.0
* Removed Metrics
* Fixed custom potion error
* Fixed a crash when using /rmcreaterecipe with no ingredients
* Improvements to RecipeBooks
* New Flag: `@SpawnParticle`

### v2.11.0
* Added 1.12 Support
    * Please check your removal recipes against a fresh /rmextract as some have changed
* To avoid "recipe not found, removing" errors and subsequent loss of advancement connections to managed recipes, give them all names. (e.g. craft <name>)
    * This is especially true for `@override's`, but is also true if you intend to leverage advancements
* Fixed Sweeping Edge enchantment alias (again)
* Fixed errors on shutdown
* `@Summon`: Added support for invulnerable and noai (disabling ai)

### v2.10.0
* New Flag: `@MonsterSpawner` (Requires Bukkit/Spigot build of 1/10/2017 or newer)
    * Works similarly to `@SpawnEgg`
* Fixed `@forchance` error message showing FlagDescriptor instead of the actual flag name
* Removed double `@` in `@DisplayResult` description
* `@Remove`: Fixed craft recipes with air in them
* Fixed Sweeping Edge enchantment alias

### v2.9
* Furnace recipes: Fixed fuel recipes preventing other fuel recipes
* Fix incorrect result after different failed recipe
* Fix case issues with `@IngredientCondition` and `@HoldItem`
* Fixed colors in craft result denied messages
* New Flag: `@ItemUnbreakable` or `@Unbreakable`
    * Sets the unbreakable state of items (Requires 1.11)
* Added/updated special recipes for 1.11
* Added enchant aliases for Binding Curse, Vanishing Curse, and Sweeping Edge
* Added 1.11 fuel recipes
* Added `@SpawnEgg` flag (Requires 1.11)
* `@IngredientCondition`: Added spawnegg option (Requires 1.11)

### v2.8
* Dropped support for Java 6
* Fixed potions in 1.9.x
* Fixed Shield Banner recipe
* Removed the need for server lookups of UUIDs
* Fix recipe files starting with UTF8 BOM
* `@HoldItem`: Added slot attribute to support offhand, armor, and inventory
    * See your 'recipe flags.html' file for usage information
* `@CloneIngredient`: Added lore options
    * lore                 = copies all the custom item lore/description unless conditions are added to copy specific lines
    * lore [number]        = copies only the lore on line number. More lore conditions can be added to copy more lines
    * lore [text]          = copies any lore lines that contain the text
    * lore [regex:pattern] = copies any lore lines that match the regex pattern
* `@GameMode`: Fixed duplicate calls not clearing previous gamemodes
* `@NoResult`: Fixed result not clearing as it should
* `@FireworkItem`: Fixed effect parsing and power not being set
* `@IngredientCondition`: Added potion, potioneffect, and banner conditions
* Many more misc. fixes

### v2.7.3
* Improved compatibility for 1.8.x and 1.7.x versions.
* Added Frost Walker and Mending enchant aliases

### v2.7.2
* Minecraft 1.9 compatibility
    * Should still be backwards compatible to some 1.7.x builds, but may need to drop support for 1.8 and down soon

### v2.7.1
* Fixed ingredient subtracting issues
* Prevent chance recipes from being cheated around by holding an item of the type you want
* Fix shift clicking for chance recipes
* Fixed beta status not showing up in console
* Update material.fail config to default to BARRIER as FIRE no longer displays

### v2.7
* Fixed recipe parsing with material name/aliases starting with a directive (brew/craft/combine/smelt/fuel)
* Added 'item datas.yml' to override max data/durability values for materials
* Fixed potion not being defined error on brewing recipes
* Better handling of invalid Bukkit recipes
* `@Remove`: Fixed removing combine recipes
* `@ingredientcondition`: Improved amount handling
* `@HoldItem`: Added conditions similar to `@IngredientCondition`
* `@secret`: Removed conflicting alias 'hide'
* New flag `@NoResult`
    * Prevents the result item from being crafted
    * Useful when giving items through `@command` or providing non-item results, such as `@modexp`
* Furnace improvements:
    * Fixed Furnace/Brewing save data from getting erased on a restart
    * Fixed shift clicking an ingredient into a furnace not setting the permission owner
    * Fixed some flags getting called before craft in furnaces
* /rmcreaterecipe improvements:
    * Collapse recipes down to the smallest craft size
    * Added combine variant
    * Fixed amount being added to ingredients instead of `@ingredientcondition`
    * Clean up unneeded data and amount outputs

### v2.6.4
* Only call furnace fuel events for custom fuels

### v2.6.3
* Added `@inventory` flag
    * `@inventory crafting` // Player crafting menu
    * `@inventory workbench` // Must use a crafting table
* Improved /rmgetbook's command usage formatting
* Fixed /rmcheck command. Test recipe files for errors without reloading them
* `@summon`: Fixed adding armor and weapons to creatures

### v2.6.2
* `@Permission`: Fixed multiple permission requirements
    * Player must have any permission listed, not all permissions
    * Player must have no permissions marked with !
* `@KeepItem`: Fixed shift clicking when the item should be destroyed
* Update checks are now asynchronous. Should no longer stall the server if update site is down or having issues

### v2.6.1
* `@IndividualResults` Fixed all results failing when no chances provided
* Fixed results not updating correctly on recipes with multiple results
* `@Explode`: Added fuel property to control when explosions occur in fuel recipes
    * `@Explode fuel end` // Will cause the explosion to happen after the fuel runs out
    * `@Explode fuel random` // Will cause the explosion to happen sometime randomly before the fuel runs out
* API: Added RecipeManagerFuelBurnEndEvent and RecipeManagerFuelBurnRandomEvent

### v2.6
* `@IngredientCondition`: Added 'bookenchant' and 'nobookenchant'
* Added `@Hide` flag
    * `@hide attributes`     // Hide attributes like Damage
    * `@hide destroys`       // Hide what the item can break/destroy
    * `@hide enchants`       // Hide enchants
    * `@hide placedon`       // Hide where this item can be placed on
    * `@hide potioneffects`  // Hide potion effects on this item
    * `@hide unbreakable`    // Hide the unbreakable state
    * `@hide all`            // Hides everything
    * `@hide placedon | destroys` // Removes placedon line and break/destroy lines
* `@Enchant` and `@EnchantBook` Added 'remove' option for cloned ingredients
* Added `@BannerItem` flag (See 'recipe flags.html' for patterns and colors)
    * `@banner black`
    * `@banner red | circle blue | skull yellow`
    * `@banner green | half_horizontal yellow | circle orange`
* Added `@IndividualResults` flag
    * Allows multi-result recipes to have individual result outcomes, instead of a chance based outcome between all results.
    * With this flag set, the first valid recipe found will be the one crafted
    * Replaces `@displayresult first` in most cases
* Fixed display result on a failed craft

### v2.5.1
* Fixed shift clicking vanilla ingredients into furnaces
* Fix shift click behavior for result ingredientcondition amounts
* Recipe Books improvements:
    * Removed strange characters at the end
    * Show the smallest shape size possible for recipes

### v2.5
* `@IngredientCondition`: Added 'needed' parameter which allows you to match against less than all ingredients of a type
    * For Example:
    * `@ingredientcondition sugar | nometa | needed 2` // Recipe will require 2 vanilla sugar
    * `@ingredientcondition sugar | name &fFlour | needed 3` // Also require 3 sugars named &fFlour
* Added recipe-comment-characters config option for customizing inline comments
* `@ForChance`: Fixed a couple issues when using groups
* Brewing updates
    * Fixed multiple brewing recipes and multiple results
    * Added a note about this being an experimental feature. Expect bugs
* Added /rmcreaterecipe command
    * Creates a recipe with the left 9 items in the inventory being the ingredients and the selected hotbar slot as the result
    * Early testing, does not support all ingredientconditions/flags yet
* Removed /rmextractrecipe in favor of /rmcreaterecipe
* Improved furnace permissions. Now updates the fueler when adding fuel or ingredients.
* Fixed shift click stacking in furnaces (prevents items from stacking higher than their stack should)
* `@permission`: Fixed an issue when the crafter didn't have any valid permission
* Prevent the loading of recipes from getting stuck when invalid items are found

### v2.4
* Fixed some shift clicking issues with furnaces
* Fixed rmcheck command permissions
* Added basic brewing recipe support (See 'basic recipes.html' for usage)
* Prevent vanilla recipes from getting the RecipeManager id lore
* Fix 1.7.x support
* `@Restrict`: Remove override warnings
* `@SkullOwner`: Give the correct skull for Spigot (will be untextured before crafted)

### v2.3.4
* `@LightLevel` Added a note about using light from blocks with furnaces as they provide light to themselves
* Fixed vanilla repairing after /rmreload
* Fixed vanilla banner recipes after /rmreload
* Fixed vanilla book cloning after /rmreload
* Fixed vanilla map cloning after /rmreload
* No longer exports any special recipes
* Added special-recipes.book-cloning config
* Added special-recipes.banner config
* Fixed furnace-shift-click click mode when right clicking to fuel slot
* Fixed vanilla fuels not re-registering on /rmreload
* More furnace tweaks to prevent getting around recipe smelt times and improve fuel handling

### v2.3.3
* Fixed a UUID issue which caused issues with furnace/fuel recipes
* Fixed doubled up lores for smelting recipes
* Fixed custom fuels being used up when clicking on the fuel slot
* Misc. Furnace tweaks
* `@Broadcast`: Fixed colors not being set
* `@AddToBook`: Names no longer required to be all lowercase

### v2.3.2
* Fixed multi-result recipes only showing the first item
* Only perform UUID lookups when actually needed (Huge performance improvement, especially for furnaces)
* Fixed low fuel items such as sticks resetting the furnace cook time
* Fixed smelting recipes only ever smelting one item
* Fixed smelting recipes with a chance to fail
* Fixed default smelting time for custom smelting recipes

### v2.3.1
* Added `@temperature` flag
    * `@temperature < 0` // Must be in an icy biome
    * `@temperature 1.2` // Must be in a hot biome, such as a desert or savanna
    * `@temp >= 1.2` // Equivalent to the above example
    * `@temp >= .15, <= .95` // Must be within a temperature where it can rain
* `@FlagSummon`: Add better support for horses
    * Can now use saddle, horse, horsecolor, horsestyle, jumpstrength, haschest as well as a few other animal options
* `@FlagSummon`: Added adult option for animals and villagers (works similar to baby, but forces creature to be an adult)
* `@FlagSummon`: Added support for Rabbits
* `@FlagSummon`: Added elder option for Guardians
* `@IngredientCondition`: Added more data options
    * all: Flips the data check to allow all data values instead of none initially
    * vanilla: Only allow data values within the vanilla ranges
    * new: Equivalent to 0, or an undamaged item
    * damaged: On weapons and armor, this is everything within vanilla limits that is considered a damaged item
* `@IngredientCondition`: Fixed amount option
* Reset display item when item cannot be crafted
* Updated Bukkit doc links to spigot's versions for updated 1.8 stuff

### v2.3
* Fixed furnace recipe fuel restrictions. It will only restrict the recipe it is applied to now
* Fixed an issue with `@ingredientcondition's` nometa (plus variants) causing exceptions with certain recipes
* Removed "Extra object is not an itemstack!" for smelting recipes
* Added 1.8 banners, fences, and gates to vanilla fuel recipes
* Added Depth Strider to Enchantment aliases
* Added 1.8 items to item aliases
* Fixed colors not being set on smelting recipes
* Fixed setting messages to false throwing an error.
* Fix player skulls not showing the correct texture in the inventory
* Updated `@needxp` flag to handle 1.8 or 1.7 models of xp
* Added {playerexp} variable to `@needxp`
* Fixed `@world` flag

### v2.2.3
* Fixed `@remove` not always working
* Show `@needmoney`, `@modmoney`, `@needlevel`, `@modlevel`, `@needexp`, `@modexp` on recipe results
* Fixed `@needlevel`
    * `@needlevel 1` // Requires a minimum level of 1
    * `@needlevel 5-5` // Requires exactly level 5
* Fixed `@needexp` not working if exp was equal to the mininum exp
* Upgraded `@needexp` to work with exact exp similar to `@needlevel`
* Add support for `@name`, `@lore`, and `@enchant` in smelting recipes
* Fixed nometa + variants not overriding name, lore, etc. correctly
* Removed some reason messages as they aren't necessary anymore.

### v2.2.2
* Add support for leather color when extracting recipes.
* Don't try to load RecipeManager last as it breaks reload. If other plugins want their recipes to work with RecipeManager, they can use depend or softdepend
* Add new conditions to `@ingredientcondition` flag
    * noname: requires the ingredient not have a name set.
    * nolore: requires the ingredient not have a lore set.
    * noenchant: requires the ingredient not have any enchants.
    * nocolor: requires the ingredient not have a color set (leather armor only)
    * nometa: equivalent to noenchant | noname | nolore | nocolor
* Fixed an `@ingredientcondition` bug with recipes having two or more ingredients and not having conditions on all of them
* Fixed a potential issue with invalid enchants
* Fixed smelting/fuel issues when reloading

### v2.2.1
* Fixed an error where the plugin would disable if Vault wasn't installed.
* Make RecipeManager load last to catch other plugins adding recipes.
* Fixed an error where Fuel recipes were not breaking before a `@message` flag.
* Re-added furnace flag functionality to fix `@forchance`
* Fixed an error when adding custom fuels with no ingredients in the furnace.

### v2.2
* Updated Metrics to handle old getOnlinePlayers (provides backward support for old versions of CraftBukkit)
* Fixed spaces breaking recipe names
* Fixed `@needmoney` not working with only the min value set
* Fixed an Updater bug when a player joined too early
* Added material.fail config to allow changing the Material shown when a recipe fails
* Added material.secret config to allow changing the Material shown for secret recipes
* Added material.multiple-results config to allow changing the Material shown for recipes with multiple results
* Fixed parsing of flags with spaces in front of the `@` symbol
* Fixed enchantments and improved recipe extraction for /rmextract
* Added /rmextractrecipe (Alias: /extractrecipe) to extract a recipe for the item the player is currently holding.
* Allow multiple lore checks in `@ingredientcondition`
* Added disable-override-warnings config to disable 'Recipe already created by <plugin>, recipe overwritten!' warnings
* Reworked furnace smelt/fuel recipes
    * Significant performance improvements
    * Removes issue with furnaces smelting multiple items
    * Removed furnace-ticks config
    * Support for data values in furnace recipes
    * Some flags may not work right now with furnaces
* Fixed files not detecting the version correctly
* Misc. spelling fixes

### v2.1.1
* Update user agent for RecipeManager Updater
* Better version detection
* Fixed settings not getting initialized soon enough.
* Default furnace ticks to 1 if invalid
* Fixed /rmreload not reloading recipes correctly.
* Removed the clear-recipes message on load
* Fixed files asking to be deleted when they shouldn't be (This was probably just a version mismatch due to approval delay)
* Removed dark alias from spruce as dark oak gets that alias now.

### v2.1
(THDigi)
* Fixed `@ingredientcondition's` lore argument with 'regex:' prefix checking name's regex pattern instead, giving an error if name arg not defined
* Also fixed a probably confusing example.
* Fixed shutdown error when metrics is disabled in config.yml.
* Fixed possible issues with reload and re-enabling metrics.
* Possible fix for errors when loading furnaces without inventories.
* Fixed update checker printing new 'null' version to joining OPs when it is disabled;
* Added permission for update notify receivers: 'recipemanager.command.rmupdate'
* Added check for connection to print a user-friendly error if it can't establish a connection.
* API: Added clearFlags() method for Flaggable interface (use by ItemResult and BaseRecipe).
* Added check to ensure recipe is complete before generating the Bukkit recipe.
* Removed 'item' alias for 'rmfinditem' to prevent collisions with more important commands from other plugins
* Possible fix for a startup error that triggers if you have a plugin with no package name, just the main class.
* Fixed `@keepitem` flag not working if set on results
* Fixed `@ingredientcondition` not subtracting ingredients if used on results
  (Haveric)
* Changed Integer to Double as getLastDamage() now returns a Double.
* Fix DURABILITY enchantment returning a null EnchantmentTarget. Default any null EnchantmentTargets to ALL.
* Remove Debug message from furnaces.
* Updated to Metrics R7
* Updated Metrics to work with updated getOnlinePlayers
* Update to UUID support with Vault
* Fixed a potential bug with colors not being supported in messages
* Fixed regex for `@cloneingredient` data and `@cloneingredient` amount
* Fixed `@enchantedbook` flag
* Prevent unnecessary warning when using `@remove` in crafting recipes
* Fixed `@override` causing certain recipe to throw errors.
* Updated vanilla fuels
* Updated how RecipeManager checks for updates to conform to the ServerMods API.

### v2.0

* Added hopper handling for furnaces;
* Changed/fixed `@ingredientcondition` flag:
    * Fixed it and other flags glitching first result when used in results;
    * Fixed not blocking furnace ingredients from being placed;
    * Fixed it not comparing names/lores properly when colored due to unconverted colors;
    * Changed to require a 'regex:' prefix for name and lore arguments;
    * Added warning for invalid regex patterns along with a tip on testing regex patterns online;
    * Added warning for ingredients that were not found in the recipe;
* Added name/lore/enchant options for in-line items
    * Useful for `@displayitem` in particular but can be used in most other item definitions that accept limitless items;
    * Syntax is as follows: <material>:[data]:[amount] ; name <name> ; lore <line> ; enchant <enchant> [level];
    * Of course, except material, all arguments are optional and lore argument can be used more than once;
* Fixed smelt&fuel recipes being usable in already burning furnaces;
* Added `@displayresult` flag to define a custom item that will be displayed as result with alternative 'first' argument to define the first one (useful with `@ingredientcondition`);
* Added example recipe of both previous flags in action in the 'advanced recipes.html' file;
* Fixed update notifier always printing there's a new version only for joining OPs;
* Changed update notifier from printing to OPs to printing to players having 'recipemanager.command.rmupdate' permission;
* Fixed overwritten recipes ignoring their flags because the original ones weren't fully removed;
* Edited 'basic recipes.html' to fix some syntaxes to make more sense and some typos;
* Removed 'recipemanager.skipflags.<flag>' permission nodes and replaced them with 'recipemanager.flag.<flag>' that are default true, due to people giving themselves all possible permissions;
* (RMAPI) Cleaned up events code by removing some, renaming some, but it makes more sense now;
