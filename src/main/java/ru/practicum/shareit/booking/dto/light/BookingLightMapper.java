package ru.practicum.shareit.booking.dto.light;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.ItemNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@AllArgsConstructor
public class BookingLightMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingLightDto toDto(Booking booking) {
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

    public Booking toEntity(BookingLightDto bookingLightDto) {
        Item item = itemRepository.findById(bookingLightDto.getItemId()).orElseThrow(ItemNotFoundException::new);
        User user = userRepository.findById(bookingLightDto.getBookerId()).orElseThrow(UserNotFoundException::new);
        Booking booking = Booking.builder()
                .id(bookingLightDto.getId())
                .start(bookingLightDto.getStart())
                .end(bookingLightDto.getEnd())
                .status(bookingLightDto.getStatus())
                .item(item)
                .booker(user)
                .build();
        return booking;
    }
}
