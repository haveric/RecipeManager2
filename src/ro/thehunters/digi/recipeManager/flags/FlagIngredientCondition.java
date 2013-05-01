package ro.thehunters.digi.recipeManager.flags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import ro.thehunters.digi.recipeManager.Files;
import ro.thehunters.digi.recipeManager.Messages;
import ro.thehunters.digi.recipeManager.RecipeErrorReporter;
import ro.thehunters.digi.recipeManager.Tools;
import ro.thehunters.digi.recipeManager.Tools.ParseBit;
import ro.thehunters.digi.recipeManager.Vanilla;

public class FlagIngredientCondition extends Flag
{
    // Flag definition and documentation
    
    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;
    
    static
    {
        TYPE = FlagType.INGREDIENTCONDITION;
        
        A = new String[]
        {
            "{flag} <item> | <conditions>",
        };
        
        D = new String[]
        {
            "Adds conditions for individual ingredients like ranged data values, enchantments or using stacks.",
            "This flag can be called more than once to add more ingredients with conditions.",
            "",
            "The <item> argument must be an item that is in the recipe, 'material:data' format.",
            "If planning to add ranged data values the data value must be the wildcard '*' or not set at all in order to work.",
            "",
            "For <conditions> argument you must specify at least one condition.",
            "Conditions must be separated by | and can be specified in any order.",
            "Condition list:",
            "",
            "  data <[!][&]min[-max]>, [...]",
            "    Condition for data/damage/durability, as argument you can specify data values separated by , character.",
            "    One number is required, you can add another number separated by - character to make a number range.",
            "    Additionally instead of the number you can specify 'item:data' to use the named data value.",
            "    Prefixing with '&' would make a bitwise operation on the data value.",
            "    Prefixing with '!' would reverse the statement's meaning making it not work with the value specified.",
            "",
            "  enchant <name> [[!]min[-max]], [...]",
            "    Condition for applied enchantments (not stored in books).",
            "    This argument can be used more than once to add more enchantments as conditions.",
            "    The name must be an enchantment name, see '" + Files.FILE_INFO_NAMES + "' at 'ENCHANTMENTS' section.",
            "    The 2nd argument is the levels, it's optional",
            "    A number can be used as level to set that level as requirement.",
            "    You can also use 'max' to use the max supported level for that enchantment.",
            "    Additionally a second number separated by - can be added to specify a level range, 'max' is also supported in ranged value.",
            "    Prefixing with '!' would ban the level or level range.",
            "",
            "  amount <num>                 = stack amount, this will also subtract from the ingredient when crafted!",
            "  name <text or regex>         = check the item name against exact text or regex pattern.",
            "  lore <text or regex>         = checks each lore line for a specific text or regex pattern.",
            "  leather <colorname or R,G,B> = only works for leather armor, checks color, the values can be individual values or ranged separated by - char or you can use a color name constant, see '" + Files.FILE_INFO_NAMES + "' at 'DYE COLOR'.",
            "  failmsg <text>               = overwrite message sent to crafter when failing to provide required ingredient.",
            "",
            "NOTE: if an ingredient exists more than once in the recipe then the conditions will apply to all of them.",
        };
        
        E = new String[]
        {
            "{flag} iron_sword | data 0-25 // only accepts iron swords that have 0 to 25 damage.",
            "{flag} dirt | amount 64 // needs a full stack of dirt to work.",
            "{flag} dirt | data 1-3, 39, 100 // this adds a data condition to the previous one.",
            "{flag} wool | data !wool:red // no red wool",
            "{flag} potion | data &16384, !&64 // checks if potion is splash and NOT extended (see http://www.minecraftwiki.net/wiki/Data_value#Potions)",
            "{flag} diamond_helmet | enchant fire_resistance 1-3 | enchant thorns | data 0, 5, 50-100 // makes ingredient require 2 enchantments and some specific data values.",
        };
    }
    
    // Flag code
    
    // TODO written book title, author, page num, chars per page, etc
    
    public class Conditions implements Cloneable
    {
        private String failMessage;
        private Map<Short, Boolean> dataValues = new HashMap<Short, Boolean>();
        private Map<Short, Boolean> dataBits = new HashMap<Short, Boolean>();
        private int amount;
        private Map<Enchantment, Map<Short, Boolean>> enchants = new HashMap<Enchantment, Map<Short, Boolean>>();
        private String name;
        private String lore;
        private Color minColor;
        private Color maxColor;
        
