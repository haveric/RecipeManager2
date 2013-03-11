package ro.thehunters.digi.recipeManager.application;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Application extends JFrame
{
    private static final long serialVersionUID = -2555279008210071964L;
    
    private File              dir;
    private JTree             tree;
    
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    Application window = new Application();
                    window.setVisible(true);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public Application()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
        }
        
        setTitle("RecipeManager UI");
        setResizable(false);
        setBounds(100, 100, 500, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(0, 0, 494, 332);
        getContentPane().add(tabbedPane);
        
        JPanel tab_files = new JPanel();
        tabbedPane.addTab("Files and folders", (Icon)null, tab_files, null);
        tab_files.setLayout(null);
        
        JPanel right_panel = new JPanel();
        right_panel.setBorder(null);
        right_panel.setBounds(352, 12, 125, 280);
        tab_files.add(right_panel);
        right_panel.setLayout(null);
        
        JButton button_refresh = new JButton("Refresh");
        button_refresh.setBounds(0, 215, 125, 26);
        right_panel.add(button_refresh);
        
        JButton btnOpen = new JButton("Open");
        btnOpen.setBounds(0, 0, 125, 26);
        right_panel.add(btnOpen);
        
        JButton btnNewButton = new JButton("New recipe file");
        btnNewButton.setBounds(0, 137, 125, 26);
        right_panel.add(btnNewButton);
        
        JButton button_newFolder = new JButton("New folder");
        button_newFolder.setBounds(0, 176, 125, 26);
        button_newFolder.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
            }
        });
        right_panel.add(button_newFolder);
        
        JTextArea txtrOpenARecipe = new JTextArea();
        txtrOpenARecipe.setBounds(0, 38, 125, 116);
        right_panel.add(txtrOpenARecipe);
        txtrOpenARecipe.setWrapStyleWord(true);
        txtrOpenARecipe.setTabSize(2);
        txtrOpenARecipe.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtrOpenARecipe.setEditable(false);
        txtrOpenARecipe.setBackground(UIManager.getColor("Button.background"));
        txtrOpenARecipe.setText("Double-click to open.\r\nDrag & drop to move.\r\nF2 to rename.\r\nDelete to disable.");
        
        JButton button_changeDirectory = new JButton("Change directory");
        button_changeDirectory.setBounds(0, 254, 125, 26);
        button_changeDirectory.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDirectoryDialog(false);
            }
        });
        right_panel.add(button_changeDirectory);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 12, 328, 280);
        tab_files.add(scrollPane);
        
        tree = new JTree();
        tree.setVisibleRowCount(1000);
        tree.setEditable(true);
        scrollPane.setViewportView(tree);
        
        TreeNode root = new DefaultMutableTreeNode("Recipes");
        
        tree.setModel(new DefaultTreeModel(root));
        
        JPopupMenu menu = new JPopupMenu();
        addPopup(tree, menu);
        
        JMenuItem mntmOpen = new JMenuItem("Open");
        menu.add(mntmOpen);
        
        JMenuItem menuItem = new JMenuItem("");
        menu.add(menuItem);
        
        JMenuItem mntmCopy = new JMenuItem("Copy");
        menu.add(mntmCopy);
        
        JMenuItem mntmPaste = new JMenuItem("Paste");
        mntmPaste.setEnabled(false);
        menu.add(mntmPaste);
        
        JMenuItem mntmMove = new JMenuItem("Move");
        menu.add(mntmMove);
        
        JMenuItem mntmRename = new JMenuItem("Rename");
        menu.add(mntmRename);
        
        JMenuItem mntmDelete = new JMenuItem("Delete");
        menu.add(mntmDelete);
        
        JMenuItem menuItem_1 = new JMenuItem("");
        menu.add(menuItem_1);
        
        JMenuItem menu_newRecipe = new JMenuItem("New recipe file");
        menu.add(menu_newRecipe);
        
        JMenuItem menu_newFolder = new JMenuItem("New folder");
        menu.add(menu_newFolder);
        
        JPanel tab_recipes = new JPanel();
        tabbedPane.addTab("File recipe list", null, tab_recipes, "You need to select a file to access this!");
        tabbedPane.setEnabledAt(1, false);
        
        JPanel tab_designer = new JPanel();
        tabbedPane.addTab("Recipe designer", null, tab_designer, "You need to select a recipe to access this!");
        tabbedPane.setEnabledAt(2, false);
        
        dir = new File("plugins/RecipeManager/recipes");
        
        if(!dir.exists())
        {
            openDirectoryDialog(true);
        }
        else
        {
            setDirectory(dir);
        }
    }
    
    private void setDirectory(File dir)
    {
        this.dir = dir;
        refreshDirectory();
    }
    
    public void refreshDirectory()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getPath());
        DefaultTreeModel model = new DefaultTreeModel(node);
        tree.setModel(model);
        
        for(File file : dir.listFiles())
        {
            if(file.isDirectory())
            {
                
            }
            else if(file.getName().endsWith(".txt"))
            {
                node.add(new DefaultMutableTreeNode(file.getName()));
            }
        }
        
        tree.expandPath(new TreePath(node));
    }
    
    private void openDirectoryDialog(boolean required)
    {
        if(required)
            JOptionPane.showMessageDialog(this, "Could not find '" + dir.getPath() + "'.\nYou need to pick a directory...", "Message", JOptionPane.WARNING_MESSAGE);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int action = chooser.showOpenDialog(this);
        
        if(action == JFileChooser.APPROVE_OPTION)
        {
            setDirectory(chooser.getSelectedFile());
        }
        else if(required)
        {
            JOptionPane.showMessageDialog(this, "Can't continue without a starting directory.");
            System.exit(0);
        }
    }
    
    private static void addPopup(Component component, final JPopupMenu popup)
    {
        component.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if(e.isPopupTrigger())
                {
                    showMenu(e);
                }
            }
            
            public void mouseReleased(MouseEvent e)
            {
                if(e.isPopupTrigger())
                {
                    showMenu(e);
                }
            }
            
            private void showMenu(MouseEvent e)
            {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
}
