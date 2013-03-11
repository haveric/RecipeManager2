package ro.thehunters.digi.recipeManager.application.old;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Launch_old
{
    private final JFrame     window         = new JFrame("RecipeManager UI");
    
    private final JMenuBar   menu           = new JMenuBar();
    
    private final JMenu      menuFile       = new JMenu("File");
    private final JMenuItem  menuFileOpen   = new JMenuItem("Open");
    private final JMenuItem  menuFileSave   = new JMenuItem("Save");
    private final JMenuItem  menuFileSaveAs = new JMenuItem("Save as...");
    private final JMenuItem  menuFileQuit   = new JMenuItem("Exit");
    
    private final JMenu      menuAbout      = new JMenu("About");
    
    private final JPanel     leftPanel      = new JPanel();
    private final JPanel     rightPanel     = new JPanel();
    
    private final JTextField searchBar      = new JTextField(20);
    
    public Launch_old()
    {
        window.setSize(600, 400);
        window.setVisible(true);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setJMenuBar(menu);
        
        menuFile.add(menuFileOpen);
        menuFile.add(menuFileSave);
        menuFile.add(menuFileSaveAs);
        menuFile.add(menuFileQuit);
        
        menu.add(menuFile);
        menu.add(menuAbout);
        
        Actions actions = new Actions();
        
        menuFileOpen.addActionListener(actions);
        menuFileSave.addActionListener(actions);
        menuFileSaveAs.addActionListener(actions);
        menuFileQuit.addActionListener(actions);
        
        menuAbout.addActionListener(actions);
        
        window.setLayout(new FlowLayout());
        
        window.add(leftPanel);
        window.add(rightPanel);
        window.add(searchBar);
        
        JButton[] slots = new JButton[9];
        
        leftPanel.setSize(350, window.getHeight() - 40);
        
        for(JButton slot : slots)
        {
            slot = new JButton();
            slot.setSize(32, 32);
            leftPanel.add(slot);
        }
        
        leftPanel.setLayout(new GridLayout(3, 3));
    }
    
    private class Actions implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            String cmd = event.getActionCommand();
            
            switch(cmd.charAt(0))
            {
                case 'O':
                {
                    
                    break;
                }
                
                case 'S':
                {
                    if(cmd.length() > 4)
                    {
                        
                    }
                    else
                    {
                        
                    }
                    
                    break;
                }
                
                case 'E':
                {
                    System.exit(0);
                    return;
                }
                
                case 'A':
                {
                    
                    break;
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        new Launch_old();
    }
}
