package project.tastyfood.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tastyfood.error.MealNotFoundException;
import project.tastyfood.model.entity.*;
import project.tastyfood.model.entity.enums.CategoryName;
import project.tastyfood.model.service.MealServiceModel;
import project.tastyfood.model.service.OrderedProductServiceModel;
import project.tastyfood.model.service.PictureServiceModel;
import project.tastyfood.model.service.RestaurantServiceModel;
import project.tastyfood.model.view.MealViewModel;
import project.tastyfood.repository.MealRepository;
import project.tastyfood.service.*;
import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository;
    private final ModelMapper modelMapper;
    private final PictureService pictureService;
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderedProductService orderedProductService;

@Autowired
    public MealServiceImpl(MealRepository mealRepository, ModelMapper modelMapper, PictureService pictureService, RestaurantService restaurantService, UserService userService, CategoryService categoryService, OrderedProductService orderedProductService) {
        this.mealRepository = mealRepository;
        this.modelMapper = modelMapper;
        this.pictureService = pictureService;
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.categoryService = categoryService;
    this.orderedProductService = orderedProductService;
}

    @Override
    public void addMeal(MealServiceModel mealServiceModel, PictureServiceModel pictureServiceModel, String restaurantId) {
        Meal meal = this.modelMapper.map(mealServiceModel, Meal.class);
        PictureServiceModel pictureServiceModel1=this.pictureService.
                addPicture(pictureServiceModel);
        meal.setPicture(pictureServiceModel1.getPicture());
        Meal mealCheck =this.mealRepository.findByName(mealServiceModel.getName()).orElse(null);
        if(mealCheck !=null){
            throw new EntityExistsException(String.format("Meal with %s as name, already exists",
                    mealServiceModel.getName()));
        }
        meal.setCategory(categoryService.findByName(mealServiceModel.getCategory()));
        RestaurantServiceModel restaurantServiceModel = this.restaurantService.findById(restaurantId);
        Restaurant restaurant= this.modelMapper.map(restaurantServiceModel, Restaurant.class);
        meal.setRestaurant(restaurant);
        this.mealRepository.saveAndFlush(meal);
    }

    @Override
    public MealServiceModel findById(String id) {
        Meal meal = this.mealRepository.findById(id).orElse(null);
        if(meal !=null){
            MealServiceModel mealServiceModel =this.modelMapper.map(meal, MealServiceModel.class);
            mealServiceModel.setPictureUrl(meal.getPicture().getPictureUrl());
            mealServiceModel.setCategory(meal.getCategory().getName());
            return mealServiceModel;
        }
        throw new MealNotFoundException(id);
}

    @Override
    public MealViewModel buyMeal(String id, String email) {
        Meal meal =this.mealRepository.findById(id).orElse(null);
        MealViewModel mealViewModel =new MealViewModel();
        if(meal !=null){
            MealServiceModel mealServiceModel =this.modelMapper.map(meal, MealServiceModel.class);
            mealServiceModel.setPictureUrl(meal.getPicture().getPictureUrl());
            mealServiceModel.setCategory(meal.getCategory().getName());
            mealServiceModel.setRestaurantName(meal.getRestaurant().getName());
            OrderedProductServiceModel orderedProduct = this.orderedProductService.createOrderedProduct(mealServiceModel);
            this.userService.addMealToUserProduct(orderedProduct,email);
            mealViewModel.setRestaurantId(meal.getRestaurant().getId());

        }
        return mealViewModel;
    }

    @Override
    public List<MealViewModel> findAllMealsByCategoryNameFromThatRestaurant(CategoryName categoryName, String restaurantId) {
        return mealRepository
                .findAllByRestaurant_Id(restaurantId)
                .stream().filter(m->m.getCategory().getName().equals(categoryName)).map(product -> modelMapper.map(product, MealViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        Meal meal = this.mealRepository
                .findById(id)
                .orElseThrow(() -> new MealNotFoundException("Meal with the given id was not found!"));
        mealRepository.delete(meal);
    }

    @Override
    public MealViewModel findByName(String name) {
        MealViewModel mealViewModel = new MealViewModel();
        Meal meal=this.mealRepository.findByName(name).orElse(null);
        if(meal==null){
            throw new MealNotFoundException(name);
        }else {
            mealViewModel.setPictureUrl(meal.getPicture().getPictureUrl());
            mealViewModel.setName(meal.getName());
            mealViewModel.setId(meal.getId());
            mealViewModel.setIngredients(meal.getIngredients());
            mealViewModel.setPrice(meal.getPrice());
            mealViewModel.setGrams(meal.getGrams());
            mealViewModel.setCategory(meal.getCategory().getName());
            mealViewModel.setRestaurantId(meal.getRestaurant().getId());
        }
        return mealViewModel;
    }

    @Override
    public List<MealServiceModel> findAllMealsByRestaurantId(String id) {
        List<Meal> allByRestaurant_id = this.mealRepository.findAllByRestaurant_Id(id);
        List<MealServiceModel>meals=new ArrayList<>();
        for (Meal meal : allByRestaurant_id) {
            MealServiceModel mealServiceModel=this.modelMapper.map(meal,MealServiceModel.class);
            meals.add(mealServiceModel);
        }
        return meals;

    }


}
