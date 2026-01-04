package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.ProductOrder;
import com.ecom.util.OrderStatus;

public interface OrderService {

    List<ProductOrder> getOrdersByUser(Integer userId);

    ProductOrder getOrderById(Integer id);

    void saveOrder(ProductOrder order);

	Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

	ProductOrder updateOrderStatus(Integer id, String status);

}
