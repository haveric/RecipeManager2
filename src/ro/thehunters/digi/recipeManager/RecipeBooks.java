package ro.thehunters.digi.recipeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ro.thehunters.digi.recipeManager.flags.FlagRecipeBook;
import ro.thehunters.digi.recipeManager.flags.FlagType;
import ro.thehunters.digi.recipeManager.recipes.BaseRecipe;
import ro.thehunters.digi.recipeManager.recipes.FuelRecipe;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo;
import ro.thehunters.digi.recipeManager.recipes.RecipeInfo.RecipeOwner;

public class RecipeBooks
{
    public class BookID
    {
        private String id;
        private String title;
        private String description;
        private int hash;
        
        public BookID(String name)
        {
            setTitle(name);
        }
        
        public BookID(BaseRecipe recipe, RecipeInfo info)
        {
            if(recipe.hasFlag(FlagType.RECIPEBOOK))
            {
                FlagRecipeBook flag = recipe.getFlag(FlagRecipeBook.class);
                
                title = flag.getTitle();
                description = Tools.parseColors(flag.getDescription(), false);
            }
            
            if(title == null)
            {
                title = info.getAdder().toString().toLowerCase();
                
                int start = Math.max(title.lastIndexOf('/'), 0);
                int end = Math.min(title.lastIndexOf('.'), title.length());
                title = title.substring(start, end);
                
                title = WordUtils.capitalize(title);
            }
            
            setTitle(title);
        }
        
        private void setTitle(String title)
        {
            this.title = Tools.parseColors(title, false);
            this.id = ChatColor.stripColor(this.title).toLowerCase().replaceAll("[\\W\\s]+", "");
            this.hash = id.hashCode();
        }
        
        public String getID()
        {
            return id;
        }
        
        public String getTitle()
        {
            return title;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        @Override
        public int hashCode()
        {
            return hash;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if(this == obj)
                return true;
            
            if(obj == null || obj instanceof BookID == false)
                return false;
            
            return obj.hashCode() == hashCode();
        }
    }
    
    public class Book
    {
        private String title;
        private String description;
        private BookMeta[] volumes;
        
        public Book(String title)
        {
            this.title = title;
        }
        
        public Book(String title, String description, BookMeta[] volumes)
        {
            this.title = title;
            this.description = description;
            setVolumes(volumes);
        }
        
        public String getTitle()
        {
            return title;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public BookMeta[] getVolumes()
        {
            return volumes;
        }
        
        public void setVolumes(BookMeta[] volumes)
        {
            this.volumes = volumes;
        }
        
        public ItemStack getBookItem()
        {
            return getBookItem(1);
        }
        
        public ItemStack getBookItem(int volume)
        {
            volume = Math.min(Math.max(volume, 1), volumes.length) - 1;
            
            ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
            
            item.setItemMeta(volumes[volume]);
            
            return item;
        }
    }
    
    private final Map<BookID, Book> books = new HashMap<BookID, Book>();
    private final int generated = (int)(System.currentTimeMillis() / 1000);
    
    // Constants
    public static final String BOOK_MARKER = "RecipeManager";
    
    public static void init()
    {
        if(RecipeManager.recipeBooks != null)
        {
            RecipeManager.recipeBooks.clean();
        }
        
        RecipeManager.recipeBooks = new RecipeBooks();
    }
    
    private RecipeBooks()
    {
        
    }
    
    public void clean()
    {
        books.clear();
    }
    
