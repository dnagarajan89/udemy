package com.udemy.learn.reactive.spring.router;

import com.udemy.learn.reactive.spring.constants.ItemConstants;
import com.udemy.learn.reactive.spring.handler.ItemsHandler;
import com.udemy.learn.reactive.spring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.udemy.learn.reactive.spring.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static com.udemy.learn.reactive.spring.constants.ItemConstants.ITEM_FUN_ENDPOINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions.route(
                GET(ITEM_FUN_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::getAllItems
        ).andRoute(
                GET(ITEM_FUN_ENDPOINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::getItem
        ).andRoute(
                RequestPredicates.POST(ITEM_FUN_ENDPOINT_V1).and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::createItem
        ).andRoute(
                DELETE(ITEM_FUN_ENDPOINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::deleteItem
        ).andRoute(
                PUT(ITEM_FUN_ENDPOINT_V1.concat("/{id}")).and(accept(MediaType.APPLICATION_JSON)),
                itemsHandler::updateItem
        );
    }

}
