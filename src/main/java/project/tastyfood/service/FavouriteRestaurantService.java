package project.tastyfood.service;

import project.tastyfood.model.service.FavouriteRestaurantServiceModel;
import project.tastyfood.model.service.RestaurantServiceModel;

public interface FavouriteRestaurantService {
    FavouriteRestaurantServiceModel createFavouriteRestaurant(RestaurantServiceModel restaurantServiceModel);
}
