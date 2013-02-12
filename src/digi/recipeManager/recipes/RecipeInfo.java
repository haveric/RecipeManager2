package digi.recipeManager.recipes;

public class RecipeInfo
{
    public enum RecipeStatus
    {
        OVERRIDEN,
        REMOVED;
    }
    
    public enum RecipeOwner
    {
        MINECRAFT("Minecraft"),
        RECIPEMANAGER("RecipeManager"),
        UNKNOWN("Unknown plugin");
        
        private String name;
        
        private RecipeOwner(String name)
        {
            this.name = name;
        }
        
        @Override
        public String toString()
        {
            return name;
        }
    }
    
    private int          index;
    private RecipeOwner  owner;
    private RecipeStatus status;
    
    public RecipeInfo(RecipeOwner owner)
    {
        this.owner = owner;
    }
    
    public RecipeInfo(RecipeOwner owner, int index)
    {
        this.owner = owner;
        this.index = index;
    }
    
    public RecipeInfo(RecipeOwner owner, RecipeStatus status, int index)
    {
        this.owner = owner;
        this.status = status;
        this.index = index;
    }
    
    public RecipeOwner getOwner()
    {
        return owner;
    }
    
    public void setOwner(RecipeOwner owner)
    {
        this.owner = owner;
    }
    
    public RecipeStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(RecipeStatus status)
    {
        this.status = status;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
}