        public Conditions()
        {
        }
        
        public Conditions(Conditions original)
        {
            failMessage = original.failMessage;
            
            dataValues.putAll(original.dataValues);
            dataBits.putAll(original.dataBits);
            
            amount = original.amount;
            
            for(Entry<Enchantment, Map<Short, Boolean>> e : original.enchants.entrySet())
            {
                Map<Short, Boolean> map = new HashMap<Short, Boolean>(e.getValue().size());
                map.putAll(e.getValue());
                enchants.put(e.getKey(), map);
            }
            
            name = original.name;
            
            lore = original.lore;
            
            minColor = original.minColor;
            maxColor = original.maxColor;
        }
        
        public Conditions clone()
        {
            return new Conditions(this);
        }
        
        public String getFailMessage()
        {
            return failMessage;
        }
        
        public void setFailMessage(String message)
        {
            failMessage = message;
        }
        
        /**
         * @return a map that contains data values and if they should or not be in the ingredient's data (the '!' char in the definition); never null.
         */
        public Map<Short, Boolean> getDataValues()
        {
            return dataValues;
        }
        
        /**
         * Sets the new data values map.<br>
         * If the map is null the values will be cleared.
         * 
         * @param map
         */
        public void setDataValues(Map<Short, Boolean> map)
        {
            if(map == null)
            {
                this.dataValues.clear();
            }
            else
            {
                this.dataValues = map;
            }
        }
        
        /**
         * Adds data value as requirement.
         * 
         * @param data
         */
        public void addDataValue(short data)
        {
            addDataValue(data, true);
        }
        
        /**
         * Adds data value as requirement/restriction.
         * 
         * @param data
         * @param allow
         *            true if requirement, false if restricted
         */
        public void addDataValue(short data, boolean allow)
        {
            dataValues.put(data, allow);
        }
        
        /**
         * Adds data values range as requirement.<br>
         * Note: max >= min
         * 
         * @param min
         * @param max
         */
        public void addDataValueRange(short min, short max)
        {
            addDataValueRange(min, max, true);
        }
        
        /**
         * Adds data values range as requirement/restriction.<br>
         * Note: max >= min
         * 
         * @param min
         * @param max
         * @param allow
         *            true if requirement, false if restricted
         */
        public void addDataValueRange(short min, short max, boolean allow)
        {
            if(min > max)
            {
                throw new IllegalArgumentException("Invalid number range: " + min + " to " + max);
            }
            
            for(short i = min; i <= max; i++)
            {
                addDataValue(i, allow);
            }
        }
        
        public boolean hasDataValues()
        {
            return !dataValues.isEmpty();
        }
        
        /**
         * @return a map that contains data bits and if they should or not be in the ingredient's data (the '!' char in the definition); never null.
         */
        public Map<Short, Boolean> getDataBits()
        {
            return dataBits;
        }
        
        /**
         * Sets the new data bits map.<br>
         * If the map is null the values will be cleared.
         * 
         * @param map
         */
        public void setDataBits(Map<Short, Boolean> map)
        {
            if(map == null)
            {
                this.dataBits.clear();
            }
            else
            {
                this.dataBits = map;
            }
        }
        
        /**
         * Adds data bit as requirement.
         * 
         * @param data
         */
        public void addDataBit(short data)
        {
            addDataBit(data, true);
        }
        
        /**
         * Adds data bit as requirement/restriction.
         * 
         * @param data
         * @param allow
         *            true if requirement, false if restricted
         */
        public void addDataBit(short data, boolean allow)
        {
            dataBits.put(data, allow);
        }
        
        public boolean hasDataBits()
        {
            return !dataBits.isEmpty();
        }
        
