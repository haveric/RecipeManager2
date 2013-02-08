package digi.recipeManager.data;

import java.io.Serializable;

public class BlockFurnaceData implements Serializable
{
    private static final long serialVersionUID = 6139071604622897062L;
    
    private String            smelter;
    private String            fueler;
    
    public BlockFurnaceData(String smelter, String fueler)
    {
        this.smelter = smelter;
        this.fueler = fueler;
    }
    
    public String getFueler()
    {
        return fueler;
    }
    
    public void setFueler(String fueler)
    {
        this.fueler = fueler;
    }
    
    public String getSmelter()
    {
        return smelter;
    }
    
    public void setSmelter(String smelter)
    {
        this.smelter = smelter;
    }
}