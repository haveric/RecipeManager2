package haveric.recipeManager.flag.flags.result.meta;

import com.google.common.base.Preconditions;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Files;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagBookItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.BOOK_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} title [text]",
            "{flag} author [text]",
            "{flag} generation <generation>",
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
            "Use generation <generation> to set the book's generation. Defaults to ORIGINAL",
            "  <generation> values: " + RMCUtil.collectionToString(Arrays.asList(BookMeta.Generation.values())).toLowerCase(),
            "",
            "Use 'addpage <text>' to add a new page, the text can contain \\n to add new lines to it, but it mainly word-wraps itself.",
            "Page contents must not exceed 256 characters, colors (2 chars each) and new line (1 char each) included.",
            "Optionally you can leave the text blank to add a blank page.",
            "",
            "Supported items: written book, book and quill.",
            "",
            "Allows quotes to prevent spaces being trimmed.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} title The Art of Stealing",
            "{flag} author Gray Fox",
            "{flag} generation COPY_OF_ORIGINAL",
            "{flag} addpage <bold>O<reset>nce upon a time...",
            "{flag} addpage // added blank page",
            "{flag} addpage \\n\\n\\n\\n<italic>      The End.",
            "{flag} title \" The Art of Stealing \" // Quotes at the beginning and end will be removed, but spaces will be kept.",
            "{flag} author \"   Gray Fox   \"",
            "{flag} addpage \" <bold>O<reset>nce upon a time... \"", };
    }


    private String title;
    private String author;
    private BookMeta.Generation generation;
    private List<String> pages = new ArrayList<>(50);

    public FlagBookItem() {
    }

    public FlagBookItem(FlagBookItem flag) {
        super(flag);
        title = flag.title;
        author = flag.author;
        generation = flag.generation;
        pages.addAll(flag.pages);
    }

    @Override
    public FlagBookItem clone() {
        return new FlagBookItem((FlagBookItem) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
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

    public BookMeta.Generation getGeneration() {
        return generation;
    }

    public void setGeneration(BookMeta.Generation newGeneration) {
        generation = newGeneration;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> newPages) {
        Preconditions.checkNotNull(newPages, "The 'pages' argument must not be null!");

        pages.clear();

        for (String page : newPages) {
            addPage(page);
        }
    }

    public void addPage(String page) {
        pages.add(RMCUtil.parseColors(page.replace("\\n", "\n"), false));
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof BookMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), BookMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a WRITTEN_BOOK or BOOK_AND_QUILL item!"); // TODO: BOOK_AND_QUILL is outdated
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
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

        String trimmed = RMCUtil.trimExactQuotes(value);
        if (setTitle || setAuthor) {
            if (result.getType() == Material.WRITABLE_BOOK) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " can not have title or author set on WRITABLE_BOOK, only WRITTEN_BOOK.");
                return true;
            }

            if (trimmed.length() > 64) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has '" + (setTitle ? "title" : "author") + "' with over 64 characters, trimmed.");
                trimmed = trimmed.substring(0, 64);
            }

            if (setTitle) {
                setTitle(trimmed);
            } else {
                setAuthor(trimmed);
            }
        } else if (key.equals("generation")) {
            BookMeta.Generation parsedGeneration = RMCUtil.parseEnum(trimmed, BookMeta.Generation.values());

            if (parsedGeneration == null) {
                generation = BookMeta.Generation.ORIGINAL;
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has invalid generation: " + trimmed + ". Defaulting to ORIGINAL.");
            } else {
                generation = parsedGeneration;
            }
        } else if (key.equals("addpage")) {
            if (pages.size() == 50) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has over 50 pages added, they will be trimmed.");
            }

            if (trimmed.length() > 256) {
                ErrorReporter.getInstance().warning("Flag " + getFlagType() + " has 'addpage' with over 256 characters! It will be trimmed.");
            }

            addPage(trimmed);
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (!(meta instanceof BookMeta)) {
                a.addCustomReason("Needs BookMeta supported item!");
                return;
            }

            BookMeta bookMeta = (BookMeta) meta;

            bookMeta.setTitle(title);
            bookMeta.setAuthor(author);
            bookMeta.setGeneration(generation);
            bookMeta.setPages(pages);

            a.result().setItemMeta(bookMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "title: " + title;
        toHash += "author: " + author;
        toHash += "generation: " + generation;

        for (String page : pages) {
            toHash += page;
        }

        return toHash.hashCode();
    }

    @Override
    public void parseItemMeta(ItemStack item, ItemMeta meta, StringBuilder recipeString) {
        if (meta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) meta;

            if (bookMeta.hasTitle()) {
                recipeString.append(Files.NL).append("@bookitem title ").append(bookMeta.getTitle());
            }

            if (bookMeta.hasAuthor()) {
                recipeString.append(Files.NL).append("@bookitem author ").append(bookMeta.getAuthor());
            }

            if (bookMeta.hasGeneration()) {
                BookMeta.Generation generation = bookMeta.getGeneration();
                if (generation != null) {
                    recipeString.append(Files.NL).append("@bookitem generation ").append(generation.name());
                }
            }

            List<String> pages = bookMeta.getPages();
            for (String page : pages) {
                recipeString.append(Files.NL).append("@bookitem addpage ").append(page);
            }
        }
    }

    // TODO: Add condition support for FlagBookItem
}
