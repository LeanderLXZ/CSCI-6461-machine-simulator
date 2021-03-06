package main.java.memory;

/**
 * @author jalal
 * @since 29/9/19
 * <p>
 * Cache Lines of cache.
 * Each of this contains 8 words.
 */
public class CacheLine {

    protected static final int MAX_WORD = 8;

    private boolean valid;
    private int tag;
    private Word[] words;

    public CacheLine(int tag) {
        this.valid = true;
        this.tag = tag;

        fetWords(tag);
    }

    protected boolean isValid() {
        return valid;
    }

    protected boolean isTagMatch(int tag) {
        return this.tag == tag;
    }

    protected Word getWord(int index) {
        return words[index];
    }

    protected void setWord(Address address, Word word) {
        this.words[address.getOffset()] = word;

        Memory.memoryMap.put(address.getTag() * MAX_WORD + address.getOffset(), word);
    }

    protected void fetWords(int tag) {
        this.words = new Word[MAX_WORD];

        for (int i = 0; i < MAX_WORD; i++) {
            int address = (tag * MAX_WORD) + i;

            this.words[i] = Memory.memoryMap.get(address);
        }
    }
}
