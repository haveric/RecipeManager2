package haveric.recipeManager.flags;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.data.RecipeBook;
import haveric.recipeManager.recipes.ItemResult;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;


public class FlagGetRecipeBook extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.GETRECIPEBOOK;
    protected static final String[] A = new String[] {
        "{flag} <book id> [volume <num>]", };

    protected static final String[] D = new String[] {
        "Overwrites result with the specified recipe book.",
        "",
        "For the '<book id>' argument you need to specify the book ID/filename, case insensitive.",
        "",
        "Optionally you can set which volume to give, will give first by default, using a bigger number than the number of volumes will pick the last volume.", };

    protected static final String[] E = new String[] {
        "{flag} recipe stuff // matches a 'Recipe Stuff.yml' book for example.",
        "{flag} vanilla_recipes volume 2 // matches a 'vanilla_recipes.yml' with volume 2 for example.", };


    // Flag code

    private String bookID;
    private int volume = 1;

    public FlagGetRecipeBook() {
    }

    public FlagGetRecipeBook(FlagGetRecipeBook flag) {
        bookID = flag.bookID;
        volume = flag.volume;
    }

    @Override
    public FlagGetRecipeBook clone() {
        super.clone();
        return new FlagGetRecipeBook(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String id) {
        Validate.notNull(id, "The 'id' argument must not be null!");
        bookID = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int newVolume) {
        volume = Math.max(newVolume, 1);
    }

    @Override
    protected boolean onValidate() {
        ItemResult result = getResult();

        if (result == null || result.getType() != Material.WRITTEN_BOOK) {
            return ErrorReporter.error("Flag " + getType() + " needs a WRITTEN_BOOK to work!");
        }

        return true;
    }

    @Override
    protected boolean onParse(String value) {
        value = value.toLowerCase();
        String id = value;
        int index = value.lastIndexOf("volume");

        if (index > 0) {
            value = value.substring(index + "volume".length()).trim();

            try {
                setVolume(Integer.parseInt(value));
                id = id.substring(0, index).trim();
            } catch (NumberFormatException e) {
                // TODO: Handle exception
            }
        }

        setBookID(id);

        return true;
    }

    @Override
    protected void onRegistered() {
        List<RecipeBook> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(getBookID());

        if (books.isEmpty()) {
            ErrorReporter.warning("Flag " + getType() + " could not find book ID containing '" + bookID + "', flag ignored.");
            remove();
            return;
        }

        RecipeBook book = books.get(0);

        if (books.size() > 1) {
            ErrorReporter.warning("Flag " + getType() + " found " + books.size() + " books matching ID '" + bookID + "', using first: " + book.getTitle());
        }
    }

    @Override
    protected void onPrepare(Args a) {
        if (getBookID() == null) {
            a.addCustomReason("Book ID not set!");
            return;
        }

        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        List<RecipeBook> books = RecipeManager.getRecipeBooks().getBooksPartialMatch(getBookID());

        if (books.isEmpty()) {
            return;
        }

        RecipeBook book = books.get(0);

        a.result().setItemMeta(book.getBookItem(volume).getItemMeta());
    }
}
