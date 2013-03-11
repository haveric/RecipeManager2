package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitTask;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeStatus;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

public class RecipeRegistrator implements Runnable
{
    private final CommandSender                     sender;
    
    protected Map<BaseRecipe, RecipeInfo>           queuedRecipes = new HashMap<BaseRecipe, RecipeInfo>();
    private boolean                                 registered    = false;
    
    private Iterator<Entry<BaseRecipe, RecipeInfo>> iterator      = null;
    
    private int                                     size;
    private long                                    start;
    private int                                     processed     = 0;
    private long                                    time;
    private long                                    lastDisplay;
    private int                                     temp;
    
    private static BukkitTask                       task;
    
    protected RecipeRegistrator(CommandSender sender)
    {
        this.sender = sender;
        
        if(task != null)
            task.cancel();
    }
    
    private BaseRecipe r;
    
    @Override
    public void run()
    {
        /*
        temp = 200;
        
        while(temp-- >= 0)
        {
            r = recipes.poll();
            time = System.currentTimeMillis();
            
            if(r == null)
            {
                if(task != null)
                    task.cancel();
                
                Messages.send(sender, "<green>Recipes loaded! Total elapsed time " + (time - start) / 1000.0 + " seconds");
                return;
            }
            
            RecipeManager.getRecipes().registerRecipe(r);
            
            if(time > lastDisplay + 1000)
            {
                Messages.send(sender, ChatColor.GRAY + "Step 3/3 " + ChatColor.RESET + "Registering recipes " + ((processed * 100) / size) + "%...");
                lastDisplay = time;
            }
            
            processed++;
        }
        */
    }
    
    protected void queueRecipe(BaseRecipe recipe, String adder)
    {
        if(recipe instanceof CraftRecipe)
            queueCraftRecipe((CraftRecipe)recipe, adder);
        
        else if(recipe instanceof CombineRecipe)
            queueCombineRecipe((CombineRecipe)recipe, adder);
        
        else if(recipe instanceof SmeltRecipe)
            queueSmeltRecipe((SmeltRecipe)recipe, adder);
        
        else if(recipe instanceof FuelRecipe)
            queuFuelRecipe((FuelRecipe)recipe, adder);
        
        else
            throw new IllegalArgumentException("Unknown recipe!");
    }
    
    protected void queueCraftRecipe(CraftRecipe recipe, String adder)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and exacly 9 ingredient slots, empty ones can be null.");
        
        queuedRecipes.remove(recipe); // if exists, update key too !
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queueCombineRecipe(CombineRecipe recipe, String adder)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and ingredient!");
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queueSmeltRecipe(SmeltRecipe recipe, String adder)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs a result and ingredient!");
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queuFuelRecipe(FuelRecipe recipe, String adder)
    {
        if(registered)
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        
        if(!recipe.isValid())
            throw new IllegalArgumentException("Recipe is invalid ! Needs an ingredient!");
        
        /*
        fuels.put(Tools.convertItemToStringID(recipe.getIngredient()), recipe);
        */
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void registerRecipesToServer(CommandSender sender, long start, Set<String> adders)
    {
        if(registered)
            throw new IllegalAccessError("This class is already registered, create a new one!");
        
        Map<BaseRecipe, RecipeInfo> copyRecipes = new HashMap<BaseRecipe, RecipeInfo>();
        Set<BaseRecipe> removeRecipes = new HashSet<BaseRecipe>();
        
        Iterator<Entry<BaseRecipe, RecipeInfo>> iterator;
        Entry<BaseRecipe, RecipeInfo> entry;
        RecipeInfo info;
        BaseRecipe recipe;
        boolean needFurnaceWorker = !FurnaceWorker.isRunning();
        
        // ---
        
        if(adders != null && !adders.isEmpty() && RecipeManager.getRecipes() != null)
        {
            iterator = RecipeManager.getRecipes().index.entrySet().iterator();
            
            while(iterator.hasNext())
            {
                entry = iterator.next();
                info = entry.getValue();
                
                if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                    continue; // skip unowned recipes
                    
                if(adders.contains(info.getAdder()))
                {
                    // recipe was defined in reloaded file, queue for removal in case it's not found defined in the file
                    removeRecipes.add(entry.getKey());
                }
                else
                {
                    // unchanged recipe
                    
                    // check if recipe has custom smelt time
                    if(!needFurnaceWorker)
                    {
                        recipe = entry.getKey();
                        
                        if(recipe instanceof SmeltRecipe && ((SmeltRecipe)recipe).hasCustomTime())
                            needFurnaceWorker = true;
                    }
                    
                    copyRecipes.put(entry.getKey(), info); // save recipe
                }
            }
        }
        
        // ---
        
        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Processed recipes: " + queuedRecipes.size());
        
        iterator = queuedRecipes.entrySet().iterator();
        
        boolean remove;
        boolean add;
        long lastDisplay = System.currentTimeMillis();
        long time;
        int processed = 0;
        int size = queuedRecipes.size();
        
        while(iterator.hasNext())
        {
            entry = iterator.next();
            info = entry.getValue();
            
            if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                continue;
            
            recipe = entry.getKey();
            add = !recipe.hasFlag(FlagType.REMOVE);
            remove = !add || recipe.hasFlag(FlagType.OVERRIDE) || removeRecipes.remove(recipe); // overriden recipe OR removed recipe (and also remove it from list)
            
            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.GREEN + "RECIPE = " + recipe.getRecipeType());
            
            if(remove)
            {
                Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Removing recipe...");
                
                if(!recipe.remove())
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RED + "Couldn't find shaped recipe to remove!");
            }
            
            if(add)
            {
                Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Registering recipe...");
                
                RecipeManager.getRecipes().registerRecipe(recipe, info);
            }
            
            time = System.currentTimeMillis();
            
            if(time > lastDisplay + 1000)
            {
                Messages.send(sender, ChatColor.GRAY + "Step 2/3 " + ChatColor.RESET + "Converting recipes " + ((processed * 100) / size) + "%...");
                lastDisplay = time;
            }
            
            processed++;
        }
        
