package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlagCommands extends Flag
{
    private List<String> commands = new ArrayList<String>();
    
    public FlagCommands()
    {
        type = FlagType.COMMANDS;
    }
    
    public FlagCommands(FlagCommands flag)
    {
        this();
        
        commands.addAll(flag.commands);
    }
    
    @Override
    public FlagCommands clone()
    {
        return new FlagCommands(this);
    }
    
    /**
     * @return the command list
     */
    public List<String> getCommands()
    {
        return commands;
    }
    
    /**
     * Set the command list.<br>
     * You can use null to remove the entire flag.
     * 
     * @param commands
     */
    public void setCommands(List<String> commands)
    {
        if(commands == null)
            remove();
        else
            this.commands = commands;
    }
    
    /**
     * Adds a command to the list.<br>
     * You can use "false" or "remove" to remove the entire flag.
     * 
     * @param command
     */
    public void addCommand(String command)
    {
        if(command == null || command.equalsIgnoreCase("false") || command.equalsIgnoreCase("remove"))
        {
            this.remove();
        }
        else
        {
            commands.add(command);
        }
    }
    
    @Override
    protected boolean onParse(String value)
    {
        addCommand(value);
        return true;
    }
    
    @Override
    protected boolean onCrafted(Args a)
    {
        Player p = a.player();
        
        for(String command : commands)
        {
            command = a.parseVariables(command);
            
            if(command.charAt(0) == '/')
            {
                if(p != null)
                {
                    a.player().chat(command);
                }
            }
            else
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        
        return true;
    }
}