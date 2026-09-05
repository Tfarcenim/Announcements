package tfar.announcements;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum TextPosition {
    CENTER,ACTION_BAR;
    public static final IntFunction<TextPosition> BY_ID = ByIdMap.continuous(Enum::ordinal,
            values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, TextPosition> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
}
