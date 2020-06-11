package com.udemy.learn.reactive.spring.handler;

import com.udemy.learn.reactive.spring.document.Item;
import com.udemy.learn.reactive.spring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.udemy.learn.reactive.spring.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static com.udemy.learn.reactive.spring.constants.ItemConstants.ITEM_FUN_ENDPOINT_V1;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemsHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 349.99),
                new Item(null, "Apple Watch", 339.99),
                new Item("BTH", "Beats Headphones", 29.99),
                new Item("BSH", "Bose Headphones", 199.99)
        );
    }

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ITEM_FUN_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_FUN_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    response.getResponseBody().stream().forEach(item -> assertTrue(item.getId() != null));
                });

    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_FUN_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void createItem_approach1() {
        webTestClient.post().uri(ITEM_FUN_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "Galaxy Watch S3", 239.99)), Item.class)
                .exchange()
                .expectBody(Item.class)
                .consumeWith(response -> assertTrue(response.getResponseBody().getId() != null));
    }

    @Test
    public void createItem_approach2() {
        webTestClient.post().uri(ITEM_FUN_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "Galaxy Watch S3", 239.99)), Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Galaxy Watch S3")
                .jsonPath("$.price").isEqualTo(239.99);
    }

    @Test
    public void deleteItem() {
        webTestClient
                .delete()
                .uri(ITEM_FUN_ENDPOINT_V1.concat("/{id}"), "BSH")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class)
                .consumeWith(response -> {
                    StepVerifier.create(itemReactiveRepository.findById("BSH"))
                            .expectNextCount(0)
                            .verifyComplete();
                });

    }

    @Test
    public void updateItem() {
        webTestClient
                .put()
                .uri(ITEM_FUN_ENDPOINT_V1.concat("/{id}"), "BTH")
                .body(Mono.just(new Item("BTH", "Beats Headphones", 39.99)), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .consumeWith(response -> {
                    assertTrue(response.getResponseBody().getPrice().equals(39.99));
                });
    }

    @Test
    public void updateItem_notFound() {
        webTestClient
                .put()
                .uri(ITEM_FUN_ENDPOINT_V1.concat("/{id}"), "AAC")
                .body(Mono.just(new Item("AAC", "Test Headphones", 39.99)), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
