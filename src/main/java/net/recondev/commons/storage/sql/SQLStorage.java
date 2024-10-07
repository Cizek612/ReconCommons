package net.recondev.commons.storage.sql;

import lombok.SneakyThrows;
import net.recondev.commons.ReconCommons;
import net.recondev.commons.patterns.Registry;
import net.recondev.commons.storage.Storage;
import net.recondev.commons.storage.id.utils.IdFinder;

import java.sql.*;
import java.util.Collection;

@SuppressWarnings({"unused", "unchecked"})
public abstract class SQLStorage<K, V> implements Storage<K, V> {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String tableName;
    private final Class<V> type;
    private final Registry<K, V> registry;

    @SneakyThrows
    public SQLStorage(final String jdbcUrl, final String username, final String password, final String tableName, final Class<V> type, final Registry<K, V> registry) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        this.type = type;
        this.registry = registry;

        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.registerDriver(new  com.mysql.cj.jdbc.Driver());


        try (final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
             final PreparedStatement createTableStatement = connection.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS " + this.tableName + " (key VARCHAR(255) PRIMARY KEY, value VARCHAR(8000))"
             )) {
            createTableStatement.executeUpdate();
        }


        try (final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
             final PreparedStatement selectAllStatement = connection.prepareStatement(
                     "SELECT * FROM " + this.tableName
             );

             final ResultSet resultSet = selectAllStatement.executeQuery()) {

             while (resultSet.next()) {
                final String key = resultSet.getString("key");
                final String jsonValue = resultSet.getString("value");
                final V value = ReconCommons.getGson().fromJson(jsonValue, this.type);
                this.registry.register((K) key, value);
            }
        }
    }

    @Override
    public boolean contains(final K key) {
        return this.registry.containsKey(key);
    }

    @Override
    public Registry<K, V> registry() {
        return this.registry;
    }

    @Override
    public Collection<K> keys() {
        return this.registry.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.registry.values();
    }


    @Override @SneakyThrows
    public V get(final K key) {
        if (this.registry.containsKey(key)) {
            return this.registry.getRegistry().get(key);
        } else {
            try (final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
                 final PreparedStatement selectStatement = connection.prepareStatement(
                         "SELECT value FROM " + this.tableName + " WHERE key = ?"
                 )) {
                selectStatement.setString(1, key.toString());
                try (final ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        final String jsonValue = resultSet.getString("value");
                        return ReconCommons.getGson().fromJson(jsonValue, this.type);
                    }
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void remove(final K key) {
        this.registry.unregister(key);

        try (final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
             final PreparedStatement deleteStatement = connection.prepareStatement(
                     "DELETE FROM " + this.tableName + " WHERE key = ?"
             )) {
             deleteStatement.setString(1, key.toString());
             deleteStatement.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void save(final V value) {
        final String jsonValue = ReconCommons.getGson().toJson(value);
        try (final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
             final PreparedStatement insertStatement = connection.prepareStatement(
                     "INSERT INTO " + this.tableName + " (key, value) VALUES (?, ?)"
             )) {
            insertStatement.setString(1, (String) IdFinder.getId(this.type, value));
            insertStatement.setString(2, jsonValue);
            insertStatement.executeUpdate();
        }
        this.registry.register((K) IdFinder.getId(this.type, value), value);
    }

    @Override
    public void saveAll(final Collection<V> collection) {
        for (final V value : collection) {
            this.save(value);
        }
    }

    @Override
    public void write() {}
}