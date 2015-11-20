package haveric.recipeManager.flags;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagBookItem extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} title [text]",
            "{flag} author [text]",
            "{flag} addpage [text]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes book's contents.",
            "Using this flag more than once will configure the same flag.",
            "",
            "Supports colors and format (e.g. <red>, <blue>, &4, &F, etc).",
            "",
            "Use 'title <text>' and 'author <text>' only on written books, it doesn't work on book and quill therefore they're optional.",
            "Title and author must not exceed 64 characters, colors included (2 chars each).",
            "",
            "Use 'addpage <text>' to add a new page, the text can contain \\n to add new lines to it, but it mainly word-wraps itself.",
            "Page contents must not exceed 256 characters, colors (2 chars each) and new line (1 char each) included.",
            "Optionally you can leave the text blank to add a blank page.",
            "",
            "Supported items: written book, book and quill.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} title The Art of Stealing",
            "{flag} author Gray Fox",
            "{flag} addpage <bold>O<reset>nce upon a time...",
            "{flag} addpage // added blank page",
            "{flag} addpage \\n\\n\\n\\n<italic>      The End.", };
    }


    private String title;
    private String author;
    private List<String> pages = new ArrayList<String>(50);

    public FlagBookItem() {
    }

    public FlagBookItem(FlagBookItem flag) {
        title = flag.title;
        author = flag.author;
        pages.addAll(flag.pages);
    }

    @Override
    public FlagBookItem clone() {
        super.clone();
        return new FlagBookItem(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = RMCUtil.parseColors(newTitle, false);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String newAuthor) {
        author = RMCUtil.parseColors(newAuthor, false);
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> newPages) {
        Validate.notNull(newPages, "The 'pages' argument must not be null!");

        pages.clear();

        for (String page : newPages) {
            addPage(page);
        }
    }

    public void addPage(String page) {
        pages.add(RMCUtil.parseColors(page.replace("\\n", "\n"), false));
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || !(result.getItemMeta() instanceof BookMeta)) {
            ErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK or BOOK_AND_QUILL item!");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        int i = value.indexOf(' ');
        String key;

        if (i >= 0) {
            key = value.substring(0, i).trim().toLowerCase();
            value = value.substring(i).trim();
        } else {
            key = value.toLowerCase();
            value = "";
        }

        ItemStack result = getResult();
        boolean setTitle = key.equals("title");
        boolean setAuthor = !setTitle && key.equals("author");

        if (setTitle || setAuthor) {
            if (result.getType() == Material.BOOK_AND_QUILL) {
                ErrorReporter.warning("Flag " + getType() + " can not have title or author set on BOOK_AND_QUILL, only WRITTEN_BOOK.");
                return true;
            }

            if (value.length() > 64) {
                ErrorReporter.warning("Flag " + getType() + " has '" + (setTitle ? "title" : "author") + "' with over 64 characters, trimmed.");
                value = value.substring(0, 64);
            }

            if (setTitle) {
                setTitle(value);
            } else {
                setAuthor(value);
            }
        } else if (key.equals("addpage")) {
            if (pages.size() == 50) {
                ErrorReporter.warning("Flag " + getType() + " has over 50 pages added, they will be trimmed.");
            }

            if (value.length() > 256) {
                ErrorReporter.warning("Flag " + getType() + " has 'addpage' with over 256 characters! It will be trimmed.");
            }

            addPage(value);
        }

        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();

        if (!(meta instanceof BookMeta)) {
            a.addCustomReason("Needs BookMeta supported item!");
            return;
        }

        BookMeta bookMeta = (BookMeta) meta;

        bookMeta.setTitle(title);
        bookMeta.setAuthor(author);
        bookMeta.setPages(pages);

        a.result().setItemMeta(bookMeta);
    }
}
