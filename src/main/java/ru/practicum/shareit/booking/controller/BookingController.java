package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.light.BookingLightDto;
import ru.practicum.shareit.booking.other.BookingRequestStates;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";
    private BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody final BookingLightDto bookingLightDto,
                                    @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return bookingService.createBooking(bookingLightDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable final Long bookingId,
                                     @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable final Long id,
                                 @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForUserId(@RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingRequestStates state,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getBookingsForUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForItemOfUserId(@RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") BookingRequestStates state,
                                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getBookingsForItemOfUserId(userId, state, from, size);
    }
}
