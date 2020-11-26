package com.tyron.hanapbb.emoji.listeners;

import androidx.annotation.NonNull;
import com.tyron.hanapbb.emoji.Emoji;
import com.tyron.hanapbb.ui.components.EmojiImageView;

public interface OnEmojiLongClickListener {
    void onEmojiLongClick(@NonNull EmojiImageView view, @NonNull Emoji emoji);
}