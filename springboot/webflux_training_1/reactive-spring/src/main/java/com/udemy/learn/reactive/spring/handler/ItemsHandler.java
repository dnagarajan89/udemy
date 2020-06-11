package com.udemy.learn.reactive.spring.handler;

import com.udemy.learn.reactive.spring.document.Item;
import com.udemy.learn.reactive.spring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromServerSentEvents;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;

@Component
public class ItemsHandler {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);

    }

    public Mono<ServerResponse> getItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = itemReactiveRepository.findById(serverRequest.pathVariable("id"));
        return itemMono.flatMap(item -> ServerResponse.ok().body(fromPublisher(Mono.just(item), Item.class)))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item ->
            ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(itemReactiveRepository.save(item), Item.class)
        );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        Mono<Void> deletedItem = itemReactiveRepository.deleteById(serverRequest.pathVariable("id"));
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deletedItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        Mono<Item> itemToUpdateMono = serverRequest.bodyToMono(Item.class);
        Mono<Item> currentItemMono = itemReactiveRepository.findById(serverRequest.pathVariable("id"));
        Mono<Item> updatedItem = itemToUpdateMono.flatMap(itemToUpdate -> {
            return currentItemMono.flatMap(currentItem -> {
                currentItem.setDescription(itemToUpdate.getDescription());
                currentItem.setPrice(itemToUpdate.getPrice());
                return itemReactiveRepository.save(currentItem);
            });
        });
        return updatedItem.flatMap(item ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(updatedItem, Item.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }
}
