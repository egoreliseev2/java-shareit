package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user = new User(null, "user", "user@user.ru");

    ItemRequest itemRequest = new ItemRequest(
            null,
            user,
            "description",
            LocalDateTime.now());

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByRequestorIdTest() {
        em.persist(user);
        em.persist(itemRequest);

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(user.getId());
        assertEquals(1, itemRequestList.size());
        assertEquals("description", itemRequestList.get(0).getDescription());
    }

    @Test
    void findAllPageableTest() {
        em.persist(user);
        em.persist(itemRequest);
        PageRequest pg = PageRequest.of(0, 10);

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllPageable(9999L, pg);
        assertEquals(1, itemRequestList.size());
        assertEquals("description", itemRequestList.get(0).getDescription());

        itemRequestList = itemRequestRepository.findAllPageable(user.getId(), pg);
        assertTrue(itemRequestList.isEmpty());
    }
}