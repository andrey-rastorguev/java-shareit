package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
class ItemRequestRepositoryTestJpa {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository repository;

    private final List<User> users = List.of(
            User.builder().name("user1").email("user1@mail.com").build(),
            User.builder().name("user2").email("user2@mail.com").build(),
            User.builder().name("user3").email("user3@mail.com").build()
    );

    private final List<Item> items = List.of(
            Item.builder().name("item1").description("test item1").available(true).build(),
            Item.builder().name("item2").description("test item2").available(true).build(),
            Item.builder().name("item3").description("test item3").available(true).build(),
            Item.builder().name("item4").description("test item4").available(true).build()
    );

    private final List<ItemRequest> itemRequests = List.of(
            ItemRequest.builder().description("wish 1").build(),
            ItemRequest.builder().description("wish 2").build(),
            ItemRequest.builder().description("wish 3").build()
    );

    @Test
    public void findByRequesterNotOrderByIdDesc() {
        for (User user : users) {
            em.persist(user);
        }
        em.flush();
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (i == 0) item.setOwner(user1);
            if (i == 1) item.setOwner(user2);
            if (i == 2) item.setOwner(user2);
            if (i == 3) item.setOwner(user2);
            em.persist(item);
        }
        em.flush();

        for (int i = 0; i < itemRequests.size(); i++) {
            ItemRequest itemRequest = itemRequests.get(i);
            if (i == 0) itemRequest.setRequester(user2);
            if (i == 1) itemRequest.setRequester(user3);
            if (i == 2) itemRequest.setRequester(user3);
            em.persist(itemRequest);
        }
        em.flush();

        PageRequest page = PageRequest.of(0, 10);
        List<ItemRequest> itemRequests = repository.findByRequesterNotOrderByIdDesc(user2, page);
        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests.get(0).getDescription(), equalTo("wish 3"));
    }

}