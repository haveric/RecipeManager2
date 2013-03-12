package ro.thehunters.digi.recipeManager.application;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Application extends JFrame
{
    private static final long serialVersionUID = -2555279008210071964L;
    
    private File              dir;
    private JTree             file_tree;
    private JTable            table;
    
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
        tabbedPane.addTab("Files and Folders", (Icon)null, tab_files, "Browse recipes and folders");
        tab_files.setLayout(null);
        
        JButton button_refresh = new JButton("Refresh");
        button_refresh.setBounds(354, 227, 125, 26);
        tab_files.add(button_refresh);
        
        JButton btnOpen = new JButton("Open");
        btnOpen.setBounds(354, 12, 125, 26);
        tab_files.add(btnOpen);
        
        JButton btnNewButton = new JButton("New recipe file");
        btnNewButton.setBounds(354, 149, 125, 26);
        tab_files.add(btnNewButton);
        
        JButton button_newFolder = new JButton("New folder");
        button_newFolder.setBounds(354, 188, 125, 26);
        button_newFolder.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
            }
        });
        tab_files.add(button_newFolder);
        
        JTextArea txtrOpenARecipe = new JTextArea();
        txtrOpenARecipe.setBounds(354, 50, 125, 92);
        tab_files.add(txtrOpenARecipe);
        txtrOpenARecipe.setWrapStyleWord(true);
        txtrOpenARecipe.setTabSize(2);
        txtrOpenARecipe.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtrOpenARecipe.setEditable(false);
        txtrOpenARecipe.setBackground(UIManager.getColor("Button.background"));
        txtrOpenARecipe.setText("Double-click to open.\r\nDrag & drop to move.\r\nF2 to rename.\r\nDelete to disable.");
        
        JButton button_changeDirectory = new JButton("Change directory");
        button_changeDirectory.setBounds(354, 266, 125, 26);
        button_changeDirectory.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDirectoryDialog(false);
            }
        });
        tab_files.add(button_changeDirectory);
        
        JScrollPane files_scroll = new JScrollPane();
        files_scroll.setBounds(12, 12, 328, 280);
        tab_files.add(files_scroll);
        
        file_tree = new JTree();
        file_tree.setVisibleRowCount(1000);
        file_tree.setEditable(true);
        files_scroll.setViewportView(file_tree);
        
        TreeNode root = new DefaultMutableTreeNode("Recipes");
        
        file_tree.setModel(new DefaultTreeModel(root));
        
        JPopupMenu menu = new JPopupMenu();
        addPopup(file_tree, menu);
        
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
        tabbedPane.addTab("File Recipes", null, tab_recipes, "You need to select a file to access this!");
        tabbedPane.setEnabledAt(1, true);
        tab_recipes.setLayout(null);
        
        JScrollPane recipes_scroll = new JScrollPane();
        recipes_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        recipes_scroll.setBounds(10, 11, 334, 282);
        tab_recipes.add(recipes_scroll);
        
        JList recipes_list = new JList();
        recipes_scroll.setViewportView(recipes_list);
        recipes_list.setModel(new AbstractListModel()
        {
            String[] values = new String[] { "aaa", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "aaaaa", "", "", "aaaaaaaa" };
            
            public int getSize()
            {
                return values.length;
            }
            
            public Object getElementAt(int index)
            {
                return values[index];
            }
        });
        
        JButton btnNewButton_5 = new JButton("New Recipe");
        btnNewButton_5.setBounds(354, 11, 125, 26);
        tab_recipes.add(btnNewButton_5);
        
        JButton btnEditRecipe = new JButton("Edit Recipe");
        btnEditRecipe.setBounds(354, 48, 125, 26);
        tab_recipes.add(btnEditRecipe);
        
        JButton btnDeleteRecipes = new JButton("Delete Recipe(s)");
        btnDeleteRecipes.setBounds(354, 159, 125, 26);
        tab_recipes.add(btnDeleteRecipes);
        
        JButton btnBackToFiles = new JButton("Back to files");
        btnBackToFiles.setBounds(354, 267, 125, 26);
        tab_recipes.add(btnBackToFiles);
        
        JButton btnCutRecipes = new JButton("Move Recipe(s)");
        btnCutRecipes.setBounds(354, 122, 125, 26);
        tab_recipes.add(btnCutRecipes);
        
        JTabbedPane tab_editor = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("Recipe Editor", null, tab_editor, null);
        tabbedPane.setEnabledAt(2, true);
        
        JPanel editor_tab_craft = new JPanel();
        tab_editor.addTab("Craft (shaped)", null, editor_tab_craft, null);
        tab_editor.setEnabledAt(0, true);
        editor_tab_craft.setLayout(null);
        
        JPanel craft_grid = new JPanel();
        craft_grid.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Ingredients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        craft_grid.setBounds(10, 11, 158, 168);
        editor_tab_craft.add(craft_grid);
        craft_grid.setLayout(new GridLayout(0, 3, 0, 0));
        
        JButton craft_grid_slot1 = new JButton("");
        craft_grid.add(craft_grid_slot1);
        
        JButton craft_grid_slot2 = new JButton("");
        craft_grid.add(craft_grid_slot2);
        
        JButton craft_grid_slot3 = new JButton("");
        craft_grid.add(craft_grid_slot3);
        
        JButton craft_grid_slot4 = new JButton("");
        craft_grid.add(craft_grid_slot4);
        
        JButton craft_grid_slot5 = new JButton("");
        craft_grid.add(craft_grid_slot5);
        
        JButton craft_grid_slot6 = new JButton("");
        craft_grid.add(craft_grid_slot6);
        
        JButton craft_grid_slot7 = new JButton("");
        craft_grid.add(craft_grid_slot7);
        
        JButton craft_grid_slot8 = new JButton("");
        craft_grid.add(craft_grid_slot8);
        
        JButton craft_grid_slot9 = new JButton("");
        craft_grid.add(craft_grid_slot9);
        
        JPanel recipe_flags = new JPanel();
        recipe_flags.setBorder(new TitledBorder(null, "Recipe Flags", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        recipe_flags.setBounds(178, 11, 296, 168);
        recipe_flags.setLayout(null);
        editor_tab_craft.add(recipe_flags);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(6, 29, 284, 132);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        recipe_flags.add(scrollPane);
        
        table = new JTable();
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        scrollPane.setViewportView(table);
        table.setModel(new DefaultTableModel(new Object[][] { { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, { Boolean.TRUE, null, null }, }, new String[] { "", "", "" })
        {
            Class[] columnTypes = new Class[] { Boolean.class, String.class, Object.class };
            
            public Class getColumnClass(int columnIndex)
            {
                return columnTypes[columnIndex];
            }
        });
        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(15);
        table.getColumnModel().getColumn(1).setResizable(false);
        table.getColumnModel().getColumn(1).setPreferredWidth(185);
        table.getColumnModel().getColumn(2).setResizable(false);
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JButton btnNewButton_4 = new JButton("Add Flag");
        btnNewButton_4.setBounds(100, 11, 90, 16);
        recipe_flags.add(btnNewButton_4);
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(200, 11, 90, 16);
        recipe_flags.add(btnDelete);
        
        JPanel editor_tab_combine = new JPanel();
        tab_editor.addTab("Combine (shapeless)", null, editor_tab_combine, null);
        editor_tab_combine.setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Ingredients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(10, 11, 180, 254);
        editor_tab_combine.add(panel);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        
        JButton button = new JButton("");
        panel.add(button);
        
        JButton button_1 = new JButton("");
        panel.add(button_1);
        
        JButton button_2 = new JButton("");
        panel.add(button_2);
        
        JButton button_3 = new JButton("");
        panel.add(button_3);
        
        JButton button_4 = new JButton("");
        panel.add(button_4);
        
        JButton button_5 = new JButton("");
        panel.add(button_5);
        
        JButton button_6 = new JButton("");
        panel.add(button_6);
        
        JButton button_7 = new JButton("");
        panel.add(button_7);
        
        JButton button_8 = new JButton("");
        panel.add(button_8);
        
        JPanel combine_flags = new JPanel();
        combine_flags.setBounds(200, 11, 274, 131);
        editor_tab_combine.add(combine_flags);
        combine_flags.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Recipe Flags", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        combine_flags.setLayout(null);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBounds(6, 16, 262, 16);
        combine_flags.add(panel_2);
        panel_2.setLayout(null);
        
        JButton btnNewButton_7 = new JButton("Add Flag");
        btnNewButton_7.setBounds(162, 0, 100, 16);
        panel_2.add(btnNewButton_7);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBorder(null);
        scrollPane_1.setBounds(6, 36, 262, 88);
        combine_flags.add(scrollPane_1);
        
        JPanel panel_1 = new JPanel();
        scrollPane_1.setViewportView(panel_1);
        panel_1.setLayout(new GridLayout(6, 0, 0, 0));
        
        JButton btnNewButton_8 = new JButton("permission my.awesome.node");
        panel_1.add(btnNewButton_8);
        btnNewButton_8.setHorizontalAlignment(SwingConstants.LEADING);
        
        JPanel editor_tab_smelt = new JPanel();
        tab_editor.addTab("Smelt (furnace)", null, editor_tab_smelt, null);
        
        JPanel editor_tab_fuel = new JPanel();
        tab_editor.addTab("Fuel (furnace)", null, editor_tab_fuel, null);
        
        JPanel editor_tab_remove = new JPanel();
        tab_editor.addTab("Remove Result", null, editor_tab_remove, null);
        
        JPanel tab_help = new JPanel();
        tab_help.setToolTipText("Displays the information files already provided in the plugin's directory.");
        tabbedPane.addTab("Help and Information", null, tab_help, null);
        tab_help.setLayout(null);
        
        JPanel help_tutorials = new JPanel();
        help_tutorials.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Tutorial & Information files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        help_tutorials.setBounds(10, 11, 230, 282);
        tab_help.add(help_tutorials);
        help_tutorials.setLayout(null);
        
        JButton btnNewButton_1 = new JButton("Basic recipes");
        btnNewButton_1.setBounds(10, 21, 210, 23);
        help_tutorials.add(btnNewButton_1);
        
        JButton btnRecipeFlags = new JButton("Advanced recipes - recipe flags");
        btnRecipeFlags.setBounds(10, 55, 210, 23);
        help_tutorials.add(btnRecipeFlags);
        
        JButton btnNamesitemsEnchants = new JButton("Name index");
        btnNamesitemsEnchants.setBounds(10, 89, 210, 23);
        help_tutorials.add(btnNamesitemsEnchants);
        
        JButton btnRecipeErrorsExplained = new JButton("Recipe errors explained");
        btnRecipeErrorsExplained.setBounds(10, 123, 210, 23);
        help_tutorials.add(btnRecipeErrorsExplained);
        
        JButton btnQuestionsAnswers = new JButton("Questions & Answers");
        btnQuestionsAnswers.setBounds(10, 157, 210, 23);
        help_tutorials.add(btnQuestionsAnswers);
        
        JButton btnPossibleGlitches = new JButton("Possible Glitches");
        btnPossibleGlitches.setBounds(10, 191, 210, 23);
        help_tutorials.add(btnPossibleGlitches);
        
        JPanel help_plugin = new JPanel();
        help_plugin.setBorder(new TitledBorder(null, "Plugin information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        help_plugin.setBounds(249, 11, 230, 282);
        tab_help.add(help_plugin);
        help_plugin.setLayout(null);
        
        JButton btnBukkitdevPage = new JButton("BukkitDev page");
        btnBukkitdevPage.setBounds(10, 123, 210, 23);
        help_plugin.add(btnBukkitdevPage);
        
        JButton btnNewButton_2 = new JButton("Change log");
        btnNewButton_2.setBounds(10, 89, 210, 23);
        help_plugin.add(btnNewButton_2);
        
        JTextPane txtpnRecipeManagerV = new JTextPane();
        txtpnRecipeManagerV.setFont(new Font("Verdana", Font.PLAIN, 14));
        txtpnRecipeManagerV.setEditable(false);
        txtpnRecipeManagerV.setText("RecipeManager v2.0\r\nCreated by Digi");
        txtpnRecipeManagerV.setBackground(UIManager.getColor("Button.background"));
        txtpnRecipeManagerV.setBounds(10, 21, 210, 57);
        help_plugin.add(txtpnRecipeManagerV);
        
        JButton btnNewButton_3 = new JButton("Report issue or suggestions");
        btnNewButton_3.setBounds(10, 157, 210, 23);
        help_plugin.add(btnNewButton_3);
        
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
        file_tree.setModel(model);
        
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
        
        file_tree.expandPath(new TreePath(node));
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
