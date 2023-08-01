package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.light.BookingLightDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.BookingRequestStates;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTestIntegral {

    private final EntityManager em;

    private final BookingService bookingService;


    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, Item> items = new HashMap<>();

    @BeforeEach
    void initItemDto() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        User user1 = User.builder().name("user1").email("1@1.com").build();
        User user2 = User.builder().name("user2").email("2@2.com").build();
        User user3 = User.builder().name("user3").email("3@3.com").build();
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        users.put(1,query.setParameter("id", 1L).getSingleResult());
        users.put(2,query.setParameter("id", 2L).getSingleResult());
        users.put(3,query.setParameter("id", 3L).getSingleResult());
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
        em.persist(item1);
        em.persist(item2);
        em.flush();
        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        items.put(1,queryItem.setParameter("id", 1L).getSingleResult());
        items.put(2,queryItem.setParameter("id", 2L).getSingleResult());
        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(user2)
                .status(StatusBooking.APPROVED)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        Booking booking2 = Booking.builder()
                .item(item2)
                .booker(user1)
                .status(StatusBooking.WAITING)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        em.persist(booking1);
        em.persist(booking2);
        em.flush();

    }

    @Test
    void createBooking() {
        BookingLightDto bookingLightDto = BookingLightDto.builder()
                .itemId(1L)
                .bookerId(3L)
                .status(StatusBooking.APPROVED)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        bookingService.createBooking(bookingLightDto,3L);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingOut = query.setParameter("id", 3L).getSingleResult();
        assertThat(bookingOut.getId(),equalTo(3L));
        assertThat(bookingOut.getBooker().getId(),equalTo(bookingLightDto.getBookerId()));
        assertThat(bookingOut.getItem().getId(),equalTo(bookingLightDto.getItemId()));
        assertThat(bookingOut.getStatus(),equalTo(bookingLightDto.getStatus()));
        assertThat(bookingOut.getStart(),equalTo(bookingLightDto.getStart()));
        assertThat(bookingOut.getEnd(),equalTo(bookingLightDto.getEnd()));
    }

    @Test
    void approveBooking() {
        bookingService.approveBooking(2L,2L,true);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingOut = query.setParameter("id", 2L).getSingleResult();
        assertThat(bookingOut.getStatus(),equalTo(StatusBooking.APPROVED));
    }

    @Test
    void getBooking() {
        BookingDto bookingDto = bookingService.getBooking(1L,2L);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingOut = query.setParameter("id", 1L).getSingleResult();
        assertThat(bookingDto.getId(),equalTo(bookingOut.getId()));
        assertThat(bookingDto.getBooker().getId(),equalTo(bookingOut.getBooker().getId()));
        assertThat(bookingDto.getItem().getId(),equalTo(bookingOut.getItem().getId()));
        assertThat(bookingDto.getStatus(),equalTo(bookingOut.getStatus()));
        assertThat(bookingDto.getStart(),equalTo(bookingOut.getStart()));
        assertThat(bookingDto.getEnd(),equalTo(bookingOut.getEnd()));
    }

    @Test
    void getBookingsForUserId() {
        List<BookingDto> bookingsDto = bookingService.getBookingsForUserId(1L, BookingRequestStates.ALL,0,10);
        assertThat(bookingsDto, hasSize(1));
    }

    @Test
    void getBookingsForItemOfUserId() {
        List<BookingDto> bookingsDto = bookingService.getBookingsForItemOfUserId(1L, BookingRequestStates.ALL,0,10);
        assertThat(bookingsDto, hasSize(1));
    }
}