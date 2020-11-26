package com.tyron.hanapbb.emoji.listeners;

import androidx.annotation.NonNull;
import com.tyron.hanapbb.ui.components.EmojiImageView;
import com.tyron.hanapbb.emoji.Emoji;

public interface OnEmojiClickListener {
    void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView);
}