        /**
         * @return human-friendly list of data values and bits
         */
        public String getDataString()
        {
            StringBuilder s = new StringBuilder();
            
            for(Entry<Short, Boolean> e : dataValues.entrySet())
            {
                if(s.length() > 0)
                {
                    s.append(", ");
                }
                
                if(!e.getValue())
                {
                    s.append("! ");
                }
                
                s.append(e.getKey());
            }
            
            for(Entry<Short, Boolean> e : dataBits.entrySet())
            {
                if(s.length() > 0)
                {
                    s.append(", ");
                }
                
                if(!e.getValue())
                {
                    s.append("! ");
                }
                
                s.append("& ").append(e.getKey());
            }
            
            return s.toString();
        }
        
        /**
         * Checks if the supplied data value can be used with this condition.
         * 
         * @param data
         *            ingredient's data value
         * @return true if value is permitted, false otherwise.
         */
        public boolean checkData(short data)
        {
            if(!dataBits.isEmpty())
            {
                for(Entry<Short, Boolean> e : dataBits.entrySet())
                {
                    short d = e.getKey().shortValue();
                    
                    if(e.getValue() ? (data & d) == d : (data & d) != d)
                    {
                        return false;
                    }
                }
            }
            
            if(!dataValues.isEmpty())
            {
                Boolean is = dataValues.get(data);
                
                return is == null ? false : is.booleanValue(); // if value not found return false otherwise return true/false if value should/not be there
            }
            
            return true;
        }
        
        public int getAmount()
        {
            return amount;
        }
        
        public void setAmount(int amount)
        {
            this.amount = amount;
        }
        
        public boolean hasAmount()
        {
            return amount > 0;
        }
        
        public boolean checkAmount(int amount)
        {
            return (amount >= this.amount);
        }
        
        /**
         * @return enchantments map, never null.
         */
        public Map<Enchantment, Map<Short, Boolean>> getEnchants()
        {
            return enchants;
        }
        
        /**
         * Set the enchants map.<br>
         * Setting to null will clear the map contents.
         * 
         * @param enchants
         */
        public void setEnchants(Map<Enchantment, Map<Short, Boolean>> enchants)
        {
            if(enchants == null)
            {
                this.enchants.clear();
            }
            else
            {
                this.enchants = enchants;
            }
        }
        
        public void addEnchant(Enchantment enchant)
        {
            enchants.put(enchant, new HashMap<Short, Boolean>(0));
        }
        
        public void addEnchantLevel(Enchantment enchant, short level)
        {
            addEnchantLevel(enchant, level, true);
        }
        
        public void addEnchantLevel(Enchantment enchant, short level, boolean allow)
        {
            addEnchantLevelRange(enchant, level, level, allow);
        }
        
        public void addEnchantLevelRange(Enchantment enchant, short min, short max)
        {
            addEnchantLevelRange(enchant, min, max, true);
        }
        
        public void addEnchantLevelRange(Enchantment enchant, short min, short max, boolean allow)
        {
            Map<Short, Boolean> levels = enchants.get(enchant);
            
            if(levels == null)
            {
                levels = new HashMap<Short, Boolean>();
                enchants.put(enchant, levels);
            }
            
            for(short i = min; i <= max; i++)
            {
                levels.put(i, allow);
            }
        }
        
        public boolean hasEnchants()
        {
            return !enchants.isEmpty();
        }
        
        public boolean checkEnchants(Map<Enchantment, Integer> enchants)
        {
            if(this.enchants.isEmpty())
            {
                return true; // no enchantment conditions
            }
            
            if(enchants.isEmpty())
            {
                return false; // this flag requires enchantments but item has none
            }
            
            for(Entry<Enchantment, Map<Short, Boolean>> e : this.enchants.entrySet())
            {
                Integer level = enchants.get(e.getKey());
                
                if(level == null)
                {
                    return false;
                }
                else if(!e.getValue().isEmpty())
                {
                    Boolean is = e.getValue().get(level.shortValue());
                    
                    return (is == null ? false : is.booleanValue());
                }
            }
            
            return true;
        }
        
