package haveric.recipeManager.flags;

import haveric.recipeManager.Files;

public class FlagAddToBook extends Flag {
    private static final FlagType TYPE = FlagType.ADDTOBOOK;

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <book id> [volume <num>]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "This flag is a shortcut for quickly adding recipe(s) to books.",
            "Using this flag more than once will overwrite the previous one.",
            "",
            "The book must exist first, you must create it, see '" + Files.FILE_INFO_BOOKS + "' for how to do that.",
            "",
            "The '<book id>' argument must be an existing book's ID/filename, case insensitive.",
            "Optionally you can specify which volume to add it to, otherwise it will be added in its 'recipes' node and left to be added automatically to the latest volume with free slots.",
            "",
            "NOTE: To properly remove recipes from books you must first remove this flag (to avoid re-adding them) then go to the book's YML file and remove them from there as well.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} testing book // matches a 'Testing Book.yml' book for example",
            "{flag} random stuff volume 3 // matches a 'Random Stuff.yml' with volume 3 book for example", };
    }


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
        super.clone();
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
     * Set the book name, will be forced as lowercase.<br>
     * Setting this to null, "false" or "remove" will remove the flag from its container.
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
    public void setVolume(int newVolume) {
        volume = newVolume;
    }

    @Override
    protected boolean onParse(String value) {
        value = value.toLowerCase();
        String newBookName = value;
        int index = value.lastIndexOf("volume");

        if (index > 0) {
            value = value.substring(index + "volume".length()).trim();

            try {
                setVolume(Integer.parseInt(value));
                newBookName = newBookName.substring(0, index).trim();
            } catch (NumberFormatException e) {
                // TODO: Handle error
            }
        }

        setBookName(newBookName);

        return true;
    }
}
