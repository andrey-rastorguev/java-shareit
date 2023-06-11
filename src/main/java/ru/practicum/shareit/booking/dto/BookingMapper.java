package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .item(itemMapper.toItemDto(booking.getItem()))
                .booker(userMapper.toUserDto(booking.getBooker()))
                .build();
        return bookingDto;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Item item = bookingDto.getItem() != null
                ? itemMapper.toItem(bookingDto.getItem())
                : itemRepository.getById(bookingDto.getItemId());
        Booking booking = Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .item(item)
                .booker(userMapper.toUser(bookingDto.getBooker()))
                .build();
        return booking;
    }
}
