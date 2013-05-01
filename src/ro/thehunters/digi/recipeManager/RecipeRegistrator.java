package ro.thehunters.digi.recipeManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.CombineRecipe;
import ro.thehunters.digi.recipeManager.recipes.CraftRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.ItemResult;
import ro.thehunters.digi.recipeManager.recipes.MultiResultRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeStatus;
import ro.thehunters.digi.recipeManager.recipes.SingleResultRecipe;
import ro.thehunters.digi.recipeManager.recipes.SmeltRecipe;

import com.google.common.collect.Sets;

public class RecipeRegistrator implements Runnable
{
    protected Map<BaseRecipe, RecipeInfo> queuedRecipes = new HashMap<BaseRecipe, RecipeInfo>();
    private boolean registered = false;
    
    /*
    private final CommandSender                     sender;
    
    private Iterator<Entry<BaseRecipe, RecipeInfo>> iterator      = null;
    
    private int                                     size;
    private long                                    start;
    private int                                     processed     = 0;
    private long                                    time;
    private long                                    lastDisplay;
    private int                                     temp;
    
    private BaseRecipe r;
    */
    
    private static BukkitTask task;
    
    protected RecipeRegistrator(CommandSender sender)
    {
//        this.sender = sender;
        
        if(task != null)
        {
            task.cancel();
        }
    }
    