    public void reload()
    {
        clean();
        
        Map<BookID, Map<Integer, List<BaseRecipe>>> recipeBooks = new HashMap<BookID, Map<Integer, List<BaseRecipe>>>();
        Map<BookID, MutableInt> volumeNum = new HashMap<BookID, MutableInt>();
        Map<BookID, MutableInt> recipeNum = new HashMap<BookID, MutableInt>();
        BookID vanillaBook = new BookID("Vanilla Minecraft Recipes"); // TODO messages.yml
        BookID unknownBook = new BookID("Unknown Plugins Recipes");
        RecipeInfo info;
        BookID id;
        
        for(Entry<BaseRecipe, RecipeInfo> e : RecipeManager.getRecipes().getRecipeList().entrySet())
        {
            info = e.getValue();
            
            if(info.getOwner() == RecipeOwner.RECIPEMANAGER)
            {
                id = new BookID(e.getKey(), e.getValue());
            }
            else if(info.getOwner() == RecipeOwner.MINECRAFT)
            {
                id = vanillaBook;
            }
            else
            {
                id = unknownBook;
            }
            
            Map<Integer, List<BaseRecipe>> volumes = recipeBooks.get(id);
            
            if(volumes == null)
            {
                volumes = new HashMap<Integer, List<BaseRecipe>>();
                recipeBooks.put(id, volumes);
                volumeNum.put(id, new MutableInt(0));
                recipeNum.put(id, new MutableInt(0));
            }
            
            MutableInt vol = volumeNum.get(id);
            List<BaseRecipe> volume = volumes.get(vol.intValue());
            
            if(volume == null)
            {
                volume = new ArrayList<BaseRecipe>();
                volumes.put(vol.intValue(), volume);
            }
            
            volume.add(e.getKey());
            
            MutableInt num = recipeNum.get(id);
            num.increment();
            
            if(num.intValue() > 48)
            {
                num.setValue(0);
                vol.increment();
            }
        }
        
        for(Entry<BookID, Map<Integer, List<BaseRecipe>>> b : recipeBooks.entrySet())
        {
            BookMeta[] volume = new BookMeta[b.getValue().size()];
            id = b.getKey();
            int vol = 0;
            
            for(Entry<Integer, List<BaseRecipe>> v : b.getValue().entrySet())
            {
                BookMeta meta = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
                volume[vol] = meta;
                
                meta.setTitle(id.getTitle() + (volume.length > 1 ? " - " + Messages.RECIPEBOOK_VOLUME.get("{volume}", (vol + 1)) : ""));
                meta.setAuthor(BOOK_MARKER + Tools.hideString(" " + id.getID() + " " + vol + " " + (System.currentTimeMillis() / 1000)));
                
                // Cover page
                
                StringBuilder cover = new StringBuilder(256);
                
                cover.append('\n').append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append(id.getTitle());
                
                if(volume.length > 1)
                {
                    cover.append('\n').append(ChatColor.BLACK).append("        ").append(Messages.RECIPEBOOK_VOLUMEOFVOLUMES.get("{volume}", (vol + 1), "{volumes}", volume.length));
                }
                
                cover.append('\n').append(ChatColor.GRAY).append("        Published by\n          RecipeManager");
                
                if(id.getDescription() != null)
                {
                    cover.append('\n').append(ChatColor.DARK_BLUE).append(id.getDescription());
                }
                
                meta.addPage(cover.toString());
                
                List<StringBuilder> index = new ArrayList<StringBuilder>();
                List<String> pages = new ArrayList<String>();
                int r = 2;
                int i = 0;
                int p = (int)Math.ceil(v.getValue().size() / 13.0) + 2;
                
                index.add(new StringBuilder(256).append(ChatColor.BLACK).append(ChatColor.BOLD).append(ChatColor.UNDERLINE).append("CONTENTS INDEX").append("\n\n").append(ChatColor.BLACK));
                
                List<FuelRecipe> fuels = new ArrayList<FuelRecipe>();
                
                for(BaseRecipe recipe : v.getValue())
                {
                    if(recipe instanceof FuelRecipe)
                    {
                        fuels.add((FuelRecipe)recipe);
                        continue;
                    }
                    
                    index.get(i).append(p++).append(". ").append(recipe.printBookIndex()).append(ChatColor.BLACK).append('\n');
                    
                    if(++r >= 13)
                    {
                        r = 0;
                        i++;
                        index.add(new StringBuilder(256).append(ChatColor.BLACK));
                    }
                    
                    String page = recipe.printBook();
                    
                    if(page.length() >= 255)
                    {
                        int x = page.indexOf('\n', 220);
                        
                        if(x < 0 || x > 255)
                        {
                            x = 255;
                        }
                        
                        pages.add(page.substring(0, x));
                        pages.add(page.substring(x + 1));
                        p++;
                    }
                    else
                    {
                        pages.add(page);
                    }
                }
                
                boolean hasFuels = !fuels.isEmpty();
                
                if(hasFuels)
                {
                    index.get(i).append(p++).append(". ").append("Furnace fuels").append(ChatColor.BLACK).append('\n');
                }
                
                for(StringBuilder s : index)
                {
                    meta.addPage(s.toString());
                }
                
                for(String s : pages)
                {
                    meta.addPage(s);
                }
                
                if(hasFuels)
                {
                    StringBuilder s = null;
                    int f = 0;
                    
                    for(FuelRecipe recipe : fuels)
                    {
                        if(f == 0)
                        {
                            s = new StringBuilder(256);
                            s.append(ChatColor.BLACK).append(ChatColor.BOLD).append("FURNACE FUELS"); // TODO messages.yml
                            s.append('\n');
                        }
                        
                        s.append('\n').append(Tools.printItem(recipe.getIngredient(), ChatColor.BLACK, null, false));
                        
                        if(++f > 10)
                        {
                            meta.addPage(s.toString());
                            f = 0;
                        }
                    }
                    
                    if(f > 0)
                    {
                        meta.addPage(s.toString());
                    }
                }
                
                vol++;
            }
            
            books.put(id, new Book(id.getTitle(), id.getDescription(), volume));
        }
        
        // Update online player's in hand books
        
        for(Player player : Bukkit.getOnlinePlayers())
        {
            updateBook(player, player.getItemInHand());
        }
    }
    
