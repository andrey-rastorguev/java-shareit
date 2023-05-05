package ru.practicum.shareit.request.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class ItemRequestRepositoryInMemory implements ItemRequestRepository {

    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();

    @Override
    public ItemRequest getItemRequestRepositoryById(int id) {
        ItemRequest itemRequest = itemRequests.get(id);
        if (itemRequest != null) {
            return itemRequest;
        } else {
            throw new ObjectNotFoundException("ItemRequest");
        }
    }
}
