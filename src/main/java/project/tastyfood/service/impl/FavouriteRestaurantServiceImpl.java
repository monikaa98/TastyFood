package project.tastyfood.service.impl;

import org.springframework.stereotype.Service;
import project.tastyfood.model.entity.FavouriteRestaurant;
import project.tastyfood.model.service.FavouriteRestaurantServiceModel;
import project.tastyfood.model.service.RestaurantServiceModel;
import project.tastyfood.repository.FavouriteRestaurantRepository;
import project.tastyfood.service.FavouriteRestaurantService;


@Service
public class FavouriteRestaurantServiceImpl implements FavouriteRestaurantService {
    private final FavouriteRestaurantRepository favouriteRestaurantRepository;

    public FavouriteRestaurantServiceImpl(FavouriteRestaurantRepository favouriteRestaurantRepository) {
        this.favouriteRestaurantRepository = favouriteRestaurantRepository;
    }

    @Override
    public FavouriteRestaurantServiceModel createFavouriteRestaurant(RestaurantServiceModel restaurantServiceModel) {
        FavouriteRestaurant favouriteRestaurant =new FavouriteRestaurant();
        favouriteRestaurant.setName(restaurantServiceModel.getName());
        favouriteRestaurant.setRestaurantId(restaurantServiceModel.getId());
        favouriteRestaurant.setPictureUrl(restaurantServiceModel.getPictureUrl());
        FavouriteRestaurant favouriteRestaurantAfterSave = this.favouriteRestaurantRepository.saveAndFlush(favouriteRestaurant);
        FavouriteRestaurantServiceModel favouriteRestaurantServiceModel =new FavouriteRestaurantServiceModel();
        favouriteRestaurantServiceModel.setId(favouriteRestaurantAfterSave.getId());
        favouriteRestaurantServiceModel.setName(favouriteRestaurantAfterSave.getName());
        favouriteRestaurantServiceModel.setRestaurantId(favouriteRestaurantAfterSave.getRestaurantId());
        favouriteRestaurantServiceModel.setPictureUrl(favouriteRestaurantAfterSave.getPictureUrl());
        return favouriteRestaurantServiceModel;
    }


}

