package io.vividcode.happytakeaway.order.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.vividcode.happytakeaway.order.entity.OrderEntity;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, String> {}
