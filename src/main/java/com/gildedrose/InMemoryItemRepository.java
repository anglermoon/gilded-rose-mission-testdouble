package com.gildedrose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 메모리에 재고를 보관하는 저장소.
 *
 * 실제 DB나 원격 저장소처럼 느리게 동작한다고 가정하기 위해,
 * 모든 저장소 접근(조회/저장/입고/출고) 전에 일정 시간 지연이 존재합니다.
 * 이 구현체를 그대로 테스트에 쓰면 테스트가 느려지므로,
 * 단위 테스트에서는 ItemRepository 인터페이스의 테스트 대역(Mock/Stub)을 사용하는 것을 권장합니다.
 */
public class InMemoryItemRepository implements ItemRepository {

    // 저장소 접근 한 번에 걸리는 시간(ms). 느린 외부 저장소의 응답 시간을 흉내 냅니다.
    private static final long DELAY_MILLIS = 1000;

    private final List<Item> items = new ArrayList<>();

    public InMemoryItemRepository() {
    }

    public InMemoryItemRepository(Item... items) {
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public List<Item> findAll() {
        simulateSlowAccess();
        return new ArrayList<>(items);
    }

    @Override
    public void saveAll(List<Item> items) {
        simulateSlowAccess();
        this.items.clear();
        this.items.addAll(items);
    }

    @Override
    public void add(Item item) {
        simulateSlowAccess();
        items.add(item);
    }

    @Override
    public boolean remove(Item item) {
        simulateSlowAccess();
        return items.remove(item);
    }

    // 느린 저장소를 가정하기 위한 인위적인 지연
    private void simulateSlowAccess() {
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            // 인터럽트 상태를 복원해 호출한 쪽에서 중단 요청을 알 수 있게 합니다.
            Thread.currentThread().interrupt();
        }
    }
}