        if(!removeRecipes.isEmpty())
        {
            Iterator<BaseRecipe> it = removeRecipes.iterator();
            
            while(it.hasNext())
            {
                it.next().remove();
            }
            
            removeRecipes.clear();
        }
        
        // Start/restart/stop the furnace worker task
        if(needFurnaceWorker)
            FurnaceWorker.start();
        
        registered = true; // mark this class as registered so it doesn't get re-registered
        queuedRecipes.clear(); // clear the queue to let the class vanish
    }
    
    protected void aaaaaaaaaaaaaa(CommandSender sender, long start, Set<String> adders)
    {
        if(registered)
            throw new IllegalAccessError("This class is already registered, create a new one!");
        
        iterator = queuedRecipes.entrySet().iterator();
        
        /*
        if(RecipeManager.getSettings().MULTITHREADING && size > 500)
        {
            task = Bukkit.getScheduler().runTaskTimer(RecipeManager.getPlugin(), this, 0, 2);
        }
        else
        {
            task = null;
            
            while(!recipes.isEmpty())
            {
                run();
            }
        }
        */
        
        Map<BaseRecipe, RecipeInfo> copyRecipes = new HashMap<BaseRecipe, RecipeInfo>();
        Set<BaseRecipe> removeRecipes = new HashSet<BaseRecipe>();
        
        Iterator<Entry<BaseRecipe, RecipeInfo>> iterator;
        Entry<BaseRecipe, RecipeInfo> entry;
        RecipeInfo info;
        BaseRecipe recipe;
        boolean needFurnaceWorker = false;
        
        // ---
        
        if(adders != null && !adders.isEmpty() && RecipeManager.getRecipes() != null)
        {
            iterator = RecipeManager.getRecipes().index.entrySet().iterator();
            
            while(iterator.hasNext())
            {
                entry = iterator.next();
                info = entry.getValue();
                
                if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                    continue; // skip unowned recipes
                    
                if(adders.contains(info.getAdder()))
                {
                    // recipe was defined in reloaded file, queue for removal in case it's not found defined in the file
                    removeRecipes.add(entry.getKey());
                }
                else
                {
                    // unchanged recipe
                    
                    // check if recipe has custom smelt time
                    if(!needFurnaceWorker)
                    {
                        recipe = entry.getKey();
                        
                        if(recipe instanceof SmeltRecipe && ((SmeltRecipe)recipe).getMinTime() >= 0)
                            needFurnaceWorker = true;
                    }
                    
                    copyRecipes.put(entry.getKey(), info); // save recipe
                }
            }
        }
        
        // ---
        
        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Processed recipes: " + queuedRecipes.size());
        
        iterator = queuedRecipes.entrySet().iterator();
        
        boolean remove;
        boolean add;
        CraftRecipe cr;
        CombineRecipe co;
        SmeltRecipe sm;
        
        long lastDisplay = System.currentTimeMillis();
        long time;
        int processed = 0;
        int size = queuedRecipes.size();
        
        final Queue<Recipe> recipeQueue = new LinkedList<Recipe>();
        
        while(iterator.hasNext())
        {
            entry = iterator.next();
            info = entry.getValue();
            
            if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                continue;
            
            recipe = entry.getKey();
            add = !recipe.hasFlag(FlagType.REMOVE);
            remove = !add || recipe.hasFlag(FlagType.OVERRIDE) || removeRecipes.remove(recipe); // overriden recipe OR removed recipe (and also remove it from list)
            
            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.GREEN + "RECIPE = " + recipe.getRecipeType());
            
            if(recipe instanceof CraftRecipe)
            {
                cr = (CraftRecipe)recipe;
                
                if(remove)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Removing recipe...");
                    
                    if(!Vanilla.removeCraftRecipe(cr))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RED + "Couldn't find shaped recipe to remove!");
                }
                
                if(add)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Adding recipe to queue...");
                    
                    recipeQueue.offer(cr.toShapedRecipe());
                }
            }
            else if(recipe instanceof CombineRecipe)
            {
                co = (CombineRecipe)recipe;
                
                if(remove)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Removing recipe...");
                    
                    // TODO remove debug ?
                    if(!Vanilla.removeCombineRecipe(co))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RED + "Couldn't find shapeless recipe to remove!");
                }
                
                if(add)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Adding recipe to queue...");
                    
                    recipeQueue.offer(co.toShapelessRecipe());
                }
            }
            else if(recipe instanceof SmeltRecipe)
            {
                sm = (SmeltRecipe)recipe;
                
                if(remove)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Removing recipe...");
                    
                    // TODO remove debug ?
                    if(!Vanilla.removeSmeltRecipe(sm))
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RED + "Couldn't find furnace recipe to remove!");
                }
                
                if(add)
                {
                    Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Adding recipe to queue...");
                    
                    recipeQueue.offer(sm.toFurnaceRecipe());
                    
                    if(!needFurnaceWorker && sm.getMinTime() >= 0)
                    {
                        Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.LIGHT_PURPLE + "needFurnaceWorker set to true");
                        needFurnaceWorker = true;
                    }
                }
            }
            
            time = System.currentTimeMillis();
            
            if(time > lastDisplay + 1000)
            {
                Messages.send(sender, ChatColor.GRAY + "Step 2/3 " + ChatColor.RESET + "Converting recipes " + ((processed * 100) / size) + "%...");
                lastDisplay = time;
            }
            
            processed++;
        }
        
        /*
        if(!removeRecipes.isEmpty())
        {
            Iterator<BaseRecipe> it = removeRecipes.iterator();
            
            while(it.hasNext())
            {
                BukkitRecipes.removeBukkitRecipe(it.next());
            }
            
            removeRecipes.clear();
        }
        */
        
        if(!recipeQueue.isEmpty())
        {
            Messages.info(ChatColor.RED + "[DEBUG] " + ChatColor.RESET + "Registering recipes: " + recipeQueue.size());
            
//            new RecipeRegistrator(sender, start, recipeQueue, new LinkedList<BaseRecipe>(removeRecipes));
        }
        
        if(!copyRecipes.isEmpty())
        {
            copyRecipes.putAll(queuedRecipes);
            queuedRecipes.clear();
            queuedRecipes.putAll(copyRecipes);
        }
        
