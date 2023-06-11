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
import ru.practicum.shareit.other.exception.AccessDeniedForItemException;
import ru.practicum.shareit.other.exception.IllegalUpdateObjectException;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.other.exception.WrongInputDataBookingException;
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
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User");
        }
        Booking booking = bookingLightMapper.toBooking(bookingLightDto);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long userId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Booking");
        }
        if (booking.get().getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("User");
        }
        if (booking.get().getStatus() == StatusBooking.APPROVED) {
            throw new IllegalUpdateObjectException("Booking");
        }
        booking.get().setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking.get()));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long id, long userId) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Booking");
        }
        if (booking.get().getItem().getOwner().getId() != userId && booking.get().getBooker().getId() != userId) {
            throw new ObjectNotFoundException("User");
        }
        return bookingMapper.toBookingDto(booking.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForUserId(Long userId, BookingRequestStates state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User");
        }
        List<Booking> bookings = bookingRepository.findByBooker_Id(userId);
        List<BookingDto> bookingsDto = getBookingByState(bookings, state)
                .stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return bookingsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForItemOfUserId(Long userId, BookingRequestStates state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User");
        }
        List<Booking> bookings = bookingRepository.findByItem_Owner(user.get());
        List<BookingDto> bookingsDto = getBookingByState(bookings, state)
                .stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return bookingsDto;
    }

    private void checkedBookingLightDto(BookingLightDto bookingLightDto) {
        if (userRepository.findById(bookingLightDto.getBookerId()).isEmpty()) {
            throw new ObjectNotFoundException("User");
        }
        Optional<Item> item = itemRepository.findById(bookingLightDto.getItemId());
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Item");
        }
        if (!item.get().isAvailable()) {
            throw new AccessDeniedForItemException(item.get().getId());
        }
        if (bookingLightDto.getEnd() == null || bookingLightDto.getStart() == null ||
                bookingLightDto.getEnd().isBefore(bookingLightDto.getStart()) ||
                bookingLightDto.getEnd().equals(bookingLightDto.getStart())) {
            throw new WrongInputDataBookingException("Booking");
        }
        if (bookingLightDto.getBookerId() == item.get().getOwner().getId()) {
            throw new ObjectNotFoundException("Item");
        }
    }


    private List<Booking> getBookingByState(List<Booking> bookings, BookingRequestStates state) {
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
        return bookingStream.sorted(Comparator.comparing(Booking::getStart)).collect(Collectors.toList());
    }

}
