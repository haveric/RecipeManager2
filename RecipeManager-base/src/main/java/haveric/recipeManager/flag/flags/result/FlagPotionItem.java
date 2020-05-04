package haveric.recipeManager.flag.flags.result;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

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
        String[] description = new String[] {
                "Builds a potion item, only works with POTION item.",
                "",
                "There are 2 types of potions... basic potions which have 1 effect and custom potions which can have multiple effects.",
                "",
                "Building a basic potion:",
                "",
                "Instead of <basic effect> argument you must enter a series of arguments separated by | character, in any order.",
                "Arguments for basic effect:",
                "  type &lt;potion type&gt;     = (REQUIRED if you want to use level or extended) Type of potion, see " + Files.getNameIndexHashLink("potiontype"),
                "  level <number or max>  = (optional) Potion's level/tier, usually 1(default) or 2, you can enter 'max' to set it at highest supported level",
                "  extended               = (optional) Potion has extended duration",
                "  color <r> <g> <b>      = (optional) Sets the base color",
                "  splash                 = (optional) Throwable/breakable potion instead of drinkable",
        };

        if (Version.has1_9Support()) {
            description = ObjectArrays.concat(description, new String[] {
                    "  lingering              = (optional) Lingering potion instead of drinkable",
            }, String.class);
        }
        description = ObjectArrays.concat(description, new String[] {
                "",
                "",
                "Building a custom potion requires adding individual effects:",
                "",
                "A basic potion still affects the custom potion like the following:",
                "- If no basic potion is defined the bottle will look like 'water bottle' with no effects listed, effects still apply when drank",
                "- Basic potion's type affects bottle liquid color",
                "- Basic potion's splash still affects if the bottle is throwable instead of drinkable",
                "- Basic potion's extended and level do absolutely nothing.",
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
        }, String.class);

        if (Version.has1_13BasicSupport()) {
            description = ObjectArrays.concat(description, new String[]{
                    "  icon [false]        = (defaults true) show the effect icon.",
                    "  !icon               = equivalent to 'icon false'",
            }, String.class);
        }

        return description;
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} type FIRE_RESISTANCE | level max | extended // basic extended fire resistance potion",
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

        if (result == null || !(result.getItemMeta() instanceof PotionMeta)) {
            ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a POTION item!");
            return false;
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum) {
        super.onParse(value, fileName, lineNum);
        if (value.startsWith("custom")) {
            value = value.substring("custom".length()).trim();
            PotionEffect effect = Tools.parsePotionEffect(value, getFlagType());

            if (effect != null) {
                addEffect(effect);
            }
        } else {
            if (Version.has1_9Support()) {
                basePotion = Tools.parsePotion19(value, getFlagType());
            } else {
                //noinspection deprecation
                Potion p = Tools.parsePotion18(value, getFlagType());

                if (p != null) {
                    //noinspection deprecation
                    data = p.toDamageValue();
                }
            }
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
                resultMeta.setBasePotionData(baseMeta.getBasePotionData());

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
}
