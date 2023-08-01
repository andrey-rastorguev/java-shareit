package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplTestIntegral {

    private final EntityManager em;

    private final CommentService commentService;

    @BeforeEach
    void initItemDto() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        User user1 = User.builder().name("user1").email("1@1.com").build();
        User user2 = User.builder().name("user2").email("2@2.com").build();
        em.persist(user1);
        em.persist(user2);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userInit1 = query.setParameter("id", 1L).getSingleResult();
        User userInit2 = query.setParameter("id", 2L).getSingleResult();
        Item item = Item.builder()
                .name("item1")
                .description("description of item1")
                .owner(userInit1)
                .available(true)
                .build();
        em.persist(item);
        em.flush();
        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemInit = queryItem.setParameter("id", 1L).getSingleResult();
        Booking booking = Booking.builder()
                .item(itemInit)
                .booker(userInit2)
                .status(StatusBooking.APPROVED)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        em.persist(booking);
        em.flush();

        try {
            TimeUnit time = TimeUnit.SECONDS;
            time.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createCommentForItem() {
        CommentDto commentDtoIn = CommentDto.builder().itemId(1L).authorId(1l).text("Коментарий").authorName("Имя").build();
        commentService.createCommentForItem(commentDtoIn, 1L, 2L);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment commentOut = query.setParameter("id", 1L).getSingleResult();
        assertThat(commentOut.getId(), notNullValue());
        assertThat(commentOut.getText(), equalTo(commentDtoIn.getText()));
        assertThat(commentOut.getItem().getId(), equalTo(commentDtoIn.getItemId()));
        assertThat(commentOut.getAuthor().getId(), equalTo(commentDtoIn.getAuthorId()));
        assertThat(commentOut.getCreated(), notNullValue());
    }
}