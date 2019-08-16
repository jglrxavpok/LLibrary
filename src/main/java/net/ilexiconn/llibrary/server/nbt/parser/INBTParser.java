package net.ilexiconn.llibrary.server.nbt.parser;

import net.minecraft.nbt.INBTBase;

/**
 * @author iLexiconn
 * @since 1.1.0
 */
public interface INBTParser<V, T extends INBTBase> {
    V parseTag(T tag);

    T parseValue(V value);
}
