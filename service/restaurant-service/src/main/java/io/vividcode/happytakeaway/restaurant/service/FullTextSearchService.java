package io.vividcode.happytakeaway.restaurant.service;

import io.vividcode.happytakeaway.common.base.PageRequest;
import io.vividcode.happytakeaway.common.base.PagedResult;
import io.vividcode.happytakeaway.restaurant.api.FullTextSearchRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.Restaurant;
import io.vividcode.happytakeaway.restaurant.api.v1.web.FullTextSearchResponse;
import io.vividcode.happytakeaway.restaurant.entity.MenuItemEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.hibernate.search.engine.search.predicate.dsl.MatchPredicateOptionsStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.RangePredicateFieldMoreStep;
import org.hibernate.search.engine.search.predicate.dsl.RangePredicateOptionsStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;

@ApplicationScoped
public class FullTextSearchService {

  @Inject SearchSession searchSession;

  public FullTextSearchResponse search(FullTextSearchRequest request) {
    PageRequest pageRequest = request.getPageRequest();
    SearchQueryOptionsStep<?, MenuItemEntity, SearchLoadingOptionsStep, ?, ?> query =
        this.searchSession
            .search(MenuItemEntity.class)
            .where(factory -> this.buildQuery(request, factory))
            .sort(SearchSortFactory::score);
    long count = query.fetchTotalHitCount();
    List<MenuItemEntity> menuItems =
        query.fetchHits(pageRequest.getOffset(), pageRequest.getSize());
    List<Restaurant> restaurants =
        menuItems.stream()
            .map(MenuItemEntity::getRestaurant)
            .distinct()
            .map(ServiceHelper::buildRestaurant)
            .collect(Collectors.toList());
    return FullTextSearchResponse.builder()
        .result(PagedResult.fromData(restaurants, pageRequest, count))
        .build();
  }

  private PredicateFinalStep buildQuery(
      FullTextSearchRequest request, SearchPredicateFactory factory) {
    MatchPredicateOptionsStep<?> query =
        factory.match().fields("name", "description").matching(request.getQuery());
    RangePredicateOptionsStep<?> price = null;
    RangePredicateFieldMoreStep<?, ?> fieldMoreStep = factory.range().fields("price");
    if (request.getMinPrice() != null && request.getMaxPrice() != null) {
      price = fieldMoreStep.between(request.getMinPrice(), request.getMaxPrice());
    } else if (request.getMinPrice() == null && request.getMaxPrice() != null) {
      price = fieldMoreStep.atMost(request.getMaxPrice());
    } else if (request.getMinPrice() != null && request.getMaxPrice() == null) {
      price = fieldMoreStep.atLeast(request.getMinPrice());
    }
    if (price != null) {
      return factory.bool().must(query).must(price);
    } else {
      return query;
    }
  }
}
