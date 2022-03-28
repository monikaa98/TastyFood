package project.tastyfood.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import project.tastyfood.model.binding.SearchAddBindingModel;
import project.tastyfood.model.view.MealViewModel;
import project.tastyfood.model.view.RestaurantViewModel;
import project.tastyfood.model.view.TownViewModel;
import project.tastyfood.service.MealService;
import project.tastyfood.service.RestaurantService;
import project.tastyfood.service.TownService;
import project.tastyfood.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/search")
public class SearchController {
    private final TownService townService;
    private final RestaurantService restaurantService;
    private final MealService mealService;
    private final UserService userService;

    public SearchController(TownService townService, RestaurantService restaurantService, MealService mealService, UserService userService) {
        this.townService = townService;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
        this.userService = userService;
    }

    @PostMapping("/town")
    public ModelAndView searchTown(@Valid @ModelAttribute("searchAddBindingModel")
                                   SearchAddBindingModel searchAddBindingModel, ModelAndView modelAndView,
                                   @AuthenticationPrincipal Principal principal){
        TownViewModel townViewModel=this.townService.findByName(searchAddBindingModel.getName());
        modelAndView.addObject("town",townViewModel);
        modelAndView.addObject("email",principal.getName());
        modelAndView.addObject("isRestaurateur",userService.isRestaurateur(principal.getName()));
        modelAndView.addObject("isAdmin",userService.isAdmin(principal.getName()));
        modelAndView.addObject("isUser",userService.isUser(principal.getName()));
        modelAndView.setViewName("search-town");
        return modelAndView;
    }

    @PostMapping("/restaurant")
    public ModelAndView searchRestaurant(@Valid @ModelAttribute("searchAddBindingModel")
                                   SearchAddBindingModel searchAddBindingModel, ModelAndView modelAndView,
                                   @AuthenticationPrincipal Principal principal){
        RestaurantViewModel restaurantViewModel=this.restaurantService.findByName(searchAddBindingModel.getName());
        modelAndView.addObject("restaurant",restaurantViewModel);
        modelAndView.addObject("email",principal.getName());
        modelAndView.addObject("isRestaurateur",userService.isRestaurateur(principal.getName()));
        modelAndView.addObject("isAdmin",userService.isAdmin(principal.getName()));
        modelAndView.addObject("isUser",userService.isUser(principal.getName()));
        modelAndView.setViewName("search-restaurant");
        return modelAndView;
    }

    @PostMapping("/meal")
    public ModelAndView searchMeal(@Valid @ModelAttribute("searchAddBindingModel")
                                         SearchAddBindingModel searchAddBindingModel, ModelAndView modelAndView,
                                         @AuthenticationPrincipal Principal principal){
        MealViewModel mealViewModel=this.mealService.findByName(searchAddBindingModel.getName());
        modelAndView.addObject("meal",mealViewModel);
        modelAndView.addObject("email",principal.getName());
        modelAndView.addObject("isRestaurateur",userService.isRestaurateur(principal.getName()));
        modelAndView.addObject("isAdmin",userService.isAdmin(principal.getName()));
        modelAndView.addObject("isUser",userService.isUser(principal.getName()));
        modelAndView.setViewName("search-meal");
        return modelAndView;
    }
}
