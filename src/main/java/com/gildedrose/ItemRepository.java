package com.gildedrose;

import java.util.List;

public interface ItemRepository {

    List<Item> findAll();

    void saveAll(List<Item> items);

    void add(Item item);

    boolean remove(Item item);
}
