package haveric.recipeManager.flag.flags.any;

import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.RMCVanilla;
import haveric.recipeManager.common.util.ParseBit;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.conditions.ConditionsIngredient;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.tools.*;
import org.bukkit.DyeColor;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagIngredientCondition extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.INGREDIENT_CONDITION;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <item> | <conditions>", };
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
            "Adds conditions for individual ingredients like ranged data values, enchantments or using stacks.",
            "This flag can be called more than once to add more ingredients with conditions.",
            "",
            "The <item> argument must be an item that is in the recipe, 'material:data' format.",
            "If you're planning to add ranged data values the data value must be the wildcard '*' or not set at all in order to work.",
            "",
            "For <conditions> argument you must specify at least one condition.",
            "Conditions must be separated by | and can be specified in any order.",
            "Condition list:",
            "",
            "  data <[!][&]num or min-max or all or vanilla or damaged or new>, [...]",
            "    Condition for data/damage/durability, as argument you can specify data values separated by , character.",
            "    One number is required, you can add another number separated by - character to make a number range.",
            "    Additionally instead of the number you can specify 'item:data' to use the named data value.",
            "    Special data values:",
            "      all: Flips the data check to allow all data values instead of none initially.",
            "      vanilla: Only allow data values within the vanilla ranges.",
            "      new: Equivalent to 0, or an undamaged item.",
            "      damaged: On weapons and armor, this is everything within vanilla limits that is considered a damaged item.",
            "    Prefixing with '&' would make a bitwise operation on the data value.",
            "    Prefixing with '!' would reverse the statement's meaning making it not work with the value specified.",
            "    Optionally you can add more data conditions separated by ',' that the ingredient must match against one to proceed.",
            "    Defaults to the equivalent of !all.",

            "",
            "  enchant <name> [[!]num or min-max], [...]",
            "    Condition for applied enchantments (not stored in books).",
            "    This argument can be used more than once to add more enchantments as conditions.",
            "    The name must be an enchantment name, see " + Files.getNameIndexHashLink("enchantment"),
            "    The 2nd argument is the levels, it's optional",
            "    A number can be used as level to set that level as requirement.",
            "    You can also use 'max' to use the max supported level for that enchantment.",
            "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
            "    Prefixing with '!' would ban the level or level range.",
            "",
            "  noenchant or !enchant",
            "    Ingredient must have no enchantment",
            "    Overrides enchant condition if set",
            "",
            "  bookenchant <name> [[!]num or min-max], [...]",
            "    Condition for book enchantments (not applied enchantments)",
            "    This argument can be used more than once to add more enchantments as conditions.",
            "    The name must be an enchantment name, see " + Files.getNameIndexHashLink("enchantment"),
            "    The 2nd argument is the levels, it's optional",
            "    A number can be used as level to set that level as requirement.",
            "    You can also use 'max' to use the max supported level for that enchantment.",
            "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
            "    Prefixing with '!' would ban the level or level range.",
            "",
            "  nobookenchant or !bookenchant",
            "    Ingredient must have no book enchantment",
            "    Overrides bookenchant condition if set",
            "",
            "  amount <num>                     = stack amount, this will also subtract from the ingredient when crafted!",
            "",
            "  name <text or regex:pattern>     = check the display name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  noname or !name",
            "    Ingredient must have no/default display name",
            "    Overrides name condition if set",
            "",
            "  itemname <text or regex:pattern>     = check the item name against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  noitemname or !itemname",
            "    Ingredient must have no/default item name",
            "    Overrides itemname condition if set",
            "",
            "  localizedname <text or regex:pattern>     = check the item's localizedname against exact text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  nolocalizedname or !localizedname",
            "    Ingredient must have no localizedname",
            "    Overrides localizedname condition if set",
            "",
            "  lore <text or regex:pattern>     = checks each lore line for a specific text or if prefixed with 'regex:' it will check for a regex pattern.",
            "    Note for regex:pattern           Escape for '|' is a double '||'. Any double pipes will be converted back to single pipes for regex parsing.",
            "  nolore or !lore",
            "    Ingredient must have no lore",
            "    Overrides lore condition if set",
            "",
            "  color <colorname or R,G,B>       = only works for leather armor, checks color",
            "                                     values can be individual values or ranged separated by - char or you can use a color name constant, see " + Files.getNameIndexHashLink("dyecolor"),
            "",
            "  nocolor or !color",
            "    Only works for leather armor",
            "    Ingredient must have default/vanilla color",
            "    Overrides color condition if set",
            "",
            "  unbreakable = Ingredient must have the unbreakable flag",
            "  nounbreakable or !unbreakable = Ingredient must not have the unbreakable flag",
            "",
            "  hidetooltip = Ingredient must have the hidetooltip flag",
            "  nohidetooltip or !hidetooltip = Ingredient must not have the hidetooltip flag",
            "",
            "  enchantmentglint = Ingredient must have the enchantmentglint flag",
            "  noenchantmentglint or !enchantmentglint = Ingredient must not have the enchantmentglint flag",
            "",
            "  custommodeldata = Ingredient must have custom model data",
            "  nocustommodeldata or !custommodeldata = Ingredient must not have custom model data",
            "",
            "  nometa or !meta",
            "    Ingredient must have no metadata (enchants, bookenchants, name, itemname, lore, color, unbreakable, hidetooltip, enchantmentglint, localizedname, custommodeldata)",
            "    Overrides enchant, name, itemname, lore, color, unbreakable, hidetooltip, enchantmentglint, localizedname, custommodeldata conditions if set",
            "    Equivalent to noenchant | nobookenchant | noname | noitemname | nolore | nocolor | nounbreakable | nohidetooltip | noenchantmentglint | nolocalizedname | nocustommodeldata",
            "",
            "  needed <num>",
            "    Sets the number of ingredients that need to match this condition",
            "    Defaults to all of the ingredientcondition type",
            "",
            "  potion <condition>, [...]",
            "    type &lt;potiontype&gt;      = Type of potion, see " + Files.getNameIndexHashLink("potiontype"),
            "",
            "  potioneffect <condition>, [...]",
            "    type &lt;effecttype&gt;         = Type of potion effect, see " + Files.getNameIndexHashLink("potioneffect"),
            "    duration <num or min-max> = Duration of the potion effect in seconds, default 1 (does not work on HEAL and HARM)",
            "    amplify <num or min-max>  = Amplify the effects of the potion, default 0 (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
            "    ambient or !ambient       = Check effect's extra visual particles setting",
            "    particles or !particles   = Check effect's particles setting",};

            if (Version.has1_13BasicSupport()) {
                description = ObjectArrays.concat(description, new String[]{
                    "    icon or !icon             = Check effect's icon setting",
                }, String.class);
            }

            if (Supports.suspiciousStewMeta()) {
                description = ObjectArrays.concat(description, new String[]{
                    "",
                    "  suspiciousstew <condition>, [...]",
                    "    type &lt;effecttype&gt;         = Type of potion effect, see " + Files.getNameIndexHashLink("potioneffect"),
                    "    duration <num or min-max> = Duration of the potion effect in seconds, default 1 (does not work on HEAL and HARM)",
                    "    amplify <num or min-max>  = Amplify the effects of the potion, default 0 (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
                    "    ambient or !ambient       = Check effect's extra visual particles setting",
                    "    particles or !particles   = Check effect's particles setting",
                    "    icon or !icon             = Check effect's icon setting",
                }, String.class);
            }

            description = ObjectArrays.concat(description, new String[]{
                "",
                "  banner <condition>, [...]",
                "    pattern <pattern> [dyecolor]",
                "",
                "    Patterns: " + Files.getNameIndexHashLink("bannerpattern"),
                "    Dye Colors: " + RMCUtil.collectionToString(Arrays.asList(DyeColor.values())).toLowerCase(),
                "",
                "  spawnegg &lt;entitytype&gt; = Type of entity contained in a spawn egg, see " + Files.getNameIndexHashLink("entitytype"),
                "",
                // TODO mark
                // "  recipebook <name> [volume <num>] = checks if ingredient is a recipebook generated by this plugin, partial name matching; optionally you can require a specific volume, accepts any volume by default.",
                // "  extinctrecipebook                = checks if the ingredient is a recipe book generated by this plugin but no longer exists, useful to give players a chance to recycle their extinct recipe books.",
                "  failmsg <text>        = overwrite message sent to crafter when failing to provide required ingredient.",
                "",
                "This flag can be used on recipe results to determine a specific outcome for the recipe depending on the ingredients.",
                "  However, you would need 'failmsg false' along with " + FlagType.DISPLAY_RESULT + " flag, see <a href='advanced recipes.html'>advanced recipes.html</a> for an example.",
                "",
                "NOTE: this flag can not be used in recipe header, needs to be defined on individual results or recipes.",
            }, String.class);

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} wood | data 3 // pointless use of this flag, just use wood:3 as ingredient.",
            "{flag} wood | data 1-3, 39, 100 // this overwrites the data condition to the previous one.",
            "{flag} dirt | amount 64 // needs a full stack of dirt to work.",
            "{flag} iron_sword | data 0-25 // only accepts iron swords that have 0 to 25 damage.",
            "{flag} wool | data vanilla, !wool:red // no red wool",
            "{flag} wool | data all, !vanilla // only modded data values",
            "{flag} iron_sword | data new // Only allow undamaged iron swords",
            "{flag} golden_sword | data damaged // Only allow damaged gold swords",
            "{flag} splash_potion // checks if potion is splash",
            "{flag} diamond_helmet | enchant fire_resistance 1-3 | enchant thorns | data 0, 5, 50-100 // makes ingredient require 2 enchantments and some specific data values.",
            "{flag} stick | nometa // makes ingredient require a vanilla stick.",
            "{flag} stick | !meta  // Same as above.",
            "{flag} stick | name Crafted Stick | nolore | noenchant // makes ingredient require a stick with a name of 'Crafted Stick', but no lore or enchantments.", };
    }

    private List<ConditionsIngredient> conditions = new ArrayList<>();

    public FlagIngredientCondition() {
    }

    public FlagIngredientCondition(FlagIngredientCondition flag) {
        super(flag);
        for (ConditionsIngredient condition : flag.conditions) {
            conditions.add(condition.clone());
        }
    }

    @Override
    public FlagIngredientCondition clone() {
        return new FlagIngredientCondition((FlagIngredientCondition) super.clone());
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        // Match on single pipes '|', but not double '||'
        // Double pipes will be replaced by single pipes for each arg
        String[] args = value.split("(?<!\\|)\\|(?!\\|)");

        if (args.length <= 1) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs an item and some arguments for conditions!", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }

        ItemStack item = Tools.parseItem(args[0], RMCVanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);

        if (item == null) {
            return false;
        }

        ConditionsIngredient cond = new ConditionsIngredient();
        cond.setSourceLineNum(lineNum);
        cond.setFlagType(getFlagType());
        setIngredientConditions(item, cond);

        cond.setIngredient(item);

        cond.parse(args);

        return true;
    }

    @Override
    public void onRegistered() {
        BaseRecipe recipe = getRecipeDeep();

        List<ConditionsIngredient> newConditions = new ArrayList<>();
        for (ConditionsIngredient condition : conditions) {
            if (condition.getIngredient() != null && recipe.findItemInIngredients(condition.getIngredient().getType(), condition.getIngredient().getDurability()) == 0) {
                ErrorReporter.getInstance().flagError(this, "Flag " + getFlagType() + " couldn't find ingredient: " + ToolsItem.print(condition.getIngredient()), condition.getSourceLineNum());
            } else {
                newConditions.add(condition);
            }
        }

        conditions = newConditions;
    }
    // TODO: Better handle conditions to allow multiple recipes per item:dur
    public void setIngredientConditions(ItemStack item, ConditionsIngredient cond) {
        Preconditions.checkNotNull(item, "item argument must not be null!");
        Preconditions.checkNotNull(cond, "cond argument must not be null!");

        conditions.add(cond);
    }

    public List<ConditionsIngredient> getIngredientConditions(ItemStack item) {
        if (item == null) {
            return null;
        }

        List<ConditionsIngredient> conditionsList = new ArrayList<>();
        for (ConditionsIngredient condition : conditions) {
            if (RMBukkitTools.isSameItemPlusDur(condition.getIngredient(), item)) {
                conditionsList.add(condition);
            }
        }

        return conditionsList;
    }

    /**
     * @param item
     *            returns false if null.
     * @param a
     *            arguments to store reasons or null to just use return value.
     * @return true if passed, false otherwise
     */
    public boolean checkIngredientConditions(ItemStack item, Args a) {
        if (item == null) {
            return false;
        }

        boolean anySuccess = false;
        List<ConditionsIngredient> condList = getIngredientConditions(item);
        if (condList.isEmpty()) {
            return true;
        }

        for (ConditionsIngredient cond : condList) {
            if (cond == null) {
                return true;
            }

            boolean success = cond.checkIngredient(item, a);
            if (success) {
                anySuccess = true;

                if (cond.hasNeeded() && cond.getNeededLeft() > 0) {
                    cond.setNeededLeft(cond.getNeededLeft() - 1);
                }
            }
        }

        return anySuccess;
    }

    @Override
    public void onCheck(Args a) {
        if (!a.hasInventory() && !a.hasExtra()) {
            a.addCustomReason("Needs inventory or extra ingredient!");
            return;
        }

        if (!a.hasInventory()) {
            Object extra = a.extra();
            if (extra instanceof List) {
                List<?> ingredients = (List<?>) extra;

                for (ConditionsIngredient checkConditions : conditions) {
                    if (checkConditions.hasNeeded()) {
                        checkConditions.setNeededLeft(checkConditions.getNeeded());
                    }
                }

                for (int i = 1; i < ingredients.size(); i++) {
                    Object potentialIngredient = ingredients.get(i);
                    if (potentialIngredient instanceof ItemStack) {
                        ItemStack item = ToolsItem.nullIfAir((ItemStack) potentialIngredient);

                        if (item != null) {
                            checkIngredientConditions(item, a);
                        }
                    }
                }

                for (ConditionsIngredient checkConditions : conditions) {
                    if (checkConditions.hasNeeded()) {
                        if (!a.hasReasons() && checkConditions.getNeededLeft() > 0) {
                            a.addCustomReason("Needed items mismatch!");
                        }

                        checkConditions.setNeededLeft(-1);
                    }
                }

                return;
            }
        } else if (a.inventory() instanceof CraftingInventory) {
            for (ConditionsIngredient checkConditions : conditions) {
                if (checkConditions.hasNeeded()) {
                    checkConditions.setNeededLeft(checkConditions.getNeeded());
                }
            }

            for (int i = 1; i < 10; i++) {
                ItemStack item = a.inventory().getItem(i);

                if (item != null) {
                    checkIngredientConditions(item, a);
                }
            }

            for (ConditionsIngredient checkConditions : conditions) {
                if (checkConditions.hasNeeded()) {
                    if (!a.hasReasons() && checkConditions.getNeededLeft() > 0) {
                        a.addCustomReason("Needed items mismatch!");
                    }

                    checkConditions.setNeededLeft(-1);
                }
            }

            return;
        } else if (a.inventory() instanceof AnvilInventory || (Version.has1_14Support() && (a.inventory() instanceof CartographyInventory || a.inventory() instanceof GrindstoneInventory)) || (Version.has1_16Support() && a.inventory() instanceof SmithingInventory)) {
            for (ConditionsIngredient checkConditions : conditions) {
                if (checkConditions.hasNeeded()) {
                    checkConditions.setNeededLeft(checkConditions.getNeeded());
                }
            }

            for (int i = 0; i < 2; i++) {
                ItemStack item = a.inventory().getItem(i);

                if (item != null) {
                    checkIngredientConditions(item, a);
                }
            }

            for (ConditionsIngredient checkConditions : conditions) {
                if (checkConditions.hasNeeded()) {
                    if (!a.hasReasons() && checkConditions.getNeededLeft() > 0) {
                        a.addCustomReason("Needed items mismatch!");
                    }

                    checkConditions.setNeededLeft(-1);
                }
            }

            return;
        } else if (a.inventory() instanceof FurnaceInventory) {
            ItemStack smelting = ToolsItem.nullIfAir((ItemStack) a.extra());

            if (smelting != null) {
                checkIngredientConditions(smelting, a);
            }

            return;
        } else if (a.inventory() instanceof BrewerInventory) {
            boolean anyPotionSuccess = false;
            List<Boolean> potionBools = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                ItemStack potion = a.inventory().getItem(i);
                if (potion == null) {
                    potionBools.add(false);
                } else {
                    boolean success = checkIngredientConditions(potion, a);
                    potionBools.add(success);

                    if (success) {
                        anyPotionSuccess = true;
                    }
                }
            }
            a.setExtra(potionBools);

            if (anyPotionSuccess) {
                a.clearReasons();
            }

            ItemStack ingredient = a.inventory().getItem(3);
            if (ingredient != null) {
                checkIngredientConditions(ingredient, a);
            }

            return;
        }

        a.addCustomReason(getFlagType() + " has unsupported inventory type: " + a.inventory().getType());
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (ConditionsIngredient condition : conditions) {
            toHash += condition.hashCode();
        }

        return toHash.hashCode();
    }
}
