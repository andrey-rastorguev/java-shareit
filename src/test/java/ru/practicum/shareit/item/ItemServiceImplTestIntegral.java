package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIntegral {

    private final EntityManager em;

    private final ItemService itemService;

    private final List<String> uniKeys = List.of("item1", "item2", "item3", "item4", "item5");
    private final User user = User.builder().name("user1").email("1@1.com").build();

    private List<ItemDto> itemsDtoIn;
    private List<Item> itemsIn;

    @BeforeEach
    void initItemDto() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.persist(user);
        em.flush();
        itemsDtoIn = uniKeys.stream()
                .map(k -> ItemDto.builder()
                        .name(k)
                        .description("description of " + k)
                        .ownerId(1L)
                        .available(true)
                        .build()).collect(Collectors.toList());
        itemsIn = uniKeys.stream()
                .map(k -> Item.builder()
                        .name(k)
                        .description("description of " + k)
                        .owner(user)
                        .available(true)
                        .build()).collect(Collectors.toList());
        for (Item item : itemsIn) {
            em.persist(item);
        }
        em.flush();
    }

    @Test
    void addItemDto() {
        ItemDto itemDtoIn = ItemDto.builder()
                .name("item001")
                .description("description of item001")
                .available(false).build();
        itemService.addItemDto(itemDtoIn, 1L);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query.setParameter("name", itemDtoIn.getName()).getSingleResult();
        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(itemDtoIn.getName()));
        assertThat(itemOut.getDescription(), equalTo(itemDtoIn.getDescription()));
        assertThat(itemOut.isAvailable(), equalTo(itemDtoIn.getAvailable()));
        assertThat(itemOut.getOwner().getId(), equalTo(itemDtoIn.getOwnerId()));
    }

    @Test
    void patchItemDto() {
        itemsDtoIn.get(0).setDescription("description!!!");
        itemsDtoIn.get(0).setName("item001");
        itemService.patchItemDto(1L, 1L, itemsDtoIn.get(0));
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query.setParameter("name", itemsDtoIn.get(0).getName()).getSingleResult();
        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(itemsDtoIn.get(0).getName()));
        assertThat(itemOut.getDescription(), equalTo(itemsDtoIn.get(0).getDescription()));
        assertThat(itemOut.isAvailable(), equalTo(itemsDtoIn.get(0).getAvailable()));
        assertThat(itemOut.getOwner().getId(), equalTo(itemsDtoIn.get(0).getOwnerId()));
    }

    @Test
    void getItemDtoById() {
        ItemDto itemOut = itemService.getItemDtoById(2L, 1L);
        assertThat(itemOut.getId(), equalTo(2L));
        assertThat(itemOut.getName(), equalTo(itemsDtoIn.get(1).getName()));
        assertThat(itemOut.getDescription(), equalTo(itemsDtoIn.get(1).getDescription()));
        assertThat(itemOut.getAvailable(), equalTo(itemsDtoIn.get(1).getAvailable()));
        assertThat(itemOut.getOwnerId(), equalTo(itemsDtoIn.get(1).getOwnerId()));
    }

    @Test
    void getItemsDtoByUserId() {
        List<ItemDto> itemsDtoOut = itemService.getItemsDtoByUserId(1L, 0, 10);
        for (ItemDto itemDto : itemsDtoOut) {
            assertThat(itemsDtoOut, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemDto.getName())),
                    hasProperty("description", equalTo(itemDto.getDescription())),
                    hasProperty("available", equalTo(itemDto.getAvailable())),
                    hasProperty("ownerId", equalTo(itemDto.getOwnerId()))
            )));
        }
    }

    @Test
    void searchItemsDtoByText() {
        List<ItemDto> itemsOut = itemService.searchItemsDtoByText("item3", 0, 10);
        assertThat(itemsOut, hasSize(1));
    }
}