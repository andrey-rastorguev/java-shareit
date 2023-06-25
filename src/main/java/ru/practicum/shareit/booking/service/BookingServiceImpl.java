package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.light.BookingLightDto;
import ru.practicum.shareit.booking.dto.light.BookingLightMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.BookingRequestStates;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingLightMapper bookingLightMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingLightDto bookingLightDto, long bookerId) {
        bookingLightDto.setBookerId(bookerId);
        bookingLightDto.setStatus(StatusBooking.WAITING);
        checkedBookingLightDto(bookingLightDto);
        userRepository.findById(bookerId).orElseThrow(UserNotFoundException::new);
        Booking booking = bookingLightMapper.toEntity(bookingLightDto);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new UserNotFoundException();
        }
        if (booking.getStatus() == StatusBooking.APPROVED) {
            throw new IllegalUpdateObjectException("Booking");
        }
        booking.setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long id, long userId) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException();
        }
        if (booking.get().getItem().getOwner().getId() != userId && booking.get().getBooker().getId() != userId) {
            throw new UserNotFoundException();
        }
        return bookingMapper.toDto(booking.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForUserId(Long userId, BookingRequestStates state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Booking> bookings = bookingRepository.findByBooker(user);
        List<BookingDto> bookingsDto = getBookingByState(bookings, state, from, size);
        return bookingsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForItemOfUserId(Long userId, BookingRequestStates state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Booking> bookings = bookingRepository.findByItem_Owner(user);
        List<BookingDto> bookingsDto = getBookingByState(bookings, state, from, size);
        return bookingsDto;
    }

    private void checkedBookingLightDto(BookingLightDto bookingLightDto) {
        userRepository.findById(bookingLightDto.getBookerId()).orElseThrow(UserNotFoundException::new);
        Item item = itemRepository.findById(bookingLightDto.getItemId()).orElseThrow(ItemNotFoundException::new);
        if (!item.isAvailable()) {
            throw new AccessDeniedForItemException(item.getId());
        }
        if (bookingLightDto.getEnd() == null || bookingLightDto.getStart() == null ||
                bookingLightDto.getEnd().isBefore(bookingLightDto.getStart()) ||
                bookingLightDto.getEnd().equals(bookingLightDto.getStart())) {
            throw new WrongInputDataBookingException("Booking");
        }
        if (bookingLightDto.getBookerId() == item.getOwner().getId()) {
            throw new ItemNotFoundException();
        }
    }


    private List<BookingDto> getBookingByState(List<Booking> bookings, BookingRequestStates state, int from, int size) {
        Stream<Booking> bookingStream = bookings.stream();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now));
                break;
            case PAST:
                bookingStream = bookingStream.filter(booking -> booking.getEnd().isBefore(now));
                break;
            case FUTURE:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isAfter(now));
                break;
            case WAITING:
                bookingStream = bookingStream.filter(booking -> booking.getStatus() == StatusBooking.WAITING);
                break;
            case REJECTED:
                bookingStream = bookingStream.filter(booking -> booking.getStatus() == StatusBooking.REJECTED);
                break;
        }
        List<BookingDto> bookingsDto = bookingStream
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
        if (from + size > bookingsDto.size()) {
            size = bookingsDto.size() - from;
        }
        return bookingsDto.subList(from, from + size);
    }

}
