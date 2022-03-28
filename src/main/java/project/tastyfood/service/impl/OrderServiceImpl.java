package project.tastyfood.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tastyfood.error.OrderNotFoundException;
import project.tastyfood.model.entity.*;
import project.tastyfood.model.service.OrderServiceModel;
import project.tastyfood.model.view.OrderViewModel;
import project.tastyfood.model.view.OrderedProductViewModel;
import project.tastyfood.model.view.UserViewModel;
import project.tastyfood.repository.OrderRepository;
import project.tastyfood.service.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper, UserService userService) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public void addAddress(OrderServiceModel orderServiceModel,String email) {
        Order order=this.modelMapper.map(orderServiceModel,Order.class);
        List<OrderedProductViewModel> productBag = this.userService.getProductBag(email);
        List<OrderedProduct> meals =new ArrayList<>();
        double totalPrice=0;
        for (OrderedProductViewModel orderedProductViewModel : productBag) {
            OrderedProduct orderedProduct =this.modelMapper.map(orderedProductViewModel, OrderedProduct.class);
            totalPrice+= orderedProduct.getPrice();
            meals.add(orderedProduct);
        }
        order.setMealList(meals);
        order.setRestaurantName(productBag.get(0).getRestaurantName());
        order.setStatus("В процес на изпълнение");
        order.setTotalPrice(totalPrice);
        UserViewModel userByEmail = this.userService.findUserByEmail(email);
        UserEntity userEntity=this.modelMapper.map(userByEmail,UserEntity.class);
        order.setUserEntity(userEntity);
        this.orderRepository.saveAndFlush(order);
        this.userService.clearBag(email);
    }

    @Override
    public List<OrderViewModel> getAllOrders(String email) {
        List<Order>orders=this.orderRepository.findAllByUserEntity_Email(email);
        List<OrderViewModel>ordersViewModels=new ArrayList<>();
        for (Order order : orders) {
            OrderViewModel orderViewModel=new OrderViewModel();
            orderViewModel.setAddress(order.getAddress());
            orderViewModel.setPhoneNumber(order.getPhoneNumber());
            orderViewModel.setTotalPrice(order.getTotalPrice());
            orderViewModel.setStatus(order.getStatus());
            orderViewModel.setRestaurantName(order.getRestaurantName());
            orderViewModel.setId(order.getId());
            String products="";
            int i=0;
            for (OrderedProduct orderedProduct : order.getMealList()) {
                if(i<order.getMealList().size()-1){
                    products += orderedProduct.getMealName()+", ";
                }else{
                    products += orderedProduct.getMealName()+" ";
                }
                i++;
            }
            orderViewModel.setProduct(products);
            ordersViewModels.add(orderViewModel);

        }
        return ordersViewModels;
    }

    @Override
    public void changeStatus(String id) {
        Order order = this.orderRepository.findById(id).orElse(null);
        if(order==null){
            throw new OrderNotFoundException("Order not found");
        }
        order.setStatus("Вашата поръчка е изпълнена");
        this.orderRepository.save(order);
    }

    @Override
    public List<OrderViewModel> getAllOrdersFromRestaurant(String name) {
        List<Order>orders=this.orderRepository.findAllByRestaurantName(name);
        List<OrderViewModel>ordersViewModels=new ArrayList<>();
        for (Order order : orders) {
            OrderViewModel orderViewModel=new OrderViewModel();
            orderViewModel.setAddress(order.getAddress());
            orderViewModel.setPhoneNumber(order.getPhoneNumber());
            orderViewModel.setTotalPrice(order.getTotalPrice());
            orderViewModel.setStatus(order.getStatus());
            orderViewModel.setRestaurantName(order.getRestaurantName());
            orderViewModel.setId(order.getId());
            String products="";
            int i=0;
            for (OrderedProduct orderedProduct : order.getMealList()) {
                if(i<order.getMealList().size()-1){
                    products += orderedProduct.getMealName()+", ";
                }else{
                    products += orderedProduct.getMealName()+" ";
                }
                i++;
            }
            orderViewModel.setProduct(products);
            ordersViewModels.add(orderViewModel);

        }
        return ordersViewModels;
    }
}