package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.ItemRequest;

public interface ItemRequestRepository {
    ItemRequest getItemRequestRepositoryById(int id);
}