        public String getEnchantsString()
        {
            StringBuilder s = new StringBuilder();
            
            for(Entry<Enchantment, Map<Short, Boolean>> e : getEnchants().entrySet())
            {
                if(s.length() > 0)
                {
                    s.append("; ");
                }
                
                s.append(e.getKey().getName());
                
                if(!e.getValue().isEmpty())
                {
                    s.append(' ');
                    boolean first = true;
                    
                    for(Entry<Short, Boolean> l : e.getValue().entrySet())
                    {
                        if(first)
                        {
                            first = false;
                        }
                        else
                        {
                            s.append(", ");
                        }
                        
                        if(!l.getValue())
                        {
                            s.append("! ");
                        }
                        
                        s.append(l.getKey());
                    }
                }
            }
            
            return s.toString();
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public boolean hasName()
        {
            return name != null;
        }
        
        public boolean checkName(String name)
        {
            if(this.name == null)
            {
                return true;
            }
            
            if(this.name.equalsIgnoreCase(name) || name.matches(this.name))
            {
                return true;
            }
            
            return false;
        }
        
        public String getLore()
        {
            return lore;
        }
        
        public void setLore(String lore)
        {
            this.lore = lore;
        }
        
        public boolean hasLore()
        {
            return lore != null;
        }
        
        public boolean checkLore(List<String> lore)
        {
            if(!hasLore())
            {
                return true;
            }
            
            if(lore == null || lore.isEmpty())
            {
                return false;
            }
            
            for(String l : lore)
            {
                if(l.equalsIgnoreCase(this.lore) || l.matches(this.lore))
                {
                    return true;
                }
            }
            
            return false;
        }
        
        /**
         * Set the color ranges.<br>
         * 
         * @param minColor
         *            color for min-range or null to disable color checking.
         * @param maxColor
         *            color for max-range or null to disable range.
         */
        public void setColor(Color minColor, Color maxColor)
        {
            this.minColor = minColor;
            this.maxColor = maxColor;
        }
        
        /**
         * Sets the color required.<br>
         * NOTE: This sets maxColor to null.
         * 
         * @param r
         *            0-255
         * @param g
         *            0-255
         * @param b
         *            0-255
         */
        public void setColor(int r, int g, int b)
        {
            minColor = Color.fromRGB(r, g, b);
            maxColor = null;
        }
        
        /**
         * Sets the color range required.
         * 
         * @param minR
         *            0 to 255
         * @param maxR
         *            minR to 255
         * @param minG
         *            0 to 255
         * @param maxG
         *            minG to 255
         * @param minB
         *            0 to 255
         * @param maxB
         *            minG to 255
         */
        public void setColor(int minR, int maxR, int minG, int maxG, int minB, int maxB)
        {
            Validate.isTrue(maxR >= minR, "minR is bigger than maxR !");
            Validate.isTrue(maxG >= minG, "minG is bigger than maxG !");
            Validate.isTrue(maxB >= minB, "minB is bigger than maxB !");
            
            minColor = Color.fromRGB(minR, minG, minB);
            maxColor = Color.fromRGB(maxR, maxG, maxB);
        }
        
        /**
         * @return color or null if color checking is disabled.
         */
        public Color getMinColor()
        {
            return minColor;
        }
        
        /**
         * @return color or null if range is disabled.
         */
        public Color getMaxColor()
        {
            return maxColor;
        }
        
        /**
         * @return user-friendly color info or null if disabled
         */
        public String getColorString()
        {
            if(minColor == null)
            {
                return null;
            }
            
            StringBuilder s = new StringBuilder();
            
            if(maxColor == null)
            {
                s.append(minColor.getRed()).append(", ");
                s.append(minColor.getGreen()).append(", ");
                s.append(minColor.getBlue());
            }
            else
            {
                s.append(minColor.getRed()).append("-").append(maxColor.getRed()).append(", ");
                s.append(minColor.getGreen()).append("-").append(maxColor.getGreen()).append(", ");
                s.append(minColor.getBlue()).append("-").append(maxColor.getBlue());
            }
            
            return s.toString();
        }
        
        /**
         * @return if minColor != null
         */
        public boolean hasColor()
        {
            return minColor != null;
        }
        
        public boolean checkColor(Color color)
        {
            if(color == null)
            {
                return false;
            }
            
            if(minColor == null)
            {
                return true;
            }
            
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            
            if(maxColor == null)
            {
                return (minColor.getRed() == r && minColor.getGreen() == g && minColor.getBlue() == b);
            }
            else
            {
                return (minColor.getRed() <= r && maxColor.getRed() >= r && minColor.getGreen() <= g && maxColor.getGreen() >= g && minColor.getBlue() <= b && maxColor.getBlue() >= b);
            }
        }
        
        public boolean checkIngredient(ItemStack item, Args a)
        {
            boolean ok = true;
            
            if(!checkData(item.getDurability()))
            {
                if(a == null)
                {
                    return false;
                }
                
                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NODATA, getFailMessage(), "{item}", Tools.Item.print(item), "{data}", getDataString());
                ok = false;
                
                if(getFailMessage() != null)
                {
                    return false;
                }
            }
            
            if(!checkAmount(item.getAmount()))
            {
                if(a == null)
                {
                    return false;
                }
                
                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOAMOUNT, getFailMessage(), "{item}", Tools.Item.print(item), "{amount}", getAmount());
                ok = false;
                
                if(getFailMessage() != null)
                {
                    return false;
                }
            }
            