    // TODO register in chunks
    
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
        {
            queueCraftRecipe((CraftRecipe)recipe, adder);
        }
        else if(recipe instanceof CombineRecipe)
        {
            queueCombineRecipe((CombineRecipe)recipe, adder);
        }
        else if(recipe instanceof SmeltRecipe)
        {
            queueSmeltRecipe((SmeltRecipe)recipe, adder);
        }
        else if(recipe instanceof FuelRecipe)
        {
            queuFuelRecipe((FuelRecipe)recipe, adder);
        }
        else
        {
            throw new IllegalArgumentException("Unknown recipe!");
        }
    }
    
    protected void queueCraftRecipe(CraftRecipe recipe, String adder)
    {
        if(registered)
        {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }
        
        if(!recipe.isValid())
        {
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and exacly 9 ingredient slots, empty ones can be null.");
        }
        
        queuedRecipes.remove(recipe); // if exists, update key too !
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queueCombineRecipe(CombineRecipe recipe, String adder)
    {
        if(registered)
        {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }
        
        if(!recipe.isValid())
        {
            throw new IllegalArgumentException("Recipe is invalid ! Needs at least one result and ingredient!");
        }
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queueSmeltRecipe(SmeltRecipe recipe, String adder)
    {
        if(registered)
        {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }
        
        if(!recipe.isValid())
        {
            throw new IllegalArgumentException("Recipe is invalid ! Needs a result and ingredient!");
        }
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void queuFuelRecipe(FuelRecipe recipe, String adder)
    {
        if(registered)
        {
            throw new IllegalAccessError("You can't add recipes after registering this class! You must create a new one.");
        }
        
        if(!recipe.isValid())
        {
            throw new IllegalArgumentException("Recipe is invalid ! Needs an ingredient!");
        }
        
        queuedRecipes.remove(recipe);
        queuedRecipes.put(recipe, new RecipeInfo(RecipeOwner.RECIPEMANAGER, adder, RecipeStatus.QUEUED));
    }
    
    protected void registerRecipesToServer(CommandSender sender, long start, Set<String> changedFiles)
    {
        if(registered)
        {
            throw new IllegalAccessError("This class is already registered, create a new one!");
        }
        
//        Messages.debug("adders " + (changedFiles == null ? "n/a" : changedFiles.size() + ":" + ArrayUtils.toString(changedFiles)));
        
        Map<BaseRecipe, RecipeInfo> copyRecipes = new HashMap<BaseRecipe, RecipeInfo>();
        Set<BaseRecipe> removeRecipes = new HashSet<BaseRecipe>();
        
        Iterator<Entry<BaseRecipe, RecipeInfo>> iterator;
        Entry<BaseRecipe, RecipeInfo> entry;
        RecipeInfo info;
        BaseRecipe recipe;
        boolean needFurnaceWorker = !FurnaceWorker.isRunning();
        
        // ---
        
        if(RecipeManager.getRecipes() != null && changedFiles != null)
        {
            iterator = RecipeManager.getRecipes().index.entrySet().iterator();
            
//            Messages.debug("processing changed files...");
            
            while(iterator.hasNext())
            {
                entry = iterator.next();
                info = entry.getValue();
                
                if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
                {
                    continue; // skip unowned recipes
                }
                
                if(changedFiles.contains(info.getAdder()))
                {
//                    Messages.debug("changed '" + info.getAdder() + "': " + entry.getKey().getType());
                    
                    // recipe was defined in a changed file, queue for removal in case it's not found defined in the file
                    removeRecipes.add(entry.getKey());
                }
                else
                {
                    // unchanged recipe
                    
//                    Messages.debug("not changed '" + info.getAdder() + "': " + entry.getKey().getType());
                    
                    // check if recipe has custom smelt time
                    if(!needFurnaceWorker)
                    {
                        recipe = entry.getKey();
                        
                        if(recipe instanceof SmeltRecipe && ((SmeltRecipe)recipe).hasCustomTime())
                        {
                            needFurnaceWorker = true;
                        }
                    }
                    
                    copyRecipes.put(entry.getKey(), info); // save recipe
                }
            }
        }
        
        // ---
        
//        Messages.debug("Processed recipes: " + queuedRecipes.size());
        
        iterator = queuedRecipes.entrySet().iterator();
        
        boolean remove;
        boolean add;
        long lastDisplay = System.currentTimeMillis();
        long time;
        int processed = 0;
        int size = queuedRecipes.size();
        int addedNum = 0;
        int removedNum = 0;
        
        // TODO fix removed!!!!!!!! it does not remove recipes that are removed from files
        
        while(iterator.hasNext())
        {
            entry = iterator.next();
            info = entry.getValue();
            
            if(info.getOwner() != RecipeOwner.RECIPEMANAGER)
            {
                continue;
            }
            
            recipe = entry.getKey();
            add = !recipe.hasFlag(FlagType.REMOVE);
            remove = !add || recipe.hasFlag(FlagType.OVERRIDE) || removeRecipes.remove(recipe); // overriden recipe OR removed recipe (and also remove it from list)
            
//            Messages.debug("recipe = " + recipe.getType());
            
            if(remove)
            {
//                Messages.debug("Removing recipe...");
                
                if(!recipe.remove())
                {
                    Messages.debug("Couldn't find shaped recipe to remove: " + recipe.getName());
                }
            }
            
            if(add)
            {
//                Messages.debug("Registering recipe...");
                
                RecipeManager.getRecipes().registerRecipe(recipe, info);
                addedNum++;
            }
            
            time = System.currentTimeMillis();
            
            if(time > lastDisplay + 1000)
            {
                Messages.send(sender, String.format("%sRegistering recipes %d%%...", ChatColor.YELLOW, ((processed * 100) / size)));
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
                removedNum++;
            }
            
            removeRecipes.clear();
        }
        
        RecipeManager.getRecipeBooks().reload(sender); // (Re)Create recipe books for recipes
        
        if(needFurnaceWorker) // Start the furnace worker task if it's not running and it's now needed
        {
            FurnaceWorker.start();
        }
        
        registered = true; // mark this class as registered so it doesn't get re-registered
        queuedRecipes.clear(); // clear the queue to let the class vanish
        
        for(BaseRecipe r : RecipeManager.getRecipes().index.keySet())
        {
            if(r.hasFlags())
            {
                r.getFlags().sendRegistered();
            }
            
            if(r instanceof SingleResultRecipe)
            {
                SingleResultRecipe rec = (SingleResultRecipe)r;
                ItemResult result = rec.getResult();
                
                if(result.hasFlags())
                {
                    result.getFlags().sendRegistered();
                }
            }
            else if(r instanceof MultiResultRecipe)
            {
                MultiResultRecipe rec = (MultiResultRecipe)r;
                
                for(ItemResult result : rec.getResults())
                {
                    if(result.hasFlags())
                    {
                        result.getFlags().sendRegistered();
                    }
                }
            }
        }
        
        Messages.send(sender, String.format("All done in %.3f seconds, %d recipes added, %d removed.", ((System.currentTimeMillis() - start) / 1000.0), addedNum, removedNum));
    }
    
    /*
     *  Public API methods
     */
    
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
        {
            throw new IllegalAccessError("This class is already registered, create a new one!");
        }
        
        if(RecipeManager.getPlugin() == null)
        {
            Messages.debug("plugin = null, wait for post-enable !");
            
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    registerRecipes();
                }
            }.runTaskLater(RecipeManager.getPlugin(), 1);
        }
        else
        {
            registerRecipes();
        }
    }
    
    private void registerRecipes()
    {
        Messages.debug("... ?");
        
        String adder = RecipeManager.getPlugin().getPluginCaller("registerRecipesToServer");
        
        registerRecipesToServer(null, System.currentTimeMillis(), (adder == null ? null : Sets.newHashSet(adder)));
    }
}