//        RecipeManager.recipes = this;
        registered = true;
        
        if(needFurnaceWorker)
            FurnaceWorker.start();
        else
            FurnaceWorker.stop();
    }
    
    // Public API methods
    
    /**
     * Adds recipe to queue.<br>
     * Once you've added all recipes you need, use {@link #registerRecipesToServer()}
     * 
     * @param recipe
     *            RecipeManager recipe
     */
    public void queueRecipe(BaseRecipe recipe)
    {
        queueRecipe(recipe, RecipeManager.getPlugin().getPluginCaller("queueRecipe"));
    }
    
    /**
     * Adds recipe to queue.<br>
     * Once you've added all recipes you need, use {@link #registerRecipesToServer()}
     * 
     * @param recipe
     *            RecipeManager recipe
     */
    public void queueCraftRecipe(CraftRecipe recipe)
    {
        queueCraftRecipe(recipe, RecipeManager.getPlugin().getPluginCaller("queueCraftRecipe"));
    }
    
    /**
     * Adds recipe to queue.<br>
     * Once you've added all recipes you need, use {@link #registerRecipesToServer()}
     * 
     * @param recipe
     *            RecipeManager recipe
     */
    public void queueCombineRecipe(CombineRecipe recipe)
    {
        queueCombineRecipe(recipe, RecipeManager.getPlugin().getPluginCaller("queueCombineRecipe"));
    }
    
    /**
     * Adds recipe to queue.<br>
     * Once you've added all recipes you need, use {@link #registerRecipesToServer()}
     * 
     * @param recipe
     *            RecipeManager recipe
     */
    public void queueSmeltRecipe(SmeltRecipe recipe)
    {
        queueSmeltRecipe(recipe, RecipeManager.getPlugin().getPluginCaller("queueSmeltRecipe"));
    }
    
    /**
     * Adds recipe to queue.<br>
     * Once you've added all recipes you need, use {@link #registerRecipesToServer()}
     * 
     * @param recipe
     *            RecipeManager recipe
     */
    public void queuFuelRecipe(FuelRecipe recipe)
    {
        queuFuelRecipe(recipe, RecipeManager.getPlugin().getPluginCaller("queuFuelRecipe"));
    }
    
    /**
     * Adds all queued recipes to the server.
     */
    public void registerRecipesToServer()
    {
        if(registered)
            throw new IllegalAccessError("This class is already registered, create a new one!");
        
        Bukkit.getScheduler().runTask(RecipeManager.getPlugin(), new Runnable()
        {
            public void run()
            {
                Messages.info(ChatColor.GOLD + "registerRecipesToServer()");
                
                String adder = RecipeManager.getPlugin().getPluginCaller("registerRecipesToServer");
                
                if(adder != null)
                {
                    Set<String> set = new HashSet<String>();
                    set.add(adder);
                    registerRecipesToServer(null, System.currentTimeMillis(), set);
                }
                else
                    registerRecipesToServer(null, System.currentTimeMillis(), null);
            }
        });
    }
}
