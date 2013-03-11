package ro.thehunters.digi.recipeManager.application.old;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

public class Main
{
    private JFrame   window;
    private FileTree fileTree;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    Main window = new Main();
                    window.window.setVisible(true);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Create the application.
     */
    public Main()
    {
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        try
        {
            // TODO test on other platforms
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); // UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        window = new JFrame();
        window.setResizable(false);
        window.setTitle("RecipeManager UI");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/resources/icon.png")));
        window.setBounds(100, 100, 900, 700);
        window.getContentPane().setLayout(null);
        
        window.getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        window.getRootPane().getActionMap().put("refresh", new AbstractAction()
        {
            private static final long serialVersionUID = 3643754092256845041L;
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileTree.refresh();
            }
        });
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Recipe editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(319, 11, 565, 389);
        window.getContentPane().add(panel);
        
        JPanel craft_matrix = new JPanel();
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(craft_matrix, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE).addContainerGap(363, Short.MAX_VALUE)));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addComponent(craft_matrix, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE).addContainerGap(138, Short.MAX_VALUE)));
        craft_matrix.setLayout(new GridLayout(3, 3, 0, 0));
        
        ActionListener action = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Action on " + e.getActionCommand() + " | " + e.paramString() + " | " + e.getID());
            }
        };
        
        JButton slot1 = new JButton("");
        craft_matrix.add(slot1);
        slot1.setToolTipText("");
        slot1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot2 = new JButton("");
        craft_matrix.add(slot2);
        slot2.setToolTipText("");
        slot2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot3 = new JButton("");
        craft_matrix.add(slot3);
        slot3.setToolTipText("");
        slot3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot5 = new JButton("");
        craft_matrix.add(slot5);
        slot5.setToolTipText("");
        slot5.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot6 = new JButton("");
        craft_matrix.add(slot6);
        slot6.setToolTipText("");
        slot6.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot7 = new JButton("");
        craft_matrix.add(slot7);
        slot7.setToolTipText("");
        slot7.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot8 = new JButton("");
        craft_matrix.add(slot8);
        slot8.setToolTipText("");
        slot8.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot9 = new JButton("");
        craft_matrix.add(slot9);
        slot9.setToolTipText("");
        slot9.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JButton slot4 = new JButton("");
        craft_matrix.add(slot4);
        slot4.setToolTipText("");
        slot4.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel.setLayout(gl_panel);
        
        slot1.addActionListener(action);
        slot2.addActionListener(action);
        slot3.addActionListener(action);
        slot4.addActionListener(action);
        slot5.addActionListener(action);
        slot6.addActionListener(action);
        slot7.addActionListener(action);
        slot8.addActionListener(action);
        slot9.addActionListener(action);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new TitledBorder(null, "Recipes in file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        scrollPane.setBounds(319, 411, 565, 250);
        window.getContentPane().add(scrollPane);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setString("Loading recipes... 0%");
        progressBar.setStringPainted(true);
        scrollPane.setViewportView(progressBar);
        
        JPanel file_tree = new JPanel();
        file_tree.setBorder(new TitledBorder(null, "Recipe files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        file_tree.setBounds(10, 11, 299, 650);
        window.getContentPane().add(file_tree);
        
        JScrollPane tree_scroll = new JScrollPane();
        tree_scroll.setBounds(10, 50, 279, 589);
        
        JTree tree = new JTree();
        tree_scroll.setViewportView(tree);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setBackground(Color.WHITE);
        tree.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tree.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        tree.setVisibleRowCount(1000);
        
        File dir = new File("D:/Dev/Minecraft/Server/plugins/RecipeManager/recipes");
        fileTree = new FileTree(dir);
        tree.setModel(fileTree);
        tree.setEditable(true);
        
        JButton buttonRefresh = new JButton("Refresh");
        buttonRefresh.setBounds(209, 16, 80, 23);
        buttonRefresh.setToolTipText("(F5)");
        file_tree.setLayout(null);
        
        JButton buttonNew = new JButton("New");
        buttonNew.setBounds(10, 16, 60, 23);
        
        file_tree.add(buttonNew);
        file_tree.add(buttonRefresh);
        file_tree.add(tree_scroll);
    }
}
