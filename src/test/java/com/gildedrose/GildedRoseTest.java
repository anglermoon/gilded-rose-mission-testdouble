package com.gildedrose;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    @Test
    void foo() {
        ItemRepository repository = new InMemoryItemRepository(new Item("foo", 0, 0));
        GildedRose app = new GildedRose(repository);

        app.updateQuality();

        List<Item> items = repository.findAll();
        assertEquals("foo", items.get(0).name);
    }

}
