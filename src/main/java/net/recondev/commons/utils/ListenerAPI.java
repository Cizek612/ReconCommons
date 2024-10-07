package net.recondev.commons.utils;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.function.Consumer;

public class ListenerAPI {

    public static <T extends Event> void registerListener(final EventPriority priority, final Class<T> eventType, final Consumer<T> action, final Plugin plugin) {
        final Listener listener = new Listener() {};
        final RegisteredListener registeredListener = new RegisteredListener(listener, (event, executor) -> {
            @SuppressWarnings("unchecked")
            final T castedEvent = (T) event;
            action.accept(castedEvent);
        }, priority, plugin, false);

        final HandlerList handlerList = new HandlerList();
        handlerList.register(registeredListener);


        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

    }
}