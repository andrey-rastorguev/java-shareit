package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTestIntegral {

    private final EntityManager em;

    private final ItemRequestService itemRequestService;


    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, Item> items = new HashMap<>();
    private Map<Integer, ItemRequest> itemsRequest = new HashMap<>();

    @BeforeEach
    void initItemDto() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE requests ALTER COLUMN id RESTART WITH 1").executeUpdate();
        User user1 = User.builder().name("user1").email("1@1.com").build();
        User user2 = User.builder().name("user2").email("2@2.com").build();
        User user3 = User.builder().name("user3").email("3@3.com").build();
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        users.put(1, query.setParameter("id", 1L).getSingleResult());
        users.put(2, query.setParameter("id", 2L).getSingleResult());
        users.put(3, query.setParameter("id", 3L).getSingleResult());
        Item item1 = Item.builder()
                .name("item1")
                .description("description of item1")
                .owner(user1)
                .available(true)
                .build();
        Item item2 = Item.builder()
                .name("item2")
                .description("description of item2")
                .owner(user2)
                .available(true)
                .build();
        Item item3 = Item.builder()
                .name("Ложка")
                .description("Ложка деревянная")
                .owner(user2)
                .available(true)
                .build();
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();
        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        items.put(1, queryItem.setParameter("id", 1L).getSingleResult());
        items.put(2, queryItem.setParameter("id", 2L).getSingleResult());
        items.put(3, queryItem.setParameter("id", 3L).getSingleResult());
        ItemRequest itemRequest1 = ItemRequest.builder()
                .requester(user1)
                .description("Нужна тяпка")
                .created(LocalDateTime.now())
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .requester(user2)
                .description("Нужна ложка")
                .created(LocalDateTime.now())
                .items(List.of(item3))
                .build();
        em.persist(itemRequest1);
        em.persist(itemRequest2);
        em.flush();
        TypedQuery<ItemRequest> queryRequest = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        itemsRequest.put(1, queryRequest.setParameter("id", 1L).getSingleResult());
        itemsRequest.put(2, queryRequest.setParameter("id", 2L).getSingleResult());
    }


    @Test
    void getUserRequests() {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(2L);
        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getId(), notNullValue());
        assertThat(requests.get(0).getDescription(), equalTo(itemsRequest.get(2).getDescription()));
        assertThat(requests.get(0).getItems(), hasSize(1));
    }

    @Test
    void getOtherUserRequests() {
        List<ItemRequestDto> requests = itemRequestService.getOtherUserRequests(3L, 0, 10);
        assertThat(requests, hasSize(2));
    }


}