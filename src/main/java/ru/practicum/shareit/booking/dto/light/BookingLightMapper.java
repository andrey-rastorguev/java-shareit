package ru.practicum.shareit.booking.dto.light;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Component
@AllArgsConstructor
public class BookingLightMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingLightDto toBookingLightDto(Booking booking) {
        BookingLightDto bookingLightDto = BookingLightDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
        return bookingLightDto;
    }

    public Booking toBooking(BookingLightDto bookingLightDto) {
        Optional<Item> item = itemRepository.findById(bookingLightDto.getItemId());
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Item for Booking");
        }
        Optional<User> user = userRepository.findById(bookingLightDto.getBookerId());
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User for Booking");
        }
        Booking booking = Booking.builder()
                .id(bookingLightDto.getId())
                .start(bookingLightDto.getStart())
                .end(bookingLightDto.getEnd())
                .status(bookingLightDto.getStatus())
                .item(item.get())
                .booker(user.get())
                .build();
        return booking;
    }
}
