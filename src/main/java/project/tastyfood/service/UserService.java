package project.tastyfood.service;

import project.tastyfood.model.service.OrderedProductServiceModel;
import project.tastyfood.model.service.RestaurantServiceModel;
import project.tastyfood.model.service.UserServiceModel;
import project.tastyfood.model.view.OrderedProductViewModel;
import project.tastyfood.model.view.RestaurantViewModel;
import project.tastyfood.model.view.UserViewModel;
import java.util.List;

public interface UserService {
    UserViewModel findUser(String email);
    UserViewModel findUserByEmail(String email);
    boolean existsUser(String email);
    void createAndLoginUser(UserServiceModel userServiceModel);
    void loginUser(String email,String password);
    void addMealToUserProduct(OrderedProductServiceModel orderedProductServiceModel, String email);
    List<OrderedProductViewModel>getProductBag(String email);
    Double totalPrice(String email);
    void clearBag(String email);
    boolean isAdmin(String email);
    void createAdmin();
    boolean isRestaurateur(String email);
    boolean isUser(String email);
    void updateProfile(UserServiceModel userServiceModel,String email);
    void addToFavouriteRestaurant(RestaurantServiceModel restaurantServiceModel, String name);
    List<RestaurantViewModel> getAllFavouriteRestaurants(String email);
    void deleteAMealFromBag(String id,String email);
    void deleteOrderedProducts(String email);
}
