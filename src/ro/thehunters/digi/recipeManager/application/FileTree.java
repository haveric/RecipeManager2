package ro.thehunters.digi.recipeManager.application;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;

public class FileTree extends JTree
{
    private static final long serialVersionUID = 2170103700046465818L;
    private TreeNode          root;
    private Application       app;
    
    public FileTree(Application app)
    {
        /*
        this.app = app;
        this.root = new DefaultMutableTreeNode("Recipes");

        setModel(new DefaultTreeModel(root));
        
        File dir = new File("plugins/RecipeManager/recipes");
        
        if(!dir.exists())
        {
            JFileChooser chooser = new JFileChooser();
            
            int returned = chooser.showOpenDialog(this.app);
            
            if(returned == JFileChooser.APPROVE_OPTION)
            {
                dir = chooser.getSelectedFile();
                
                System.out.print("dir = " + dir);
            }
        }
        */
        
    }
}
