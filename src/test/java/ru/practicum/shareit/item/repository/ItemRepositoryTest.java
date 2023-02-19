package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    User user = new User(null, "user", "user@user.ru");
    Item item = new Item(
            null,
            "name1",
            "description first",
            true,
            user,
            null);
    Item item2 = new Item(
            null,
            "name2",
            "description second",
            true,
            user,
            null);

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        em.persist(user);
        em.persist(item2);
        em.persist(item);
        PageRequest p = PageRequest.of(0, 20);

        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId(), p);

        assertEquals(item2, items.get(0));
        assertEquals(item, items.get(1));
    }

    @Test
    void searchByTextTest() {
        em.persist(user);
        em.persist(item2);
        em.persist(item);
        PageRequest p = PageRequest.of(0, 20);

        List<Item> items = itemRepository.searchByText("first", p);
        assertEquals(1, items.size());
        assertEquals("name1", items.get(0).getName());
    }

    @Test
    void searchByRequestsIdTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        Item itemWithRequest = new Item(
                null,
                "name3",
                "description in item with request",
                true,
                user,
                itemRequest);

        em.persist(user);
        em.persist(item2);
        em.persist(item);
        em.persist(itemWithRequest);

        List<Item> items = itemRepository.searchByRequestsId(List.of(itemWithRequest.getItemRequest().getId()));
        assertEquals(1, items.size());
        assertEquals("name3", items.get(0).getName());
        assertEquals("description in item with request", items.get(0).getDescription());
    }

    @Test
    void findByItemRequestIdTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        Item itemWithRequest = new Item(
                null,
                "name3",
                "description in item with request",
                true,
                user,
                itemRequest);

        em.persist(user);
        em.persist(item2);
        em.persist(item);
        em.persist(itemWithRequest);

        List<Item> items = itemRepository.findByItemRequestId(1L);
        assertEquals("name3", items.get(0).getName());
        assertEquals("description in item with request", items.get(0).getDescription());
    }
}
