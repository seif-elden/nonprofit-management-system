package com.nonprofit.model.persistence;

import com.nonprofit.model.common.Identifiable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic data-access object: one reusable class gives CRUD
 * (Create, Read, Update, Delete) for every kind of stored record,
 * including the editable lookup tables.
 *
 * @param <T> the kind of record stored
 */
public class GenericDAO<T extends Identifiable> {

    private String tableName;
    private List<T> items;

    public GenericDAO(String tableName) {
        this.tableName = tableName;
        this.items = new ArrayList<>();
    }

    // Create
    public int insert(T item) {
        items.add(item);
        return item.getId();
    }

    // Read
    public T findById(int id) {
        for (T item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public List<T> findAll() {
        return items;
    }

    // Update: replaces the stored record that has the same id
    public boolean update(T item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == item.getId()) {
                items.set(i, item);
                return true;
            }
        }
        return false;
    }

    // Delete
    public boolean delete(int id) {
        T item = findById(id);
        return item != null && items.remove(item);
    }

    public String getTableName() {
        return tableName;
    }
}
