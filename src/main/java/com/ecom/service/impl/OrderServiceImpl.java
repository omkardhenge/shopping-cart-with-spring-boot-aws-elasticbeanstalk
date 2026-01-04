package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ecom.model.ProductOrder;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        if (userId == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder getOrderById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void saveOrder(ProductOrder order) {
        orderRepository.save(order);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        ProductOrder order = getOrderById(id);
        if (order != null) {
            order.setStatus(status); // directly save string
            orderRepository.save(order);
        }
        return order;
    }


    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageRequest);
    }
}
