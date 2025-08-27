package com.cccstudio.sky_core.api;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;

public class CustomHolderSet<T> extends HolderSet.ListBacked<T> {

    private final List<Holder<T>> VALUES;

    public CustomHolderSet(List<Holder<T>> content) {
        VALUES = content;
    }

    @Override
    protected @NotNull List<Holder<T>> contents() {
        return VALUES;
    }

    @Override
    public @NotNull Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(VALUES);
    }

    @Override
    public boolean contains(@NotNull Holder<T> holder) {
        return VALUES.contains(holder);
    }

    @Override
    public @NotNull Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }
}