    /**
     * Updates (if available) the supplied book item with the latest changes
     * 
     * @param player
     *            must not be null
     * @param item
     *            must be a written book generated by RecipeManager
     */
    public void updateBook(Player player, ItemStack item)
    {
        if(item == null || item.getType() != Material.WRITTEN_BOOK || !item.hasItemMeta() || !player.hasPermission("recipemanager.updatebooks"))
        {
            return;
        }
        
        BookMeta meta = (BookMeta)item.getItemMeta();
        
        if(!meta.hasAuthor())
        {
            return;
        }
        
        Matcher match = Pattern.compile(BOOK_MARKER + " ([\\d\\w]+) ([0-9]+) ([0-9]+)").matcher(Tools.unhideString(meta.getAuthor()));
        
        if(match.find() && match.groupCount() >= 3)
        {
            try
            {
                String id = match.group(1);
                Book book = getBook(id);
                
                if(book == null)
                {
                    Messages.EVENTS_UPDATEBOOK_EXTINCT.printOnce(player, null, "{title}", meta.getTitle());
                    return;
                }
                
                Integer volume = Integer.valueOf(match.group(2));
                Integer lastUpdate = Integer.valueOf(match.group(3));
                
                if(generated > lastUpdate)
                {
                    item.setItemMeta(book.getVolumes()[volume]);
                    
                    Messages.EVENTS_UPDATEBOOK_DONE.print(player, null, "{title}", book.getTitle());
                }
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public Map<BookID, Book> getBooks()
    {
        return books;
    }
    
    public Book getBook(String name)
    {
        return books.get(new BookID(name));
    }
    
    public ItemStack getBookItem(String name)
    {
        return getBookItem(name, 1);
    }
    
    public List<Book> getBooksPartialMatch(String name)
    {
        BookID id = new BookID(name);
        Book book = books.get(id); // full match first
        
        if(book != null)
        {
            return Arrays.asList(book);
        }
        else
        {
            // partial match
            List<Book> found = new ArrayList<Book>(books.size());
            
            if(id.getID().isEmpty())
            {
                return found;
            }
            
            for(Entry<BookID, Book> e : books.entrySet())
            {
                Messages.debug("id = " + e.getKey().getID() + " & " + id.getID() + " = " + e.getKey().getID().contains(id.getID()));
                if(e.getKey().getID().contains(id.getID()))
                {
                    found.add(e.getValue());
                }
            }
            
            return found;
        }
    }
    
    public ItemStack getBookItem(String name, int volume)
    {
        Book book = getBook(name);
        
        if(book == null)
            return null;
        
        return book.getBookItem(volume);
    }
}