package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(header) long userId,
                                 @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getRequestsInfo(@RequestHeader(header) long userId) {
        return requestService.getRequestsInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequestInfo(@RequestHeader(header) long userId,
                                                 @PathVariable long requestId) {
        return requestService.getRequestInfo(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequestsList(@RequestHeader(header) long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                        @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return requestService.getRequestsList(userId, from, size);
    }
}
