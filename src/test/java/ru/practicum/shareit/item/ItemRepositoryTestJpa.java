package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
public class ItemRepositoryTestJpa {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;

    private final List<User> users = List.of(
            User.builder().name("user1").email("user1@mail.com").build(),
            User.builder().name("user2").email("user2@mail.com").build(),
            User.builder().name("user3").email("user3@mail.com").build()
    );

    private final List<Item> items = List.of(
            Item.builder().name("item1").description("test item1 aAaA").available(true).build(),
            Item.builder().name("item2").description("test item2 aa").available(true).build(),
            Item.builder().name("item3").description("test item3 bbb").available(true).build(),
            Item.builder().name("item4").description("test item4 ccc").available(true).build()
    );

    @Test
    public void searchItemsDtoByText() {
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
            if (i == 4) item.setOwner(user3);
            em.persist(item);
        }
        em.flush();

        PageRequest page = PageRequest.of(0, 10);
        List<Item> items = repository.searchItemsDtoByText("", page);
        assertThat(items, hasSize(4));

        items = repository.searchItemsDtoByText("aa", page);
        assertThat(items, hasSize(2));

        items = repository.searchItemsDtoByText("cCc", page);
        assertThat(items, hasSize(1));

        items = repository.searchItemsDtoByText("yyyyy", page);
        assertThat(items, hasSize(0));
    }
}

