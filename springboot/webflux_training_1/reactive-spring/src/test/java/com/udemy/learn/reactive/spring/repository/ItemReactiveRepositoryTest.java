package com.udemy.learn.reactive.spring.repository;

import com.udemy.learn.reactive.spring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataMongoTest
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("TBH", "Bose Headphones", 299.99),
            new Item("TSH", "Skullcandy Headphones", 299.99),
            new Item("STV", "Sony TV", 1099.0)
    );

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> System.out.println("Inserted item is : " + item)))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("TBH"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
       StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log())
               .expectSubscription()
               .expectNextCount(1)
              // .expectNextMatches(item -> item.getId().equals("TBH"))
               .verifyComplete();
    }

    @Test
    public void saveItem() {
        Mono<Item> itemMono = itemReactiveRepository.save(new Item(null, "Google Home Mini", 29.99));
        StepVerifier.create(itemMono.log("saveItem : "))
                .expectSubscription()
                .expectNextMatches(item -> item.getId() != null && item.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(520.0);
                    return item;
                }).flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.0)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository.findById("STV")
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById);
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findById("STV"))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    public void deleteItemB() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("Skullcandy Headphones")
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findById("TSH"))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

    }

}
