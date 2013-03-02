package ro.thehunters.digi.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import ro.thehunters.digi.recipeManager.Messages;

public class FlagCommands extends Flag
{
    private List<String> commands = new ArrayList<String>();
    
    public FlagCommands()
    {
        type = FlagType.COMMANDS;
    }
    
    @Override
    public FlagCommands clone()
    {
        FlagCommands clone = new FlagCommands();
        
        clone.commands.addAll(commands);
        
        return clone;
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
            removeFlag();
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
        if(command.equalsIgnoreCase("false") || command.equalsIgnoreCase("remove"))
            removeFlag();
        else
            commands.add(command);
    }
    
    @Override
    public boolean onParse(String value)
    {
        addCommand(value);
        return true;
    }
    
    @Override
    public void onApply(Arguments a)
    {
        for(String command : commands)
        {
            Messages.debug("preparing command '" + command + "'...");
            
            command = a.parseVariables(command);
            
            if(command.charAt(0) == '/')
            {
                if(a.player() != null)
                {
                    a.player().chat(command);
                    Messages.debug("command sent to player " + a.playerName() + ": " + command);
                }
                else
                {
                    Messages.debug("player is null, can't send command:" + command);
                }
            }
            else
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                Messages.debug("command sent to server: " + command);
            }
        }
    }
}