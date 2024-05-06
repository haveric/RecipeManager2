package haveric.recipeManager.flag.flags.result.meta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.ToolsFlag;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class FlagPotionItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.POTION_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <basic effect>",
            "{flag} custom <custom effect>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Builds a potion item, only works with POTION item.",
            "",
            "There are 2 types of potions... basic potions which have 1 effect and custom potions which can have multiple effects.",
            "",
            "Building a basic potion:",
            "",
            "Instead of <basic effect> argument you must enter a series of arguments separated by | character, in any order.",
            "Arguments for basic effect:",
            "  type &lt;potion type&gt;   = Type of potion, see " + Files.getNameIndexHashLink("potiontype"),
            "  color <red> <green> <blue> = (optional) Sets the base color. Colors must be 3 numbers ranged from 0 to 255, the red, green and blue channels.",
            "  splash                     = (optional) Throwable/breakable potion instead of drinkable",
            "  lingering                  = (optional) Lingering potion instead of drinkable",
            "",
            "",
            "Building a custom potion requires adding individual effects:",
            "",
            "A basic potion still affects the custom potion like the following:",
            "- If no basic potion is defined the bottle will look like 'water bottle' with no effects listed, effects still apply when drank",
            "- Basic potion's type affects bottle liquid color",
            "- Basic potion's splash still affects if the bottle is throwable instead of drinkable",
            "- The first custom effect added is the potion's name, rest of effects are in description (of course you can use @name to change the item name)",
            "",
            "Once you understand that, you may use @potion custom as many times to add as many effects you want.",
            "",
            "Similar syntax to basic effect, arguments separated by | character, can be in any order after the effect type.",
            "Arguments for custom effect:",
            "  &lt;effect type&gt;       = (REQUIRED) Type of potion effect, see " + Files.getNameIndexHashLink("potioneffect"),
            "  duration <float>    = (default 1.0) Duration of the potion effect in seconds (does not work on instant potions effects such as HEAL and HARM)",
            "  amplifier <number>  = (default 0) Amplify the effects of the potion (e.g. 2 = <PotionName> III, numbers after potion's max level will display potion.potency.number instead)",
            "  ambient [false]     = (default true) makes the effect produce more, translucent, particles.",
            "  !ambient            = equivalent to 'ambient false'",
            "  particles [false]   = (defaults true) display particles.",
            "  !particles          = equivalent to 'particles false'",
            "  icon [false]        = (defaults true) show the effect icon.",
            "  !icon               = equivalent to 'icon false'",
        };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} type FIRE_RESISTANCE // basic fire resistance potion",
            "// advanced potion example:",
            "{flag} type POISON | splash | color 255 128 0 // set the bottle design and set it as splash with a custom color",
            "{flag} custom WITHER | duration 10 // add wither effect",
            "{flag} custom BLINDNESS | duration 2.5 | amplifier 5 // add blindness effect", };
    }


    private short data;
    private List<PotionEffect> effects = new ArrayList<>();
    private ItemStack basePotion;

    public FlagPotionItem() {
    }

    public FlagPotionItem(FlagPotionItem flag) {
        super(flag);
        data = flag.data;
        effects.addAll(flag.effects);

        if (flag.basePotion == null) {
            basePotion = null;
        } else {
            basePotion = flag.basePotion.clone();
        }
    }

    @Override
    public FlagPotionItem clone() {
        return new FlagPotionItem((FlagPotionItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public short getData() {
        return data;
    }

    public void setData(short newData) {
        data = newData;
    }

    public ItemStack getBasePotion() {
        return basePotion;
    }

    public void setBasePotion(ItemStack potion) {
        basePotion = potion;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> newEffects) {
        if (newEffects == null) {
            effects.clear();
        } else {
            effects = newEffects;
        }
    }

    public void addEffect(PotionEffect effect) {
        effects.add(effect);
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof PotionMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), PotionMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a POTION item!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (value.startsWith("custom")) {
            value = value.substring("custom".length()).trim();
            PotionEffect effect = Tools.parsePotionEffect(value, getFlagType());

            if (effect != null) {
                addEffect(effect);
            }
        } else {
            basePotion = Tools.parsePotion19(value, getFlagType());
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        if (basePotion != null) {
            Material baseType = basePotion.getType();
            if (baseType != Material.POTION && baseType != a.result().getType()) {
                a.result().setType(baseType);
            }

            PotionMeta baseMeta = (PotionMeta) basePotion.getItemMeta();
            PotionMeta resultMeta = (PotionMeta) a.result().getItemMeta();
            if (baseMeta != null && resultMeta != null) {
                resultMeta.setBasePotionType(baseMeta.getBasePotionType());

                if (baseMeta.hasColor()) {
                    resultMeta.setColor(baseMeta.getColor());
                }
            }

            a.result().setItemMeta(resultMeta);
        } else if (data != 0) {
            a.result().setDurability(data);
        }

        if (!effects.isEmpty()) {
            PotionMeta meta = (PotionMeta) a.result().getItemMeta();

            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }

            a.result().setItemMeta(meta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "data: " + data;

        for (PotionEffect effect : effects) {
            toHash += "potioneffect: " + effect.hashCode();
        }

        toHash += "basePotion: " + basePotion.hashCode();

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            PotionType potionType = potionMeta.getBasePotionType();

            recipeString.append(Files.NL).append("@potionitem type ").append(potionType);

            if (potionMeta.hasColor()) {
                Color potionColor = potionMeta.getColor();
                if (potionColor != null) {
                    recipeString.append(" | color ").append(potionColor.getRed()).append(" ").append(potionColor.getGreen()).append(" ").append(potionColor.getBlue());
                }
            }

            if (potionMeta.hasCustomEffects()) {
                List<PotionEffect> potionEffects = potionMeta.getCustomEffects();
                for (PotionEffect effect : potionEffects) {
                    recipeString.append(Files.NL).append("@potionitem custom ");

                    ToolsFlag.parsePotionEffectForItemMeta(recipeString, effect);
                }
            }
        }
    }
}
