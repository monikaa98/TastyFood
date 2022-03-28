package project.tastyfood.service.impl;

import org.springframework.stereotype.Service;
import project.tastyfood.model.entity.OrderedProduct;
import project.tastyfood.model.service.MealServiceModel;
import project.tastyfood.model.service.OrderedProductServiceModel;
import project.tastyfood.repository.OrderedProductRepository;
import project.tastyfood.service.OrderedProductService;
@Service
public class OrderedProductServiceImpl implements OrderedProductService {
    private final OrderedProductRepository orderedProductRepository;

    public OrderedProductServiceImpl(OrderedProductRepository orderedProductRepository) {
        this.orderedProductRepository = orderedProductRepository;
    }

    @Override
    public OrderedProductServiceModel createOrderedProduct(MealServiceModel mealServiceModel) {
        OrderedProduct orderedProduct=new OrderedProduct();
        orderedProduct.setMealId(mealServiceModel.getId());
        orderedProduct.setMealName(mealServiceModel.getName());
        orderedProduct.setMealPictureUrl(mealServiceModel.getPictureUrl());
        orderedProduct.setPrice(mealServiceModel.getPrice());
        orderedProduct.setRestaurantName(mealServiceModel.getRestaurantName());
        OrderedProduct orderedProductFromRepository = this.orderedProductRepository.saveAndFlush(orderedProduct);
        OrderedProductServiceModel orderedProductServiceModel=new OrderedProductServiceModel();
        orderedProductServiceModel.setId(orderedProductFromRepository.getId());
        orderedProductServiceModel.setMealId(orderedProductFromRepository.getMealId());
        orderedProductServiceModel.setMealName(orderedProductFromRepository.getMealName());
        orderedProductServiceModel.setMealPictureUrl(orderedProductFromRepository.getMealPictureUrl());
        orderedProductServiceModel.setPrice(orderedProductFromRepository.getPrice());
        orderedProductServiceModel.setRestaurantName(orderedProductFromRepository.getRestaurantName());
        return orderedProductServiceModel;
    }

    @Override
    public void deleteProductById(String id) {
        this.orderedProductRepository.deleteById(id);
    }
}
