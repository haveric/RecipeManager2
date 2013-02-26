package ro.thehunters.digi.recipeManager.commands;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;

import ro.thehunters.digi.recipeManager.*;


public class ExtractCommand implements CommandExecutor
{
    private String NL = System.getProperty("line.separator");
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled" + File.separator + "extracted recipes (" + new SimpleDateFormat("yyyy-MM-dd HH-mm").format(new Date()) + ").txt");
        
        if(file.exists())
        {
            Messages.send(sender, "<red>Command re-used too fast, wait a minute.");
            return true;
        }
        
        boolean skipSpecial = true;
        
        if(args.length > 0)
        {
            for(String arg : args)
            {
                if(arg.equalsIgnoreCase("special"))
                {
                    skipSpecial = false;
                }
                else
                {
                    Messages.send(sender, "<red>Unknown argument: " + arg);
                }
            }
        }
        
        Messages.send(sender, "<gray>Searching and converting recipes...");
        
        List<String> parsedCraftRecipes = new ArrayList<String>();
        List<String> parsedCombineRecipes = new ArrayList<String>();
        List<String> parsedSmeltRecipes = new ArrayList<String>();
        
        Iterator<org.bukkit.inventory.Recipe> recipes = Bukkit.getServer().recipeIterator();
        ItemStack result;
        Recipe r;
        int recipesNum = 0;
        
        while(recipes.hasNext())
        {
            r = recipes.next();
            
            if(r == null || RecipeManager.getRecipes().isCustomRecipe(r))
                continue;
            
            if(skipSpecial)
            {
                result = r.getResult();
                
                if(result.equals(BukkitRecipes.RECIPE_LEATHERDYE) || result.equals(BukkitRecipes.RECIPE_FIREWORKS) || result.equals(BukkitRecipes.RECIPE_MAPCLONE) || result.equals(BukkitRecipes.RECIPE_MAPEXTEND))
                    continue;
            }
            
            if(r instanceof ShapedRecipe)
            {
                ShapedRecipe recipe = (ShapedRecipe)r;
                StringBuilder recipeString = new StringBuilder("CRAFT").append(NL);
                Map<Character, ItemStack> items = recipe.getIngredientMap();
                String[] shape = recipe.getShape();
                char[] cols;
                ItemStack item;
                
                for(String element : shape)
                {
                    cols = element.toCharArray();
                    
                    for(int c = 0; c < cols.length; c++)
                    {
                        item = items.get(cols[c]);
                        
                        recipeString.append(parseIngredient(item));
                        
                        if((c + 1) < cols.length)
                            recipeString.append(" + ");
                    }
                    
                    recipeString.append(NL);
                }
                
                parseResult(recipe.getResult(), recipeString);
                
                parsedCraftRecipes.add(recipeString.toString());
            }
            else if(r instanceof ShapelessRecipe)
            {
                ShapelessRecipe recipe = (ShapelessRecipe)r;
                StringBuilder recipeString = new StringBuilder("COMBINE").append(NL);
                List<ItemStack> ingredients = recipe.getIngredientList();
                int size = ingredients.size();
                
                for(int i = 0; i < size; i++)
                {
                    recipeString.append(parseIngredient(ingredients.get(i)));
                    
                    if((i + 1) < size)
                        recipeString.append(" + ");
                }
                
                recipeString.append(NL);
                parseResult(recipe.getResult(), recipeString);
                
                parsedCombineRecipes.add(recipeString.toString());
            }
            else if(r instanceof FurnaceRecipe)
            {
                FurnaceRecipe recipe = (FurnaceRecipe)r;
                StringBuilder recipeString = new StringBuilder("SMELT").append(NL);
                
                recipeString.append(parseIngredient(recipe.getInput()));
                recipeString.append(NL);
                parseResult(recipe.getResult(), recipeString);
                
                parsedSmeltRecipes.add(recipeString.toString());
            }
            
            recipesNum++;
        }
        
        if(recipesNum == 0)
            Messages.send(sender, "<yellow>No recipes to extract.");
        
        else
        {
            try
            {
                if(!file.createNewFile())
                    Messages.log("<red>Couldn't create file: " + file.getPath());
                
                BufferedWriter stream = new BufferedWriter(new FileWriter(file));
                
                stream.write("// You can uncomment the following lines to apply the flag to the entire file:" + NL);
                stream.write("//@remove     // remove recipes" + NL);
                stream.write("//@override   // allow editing of result and adding other flags to the recipes" + NL);
                
                stream.write("//---------------------------------------------------" + NL + "// Craft recipes" + NL + NL);
                
                for(String str : parsedCraftRecipes)
                {
                    stream.write(str);
                }
                
                stream.write("//---------------------------------------------------" + NL + "// Combine recipes" + NL + NL);
                
                for(String str : parsedCombineRecipes)
                {
                    stream.write(str);
                }
                
                stream.write("//---------------------------------------------------" + NL + "// Smelt recipes" + NL + NL);
                
                for(String str : parsedSmeltRecipes)
                {
                    stream.write(str);
                }
                
                stream.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
            Messages.send(sender, "<green>Done! Recipes saved to: " + file.getPath().replace(RecipeManager.getPlugin().getDataFolder().toString(), ""));
        }
        
        return true;
    }
    
    private String parseIngredient(ItemStack item)
    {
        return (item == null ? "AIR" : item.getType() + (item.getAmount() > 1 || item.getDurability() != -1 ? ":" + item.getDurability() + (item.getAmount() > 1 ? ":" + item.getAmount() : "") : ""));
    }
    
    private void parseResult(ItemStack result, StringBuilder recipeString)
    {
        recipeString.append("= " + result.getType() + (result.getAmount() > 1 || result.getDurability() != 0 ? ":" + result.getDurability() + (result.getAmount() > 1 ? ":" + result.getAmount() : "") : ""));
        
        int enchantments = result.getEnchantments().size();
        
        if(enchantments > 0)
        {
            recipeString.append(" | ");
            int i = 0;
            
            for(Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet())
            {
                recipeString.append(entry.getKey() + ":" + entry.getValue());
                
                if(++i < enchantments)
                    recipeString.append(", ");
            }
        }
        
        recipeString.append(NL + NL);
    }
}