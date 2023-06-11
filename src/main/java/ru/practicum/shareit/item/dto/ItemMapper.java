package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.light.BookingLightMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class ItemMapper {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingLightMapper bookingLightMapper;
    private final CommentMapper commentLightMapper;

    public ItemDto toItemDto(Item item, long userId) {
        ItemDto itemDto = toItemDto(item);
        if (itemDto.getOwnerId() == userId) {
            putNextAndLastBooking(itemDto);
        }
        return itemDto;
    }

    public ItemDto toItemDto(Item item) {
        List<CommentDto> comments = commentRepository
                .findByItem_IdOrderByIdAsc(item.getId())
                .stream()
                .map(commentLightMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .ownerId(item.getOwner().getId())
                .comments(comments)
                .build();
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwnerId() != null ? userRepository.getById(itemDto.getOwnerId()) : null)
                .build();
        return item;
    }

    private void putNextAndLastBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItem_Id(itemDto.getId());
        List<Booking> bookingsAfter = getStreamBooking(bookings).filter(b -> b.getStart().isAfter(now)).collect(Collectors.toList());
        List<Booking> bookingsBefore = getStreamBooking(bookings).filter(b -> b.getStart().isBefore(now)).collect(Collectors.toList());
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookingsAfter.size() > 0) {
            nextBooking = bookingsAfter.get(0);
            itemDto.setNextBooking(bookingLightMapper.toBookingLightDto(nextBooking));
        }
        if (bookingsBefore.size() > 0) {
            lastBooking = bookingsBefore.get(bookingsBefore.size() - 1);
            itemDto.setLastBooking(bookingLightMapper.toBookingLightDto(lastBooking));
        }
    }

    private Stream<Booking> getStreamBooking(List<Booking> bookings) {
        return bookings
                .stream()
                .filter(b -> (b.getStatus().equals(StatusBooking.APPROVED) || b.getStatus().equals(StatusBooking.WAITING)))
                .sorted(Comparator.comparing(Booking::getStart));
    }

}