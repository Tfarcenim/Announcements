package tfar.announcements;

import net.minecraft.core.IdMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.Stream;

public record EnumIdMapper<E extends Enum<E>>(E[] enumConstants) implements IdMap<E> {

    public EnumIdMapper(Class<E> enumConstants) {
        this(enumConstants.getEnumConstants());
    }

    @Override
    public int getId(E value) {
        return value.ordinal();
    }

    @Override
    public @Nullable E byId(int id) {
        return enumConstants[id];
    }

    @Override
    public int size() {
        return enumConstants.length;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return Stream.of(enumConstants).iterator();
    }
}
