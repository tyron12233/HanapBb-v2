package com.tyron.hanapbb.emoji;

public interface EmojiEditable extends EmojiDisplayable {
    /** Issues a backspace. */
    void backspace();

    /** Adds the emoji. */
    void input(Emoji emoji);
}