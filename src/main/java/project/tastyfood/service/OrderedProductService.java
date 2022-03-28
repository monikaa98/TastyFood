package project.tastyfood.service;

import project.tastyfood.model.service.MealServiceModel;
import project.tastyfood.model.service.OrderedProductServiceModel;

public interface OrderedProductService {
    OrderedProductServiceModel createOrderedProduct(MealServiceModel mealServiceModel);
    void deleteProductById(String id);
}
