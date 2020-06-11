package com.udemy.learn.reactive.spring.controller.v1;

import com.udemy.learn.reactive.spring.constants.ItemConstants;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 349.99),
                new Item(null, "Apple Watch", 339.99),
                new Item("BTH", "Beats Headphones", 29.99)
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
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> response
                        .getResponseBody()
                        .stream()
                        .forEach(item -> Assert.assertTrue(item.getId() != null))
                );
    }

    @Test
    public void getAllItems_approach3() {
       Flux<Item> itemFlux = webTestClient.get().uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("Value from network : "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "BTH")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 29.99);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "TNF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "IPhone X", 999.99);
        webTestClient.post().uri(ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("IPhone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem() {
       webTestClient.delete().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "BTH")
               .accept(MediaType.APPLICATION_JSON)
               .exchange()
               .expectStatus().isOk()
               .expectBody(Void.class);
       StepVerifier.create(itemReactiveRepository.findById("BTH"))
               .expectSubscription()
               .expectNextCount(0)
               .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphones", newPrice);
        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "BTH")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    public void updateItem_notFound() {
        double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphones", newPrice);
        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "BTH1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
