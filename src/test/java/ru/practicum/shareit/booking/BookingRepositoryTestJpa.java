package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingRepositoryTestJpa {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

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

    private final List<Booking> bookings = List.of(
            Booking.builder().start(LocalDateTime.now().plusMinutes(1)).end(LocalDateTime.now().plusDays(2))
                    .status(StatusBooking.WAITING).build(),
            Booking.builder().start(LocalDateTime.now().plusMinutes(1)).end(LocalDateTime.now().plusDays(1))
                    .status(StatusBooking.WAITING).build(),
            Booking.builder().start(LocalDateTime.now().plusMinutes(1)).end(LocalDateTime.now().plusMinutes(2))
                    .status(StatusBooking.REJECTED).build(),
            Booking.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                    .status(StatusBooking.WAITING).build()
    );

    @Test
    public void existsByItemAndBookerAndStatusNotAndStartLessThanEqual() {
        for (User user : users) {
            em.persist(user);
        }
        em.flush();
        User user1 = users.get(0);
        User user2 = users.get(1);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (i == 0) item.setOwner(user1);
            if (i == 1) item.setOwner(user2);
            if (i == 2) item.setOwner(user2);
            if (i == 3) item.setOwner(user2);
            em.persist(item);
        }
        em.flush();
        Item item1 = items.get(0);
        Item item2 = items.get(1);
        Item item3 = items.get(2);
        Item item4 = items.get(3);

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            if (i == 0) {
                booking.setBooker(user2);
                booking.setItem(items.get(0));
            }
            if (i == 1) {
                booking.setBooker(user1);
                booking.setItem(items.get(1));
            }
            if (i == 2) {
                booking.setBooker(user1);
                booking.setItem(items.get(2));
            }
            if (i == 3) {
                booking.setBooker(user1);
                booking.setItem(items.get(3));
            }
            em.persist(booking);
        }
        em.flush();

        boolean exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user2,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(true));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user2,
                LocalDateTime.now());
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item3, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item4, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item2, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(true));
    }
}
