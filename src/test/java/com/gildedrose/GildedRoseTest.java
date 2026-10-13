package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GildedRoseTest {

    private static final String VEST = "+5 Dexterity Vest";
    private static final String BRIE = "Aged Brie";
    private static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    private ItemRepository repository;

    @BeforeEach
    void setUp() {
        // Replace the slow InMemoryItemRepository with a mock.
        repository = mock(ItemRepository.class);
    }

    // ---------- Normal item ----------

    @Test
    void normalItem_qualityDecreasesByOne_beforeSellDate() {
        assertUpdated(VEST, 10, 20, 9, 19);
    }

    @Test
    void normalItem_qualityDecreasesByOne_whenSellInIsOne() {
        assertUpdated(VEST, 1, 20, 0, 19);
    }

    @Test
    void normalItem_qualityDecreasesByTwo_whenSellInIsZero() {
        assertUpdated(VEST, 0, 20, -1, 18);
    }

    @Test
    void normalItem_qualityDecreasesByTwo_afterSellDate() {
        assertUpdated(VEST, -1, 20, -2, 18);
    }

    @Test
    void normalItem_qualityNeverNegative() {
        assertUpdated(VEST, 5, 0, 4, 0);
    }

    @Test
    void normalItem_qualityBecomesZero_whenQualityIsOneAndDecreasesByTwo() {
        assertUpdated(VEST, 0, 1, -1, 0);
    }

    // ---------- Aged Brie ----------

    @Test
    void agedBrie_qualityIncreasesByOne_beforeSellDate() {
        assertUpdated(BRIE, 5, 10, 4, 11);
    }

    @Test
    void agedBrie_qualityIncreasesByTwo_afterSellDate() {
        assertUpdated(BRIE, 0, 10, -1, 12);
    }

    @Test
    void agedBrie_qualityNeverExceedsFifty() {
        assertUpdated(BRIE, 5, 50, 4, 50);
    }

    @Test
    void agedBrie_qualityCappedAtFifty_whenQualityIs49AndIncreasesByTwo() {
        assertUpdated(BRIE, 0, 49, -1, 50);
    }

    // ---------- Backstage passes ----------

    @Test
    void backstage_qualityIncreasesByOne_whenElevenDaysOrMore() {
        assertUpdated(BACKSTAGE, 11, 20, 10, 21);
    }

    @Test
    void backstage_qualityIncreasesByTwo_whenTenDaysLeft() {
        assertUpdated(BACKSTAGE, 10, 20, 9, 22);
    }

    @Test
    void backstage_qualityIncreasesByTwo_whenSixDaysLeft() {
        assertUpdated(BACKSTAGE, 6, 20, 5, 22);
    }

    @Test
    void backstage_qualityIncreasesByThree_whenFiveDaysLeft() {
        assertUpdated(BACKSTAGE, 5, 20, 4, 23);
    }

    @Test
    void backstage_qualityIncreasesByThree_whenOneDayLeft() {
        assertUpdated(BACKSTAGE, 1, 20, 0, 23);
    }

    @Test
    void backstage_qualityDropsToZero_afterConcert() {
        assertUpdated(BACKSTAGE, 0, 20, -1, 0);
    }

    @Test
    void backstage_qualityCappedAtFifty_whenIncreasingByTwo() {
        assertUpdated(BACKSTAGE, 10, 49, 9, 50);
    }

    @Test
    void backstage_qualityCappedAtFifty_whenIncreasingByThree() {
        assertUpdated(BACKSTAGE, 5, 48, 4, 50);
    }

    @Test
    void backstage_qualityStaysFifty_whenAlreadyFifty() {
        assertUpdated(BACKSTAGE, 5, 50, 4, 50);
    }

    // ---------- Sulfuras ----------

    @Test
    void sulfuras_neverChanges() {
        assertUpdated(SULFURAS, 10, 80, 10, 80);
    }

    @Test
    void sulfuras_neverChanges_afterSellDate() {
        assertUpdated(SULFURAS, -1, 80, -1, 80);
    }

    // ---------- Common behavior / Repository interaction ----------

    @Test
    void multipleItems_eachUpdatedByItsOwnRule() {
        Item vest = new Item(VEST, 10, 20);
        Item brie = new Item(BRIE, 10, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(vest, brie));

        new GildedRose(repository).updateQuality();

        assertEquals(19, vest.quality);
        assertEquals(21, brie.quality);
    }

    @Test
    void updateQuality_requestsSavingUpdatedItems() {
        Item vest = new Item(VEST, 10, 20);
        List<Item> items = Arrays.asList(vest);
        when(repository.findAll()).thenReturn(items);

        new GildedRose(repository).updateQuality();

        verify(repository).saveAll(items);
    }

    // ---------- helper ----------

    private void assertUpdated(String name, int sellIn, int quality,
                               int expectedSellIn, int expectedQuality) {
        // Given
        Item item = new Item(name, sellIn, quality);
        when(repository.findAll()).thenReturn(Arrays.asList(item));

        // When
        new GildedRose(repository).updateQuality();

        // Then
        assertEquals(expectedSellIn, item.sellIn);
        assertEquals(expectedQuality, item.quality);
    }
}
