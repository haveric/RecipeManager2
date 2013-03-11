package ro.thehunters.digi.recipeManager.application.old;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class FileTree extends DefaultTreeModel
{
    private static final long serialVersionUID = 2170103700046465818L;
    private File              dir;
    
    public FileTree(File dir)
    {
        super(scanDirectory(dir, new DefaultMutableTreeNode("recipes")));
        this.dir = dir;
    }
    
    private static TreeNode scanDirectory(File dir, DefaultMutableTreeNode parent)
    {
        for(File file : dir.listFiles())
        {
            if(file.isDirectory())
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
                parent.add(node);
                scanDirectory(file, node);
            }
            else if(file.getName().endsWith(".txt"))
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
                parent.add(node);
            }
        }
        
        return parent;
    }
    
    public void refresh()
    {
        setRoot(scanDirectory(dir, new DefaultMutableTreeNode("recipes")));
    }
}
