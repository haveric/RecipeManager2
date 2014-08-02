package ro.thehunters.digi.recipeManager.flags;

import ro.thehunters.digi.recipeManager.Files;

public class FlagAddToBook extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE;
    protected static final String[] A;
    protected static final String[] D;
    protected static final String[] E;

    static {
        TYPE = FlagType.ADDTOBOOK;

        A = new String[] { "{flag} <book title> [volume <num>]", };

        D = new String[] { "This flag is a shortcut for quickly adding recipe(s) to books.", "Using this flag more than once will overwrite the previous one.", "", "The book must exist first, you must create it, see '" + Files.FILE_INFO_BOOKS + "' how to do that.", "", "The '<book title>' argument must be an existing book's name, partial matching can be used.", "Optionally you can specify which volume to add it to, otherwise it will be added in its 'recipes' node and left to be added automatically to the latest volume with free slots.", "", "NOTE: To properly remove recipes from books you must first remove this flag (to avoid re-adding them) then go to the book's YML file and remove them from there as well.", };

        E = new String[] { "{flag} testingbook // can match a 'Testing Book' book for example", "{flag} random stuf volume 3 // can match a 'Random Stuff volume 3' book for example", };
    }

    // Flag code

    private String bookName;
    private int volume;

    public FlagAddToBook() {
    }

    public FlagAddToBook(FlagAddToBook flag) {
        bookName = flag.bookName;
        volume = flag.volume;
    }

    @Override
    public FlagAddToBook clone() {
        return new FlagAddToBook(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
    }

    /**
     * @return Book name, always lowercase.
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Set the book name, will be forced as lowercase.<br> Setting this to null, "false" or "remove" will remove the flag from its container.
     *
     * @param name
     */
    public void setBookName(String name) {
        if (name == null || name.equalsIgnoreCase("false") || name.equalsIgnoreCase("remove")) {
            remove();
            return;
        }

        bookName = name.toLowerCase();
    }

    /**
     * @return 1 or more if defined, 0 if not defined.
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Set the book volume to add the recipe too.
     *
     * @param volume
     *            book volume from 1 or 0 to allocate automatically.
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    protected boolean onParse(String value) {
        value = value.toLowerCase();
        String bookName = value;
        int index = value.lastIndexOf("volume");

        if (index > 0) {
            value = value.substring(index + "volume".length()).trim();

            try {
                setVolume(Integer.valueOf(value));
                bookName = bookName.substring(0, index).trim();
            } catch (NumberFormatException e) {
                // TODO: Handle error
            }
        }

        setBookName(bookName);

        return true;
    }
}
