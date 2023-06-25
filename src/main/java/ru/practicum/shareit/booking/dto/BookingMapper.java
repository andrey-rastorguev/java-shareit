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

    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .item(itemMapper.toDto(booking.getItem()))
                .booker(userMapper.toDto(booking.getBooker()))
                .build();
        return bookingDto;
    }

    public Booking toEntity(BookingDto bookingDto) {
        Item item = bookingDto.getItem() != null
                ? itemMapper.toEntity(bookingDto.getItem())
                : itemRepository.getById(bookingDto.getItemId());
        Booking booking = Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .item(item)
                .booker(userMapper.toEntity(bookingDto.getBooker()))
                .build();
        return booking;
    }
}
