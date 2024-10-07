package net.recondev.commons.utils;

import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class LocationSerializer {

    public static String serialize(final Location location){
        final String serialize;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(location);
            bukkitObjectOutputStream.flush();
            final byte[] serializedObject = byteArrayOutputStream.toByteArray();
            serialize = new String(Base64.getEncoder().encode(serializedObject));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return serialize;
    }

    public static Location deserialize(final String stringLocation) {
        final byte[] deSerializedObject = Base64.getDecoder().decode(stringLocation);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deSerializedObject);
        try {
            final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            return (Location) bukkitObjectInputStream.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}