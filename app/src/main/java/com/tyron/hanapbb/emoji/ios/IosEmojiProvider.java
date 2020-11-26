package com.tyron.hanapbb.emoji.ios;

import androidx.annotation.NonNull;
import com.tyron.hanapbb.emoji.EmojiProvider;
import com.tyron.hanapbb.emoji.EmojiCategory;
import com.tyron.hanapbb.emoji.ios.category.ActivitiesCategory;
import com.tyron.hanapbb.emoji.ios.category.AnimalsAndNatureCategory;
import com.tyron.hanapbb.emoji.ios.category.FlagsCategory;
import com.tyron.hanapbb.emoji.ios.category.FoodAndDrinkCategory;
import com.tyron.hanapbb.emoji.ios.category.ObjectsCategory;
import com.tyron.hanapbb.emoji.ios.category.SmileysAndPeopleCategory;
import com.tyron.hanapbb.emoji.ios.category.SymbolsCategory;
import com.tyron.hanapbb.emoji.ios.category.TravelAndPlacesCategory;

public final class IosEmojiProvider implements EmojiProvider {
    @Override @NonNull public EmojiCategory[] getCategories() {
        return new EmojiCategory[] {
                new SmileysAndPeopleCategory(),
                new AnimalsAndNatureCategory(),
                new FoodAndDrinkCategory(),
                new ActivitiesCategory(),
                new TravelAndPlacesCategory(),
                new ObjectsCategory(),
                new SymbolsCategory(),
                new FlagsCategory()
        };
    }
}