            if(!checkEnchants(item.getEnchantments()))
            {
                if(a == null)
                {
                    return false;
                }
                
                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOENCHANTS, getFailMessage(), "{item}", Tools.Item.print(item), "{enchants}", getEnchantsString());
                ok = false;
                
                if(getFailMessage() != null)
                {
                    return false;
                }
            }
            
            ItemMeta meta = item.getItemMeta();
            
            if(!checkName(meta.getDisplayName()))
            {
                if(a == null)
                {
                    return false;
                }
                
                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NONAME, getFailMessage(), "{item}", Tools.Item.print(item), "{name}", getName());
                ok = false;
                
                if(getFailMessage() != null)
                {
                    return false;
                }
            }
            
            if(!checkLore(meta.getLore()))
            {
                if(a == null)
                {
                    return false;
                }
                
                a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOLORE, getFailMessage(), "{item}", Tools.Item.print(item), "{lore}", getLore());
                ok = false;
                
                if(getFailMessage() != null)
                {
                    return false;
                }
            }
            
            if(hasColor())
            {
                boolean failed = true;
                
                if(meta instanceof LeatherArmorMeta)
                {
                    LeatherArmorMeta leather = (LeatherArmorMeta)meta;
                    
                    if(checkColor(leather.getColor()))
                    {
                        failed = false;
                    }
                }
                
                if(failed)
                {
                    if(a == null)
                    {
                        return false;
                    }
                    
                    a.addReason(Messages.FLAG_INGREDIENTCONDITIONS_NOCOLOR, getFailMessage(), "{item}", Tools.Item.print(item), "{color}", getColorString());
                    ok = false;
                    
                    if(getFailMessage() != null)
                    {
                        return false;
                    }
                }
            }
            
            return ok;
        }
    }
    
    private Map<String, Conditions> conditions = new HashMap<String, Conditions>();
    
    public FlagIngredientCondition()
    {
    }
    
    public FlagIngredientCondition(FlagIngredientCondition flag)
    {
        for(Entry<String, Conditions> e : flag.conditions.entrySet())
        {
            conditions.put(e.getKey(), e.getValue().clone());
        }
    }
    
    @Override
    public FlagIngredientCondition clone()
    {
        return new FlagIngredientCondition(this);
    }
    
    @Override
    public FlagType getType()
    {
        return TYPE;
    }
    
    @Override
    protected boolean onParse(String value)
    {
        String[] args = value.split("\\|");
        
        if(args.length <= 1)
        {
            return RecipeErrorReporter.error("Flag " + getType() + " needs an item and some arguments for conditions !", "Read '" + Files.FILE_INFO_FLAGS + "' for more info.");
        }
        
        ItemStack item = Tools.parseItem(args[0], Vanilla.DATA_WILDCARD, ParseBit.NO_AMOUNT | ParseBit.NO_META);
        
        if(item == null)
        {
            return false;
        }
        
        if(Tools.findItemInIngredients(getRecipeDeep(), item.getType(), item.getDurability()) == 0)
        {
            RecipeErrorReporter.error("Flag " + getType() + " has couldn't find ingredient: " + Tools.Item.print(item));
            return false;
        }
        
        Conditions cond = getIngredientConditions(item);
        
        if(cond == null)
        {
            cond = new Conditions();
            setIngredientConditions(item, cond);
        }
        
        for(int i = 1; i < args.length; i++)
        {
            String arg = args[i].trim().toLowerCase();
            
            if(arg.startsWith("data"))
            {
                if(item.getDurability() != Vanilla.DATA_WILDCARD)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'data' argument but ingredient has specific data!", "The ingredient must have the 'any' data value set.");
                    continue;
                }
                
                value = arg.substring("data".length()).trim();
                
                String[] list = value.split(",");
                
                for(String val : list)
                {
                    val = val.trim();
                    boolean not = val.charAt(0) == '!';
                    
                    if(not)
                    {
                        val = val.substring(1).trim();
                    }
                    
                    ItemStack match = Tools.parseItem(val, 0, ParseBit.NO_AMOUNT | ParseBit.NO_META | ParseBit.NO_PRINT);
                    
                    if(match != null)
                    {
                        cond.addDataValue(match.getDurability(), !not);
                    }
                    else
                    {
                        String[] split = val.split("-");
                        
                        if(split.length > 1)
                        {
                            short min;
                            short max;
                            
                            try
                            {
                                min = Short.valueOf(split[0].trim());
                                max = Short.valueOf(split[1].trim());
                            }
                            catch(NumberFormatException e)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'data' argument with invalid numbers: " + val);
                                continue;
                            }
                            
                            if(min > max)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'data' argument with invalid number range: " + min + " to " + max);
                                break;
                            }
                            
                            cond.addDataValueRange(min, max, !not);
                        }
                        else
                        {
                            val = val.trim();
                            boolean bitwise = val.charAt(0) == '&';
                            
                            if(bitwise)
                            {
                                val = val.substring(1).trim();
                            }
                            
                            try
                            {
                                if(bitwise)
                                {
                                    cond.addDataBit(Short.valueOf(val), not);
                                }
                                else
                                {
                                    cond.addDataValue(Short.valueOf(val), not);
                                }
                            }
                            catch(NumberFormatException e)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'data' argument with invalid number: " + val);
                                continue;
                            }
                        }
                    }
                }
            }
            else if(arg.startsWith("amount"))
            {
                value = arg.substring("amount".length()).trim();
                
                try
                {
                    cond.setAmount(Integer.valueOf(value));
                }
                catch(NumberFormatException e)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'amount' argument with invalid number: " + value);
                    continue;
                }
            }
            else if(arg.startsWith("enchant"))
            {
                value = arg.substring("enchant".length()).trim();
                
                String[] list = value.split(" ", 2);
                
                value = list[0].trim();
                
                Enchantment enchant = Tools.parseEnchant(value);
                
                if(enchant == null)
                {
                    RecipeErrorReporter.error("Flag " + getType() + " has 'enchant' argument with invalid name: " + value);
                    continue;
                }
                
                if(list.length > 1)
                {
                    list = list[1].split(",");
                    
                    for(String s : list)
                    {
                        s = s.trim();
                        boolean not = s.charAt(0) == '!';
                        
                        if(not)
                        {
                            s = s.substring(1).trim();
                        }
                        
                        String[] split = s.split("-", 2);
                        
                        if(split.length > 1)
                        {
                            short min;
                            short max;
                            
                            try
                            {
                                min = Short.valueOf(split[0].trim());
                                max = Short.valueOf(split[1].trim());
                            }
                            catch(NumberFormatException e)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'enchant' argument with invalid numbers: " + s);
                                continue;
                            }
                            
                            if(min > max)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'enchant' argument with invalid number range: " + min + " to " + max);
                                continue;
                            }
                            
                            cond.addEnchantLevelRange(enchant, min, max, !not);
                        }
                        else
                        {
                            try
                            {
                                cond.addEnchantLevel(enchant, Short.valueOf(s.trim()), !not);
                            }
                            catch(NumberFormatException e)
                            {
                                RecipeErrorReporter.error("Flag " + getType() + " has 'enchant' argument with invalid number: " + s);
                                continue;
                            }
                        }
                    }
                }
                else
                {
                    cond.addEnchant(enchant);
                }
            }
            else if(arg.startsWith("color"))
            {
                if(item.getItemMeta() instanceof LeatherArmorMeta == false)
                {
                    RecipeErrorReporter.warning("Flag " + getType() + " has 'color' argument for an item that is not leather armor.", "RGB can only be applied to leather, for wool and dye use the 'data' argument.");
                    continue;
                }
                
                value = arg.substring("color".length()).trim();
                
                DyeColor dye = Tools.parseEnum(value, DyeColor.values());
                
                if(dye != null)
                {
                    cond.setColor(dye.getColor(), null);
                }
                else
                {
                    String[] split = value.split(",", 3);
                    
                    if(split.length != 3)
                    {
                        RecipeErrorReporter.warning("Flag " + getType() + " has 'color' argument with less than 3 colors separated by comma: " + value);
                        continue;
                    }
                    
                    short[] minColor = new short[3];
                    short[] maxColor = new short[3];
                    
                    for(int c = 0; c < split.length; c++)
                    {
                        String[] range = split[c].split("-", 2);
                        
                        try
                        {
                            short min = Short.valueOf(range[0].trim());
                            short max = min;
                            
                            if(range.length > 1)
                            {
                                max = Short.valueOf(range[1].trim());
                            }
                            
                            if(min < 0 || min > 255 || min > max || max > 255)
                            {
                                RecipeErrorReporter.warning("Flag " + getType() + " has 'color' argument with invalid range: " + min + " to " + max, "Numbers must be from 0 to 255 and min must be less or equal to max!");
                                break;
                            }
                            
                            minColor[c] = min;
                            maxColor[c] = max;
                        }
                        catch(NumberFormatException e)
                        {
                            RecipeErrorReporter.warning("Flag " + getType() + " has 'color' argument with invalid number: " + value);
                            continue;
                        }
                    }
                }
            }
            else if(arg.startsWith("name"))
            {
                value = args[i].trim().substring("name".length()).trim(); // preserve case for regex
                
                cond.setName(value);
            }
            else if(arg.startsWith("lore"))
            {
                value = args[i].trim().substring("lore".length()).trim(); // preserve case for regex
                
                cond.setLore(value);
            }
            else if(arg.startsWith("failmsg"))
            {
                value = args[i].trim().substring("failmsg".length()).trim(); // preserve case... because it's a message
                
                cond.setFailMessage(value);
            }
            else
            {
                RecipeErrorReporter.warning("Flag " + getType() + " has unknown argument: " + args[i]);
            }
        }
        
        return true;
    }
    
    public void setIngredientConditions(ItemStack item, Conditions cond)
    {
        Validate.notNull(item, "item argument must not be null!");
        Validate.notNull(cond, "cond argument must not be null!");
        
        this.conditions.put(Tools.convertItemToStringId(item), cond);
    }
    
    public Conditions getIngredientConditions(ItemStack item)
    {
        if(item == null)
        {
            return null;
        }
        
        Conditions cond = conditions.get(String.valueOf(item.getTypeId() + ":" + item.getDurability()));
        
        if(cond == null)
        {
            cond = conditions.get(String.valueOf(item.getTypeId()));
        }
        
        return cond;
    }
    
    /**
     * @param item
     *            returns false if null.
     * @param a
     *            arguments to store reasons or null to just use return value.
     * @return true if passed, false otherwise
     */
    public boolean checkIngredientConditions(ItemStack item, Args a)
    {
        if(item == null)
        {
            return false;
        }
        
        Conditions cond = getIngredientConditions(item);
        
        if(cond == null)
        {
            return true;
        }
        
        return cond.checkIngredient(item, a);
    }
    
    @Override
    protected void onCheck(Args a)
    {
        if(!a.hasInventory())
        {
            a.addCustomReason("Needs inventory!");
            return;
        }
        
        if(a.inventory() instanceof CraftingInventory)
        {
            for(int i = 1; i < 10; i++)
            {
                ItemStack item = a.inventory().getItem(i);
                
                if(item != null)
                {
                    checkIngredientConditions(item, a);
                }
            }
            
            return;
        }
        else if(a.inventory() instanceof FurnaceInventory)
        {
            FurnaceInventory inv = (FurnaceInventory)a.inventory();
            ItemStack smelting = Tools.Item.nullIfAir(inv.getSmelting());
            
            if(smelting != null)
            {
                checkIngredientConditions(smelting, a);
            }
            
            return;
        }
        
        a.addCustomReason("Unknown inventory type: " + a.inventory());
    }
}
