package project.tastyfood.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.tastyfood.model.binding.AddressAddBindingModel;
import project.tastyfood.model.binding.SearchAddBindingModel;
import project.tastyfood.model.entity.enums.CategoryName;
import project.tastyfood.model.service.OrderServiceModel;
import project.tastyfood.model.service.RestaurantServiceModel;
import project.tastyfood.model.service.UserServiceModel;
import project.tastyfood.model.view.*;
import project.tastyfood.service.MealService;
import project.tastyfood.service.OrderService;
import project.tastyfood.service.RestaurantService;
import project.tastyfood.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;
    private final MealService mealService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper, MealService mealService, UserService userService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
        this.mealService = mealService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/shopping-bag")
    public String shoppingBag(Model model, @RequestParam("id")String id,
                               @AuthenticationPrincipal Principal principal) {
        MealViewModel mealViewModel = this.mealService.buyMeal(id, principal.getName());
        model.addAttribute("salads", mealService
                .findAllMealsByCategoryNameFromThatRestaurant((CategoryName.САЛАТИ),mealViewModel.getRestaurantId()));
        model.addAttribute("desserts", mealService
                .findAllMealsByCategoryNameFromThatRestaurant((CategoryName.ДЕСЕРТИ),mealViewModel.getRestaurantId()));
        model.addAttribute("mainDishes", mealService
                .findAllMealsByCategoryNameFromThatRestaurant((CategoryName.ОСНОВНИ_ЯСТИЯ),mealViewModel.getRestaurantId()));
        model.addAttribute("appetizers", mealService
                .findAllMealsByCategoryNameFromThatRestaurant((CategoryName.ПРЕДЯСТИЯ),mealViewModel.getRestaurantId()));
        model.addAttribute("soups", mealService
                .findAllMealsByCategoryNameFromThatRestaurant((CategoryName.СУПИ),mealViewModel.getRestaurantId()));
        model.addAttribute("isUser",userService.isUser(principal.getName()));
        model.addAttribute("isAdmin",userService.isAdmin(principal.getName()));
        model.addAttribute("isRestaurateur",userService.isRestaurateur(principal.getName()));
        model.addAttribute("searchForMealBindingModel",new SearchAddBindingModel());
        model.addAttribute("name","name");
        return "meals";
    }

    @GetMapping("/add-address")
    public String addAddress(Model model) {
        if (!model.containsAttribute("addressAddBindingModel")) {
            model.addAttribute("addressAddBindingModel", new AddressAddBindingModel());

        }
        return "add-address";
    }

    @PostMapping("/add-address")
    public String addAddressConfirm(@Valid @ModelAttribute("addressAddBindingModel")
            AddressAddBindingModel addressAddBindingModel,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,@AuthenticationPrincipal Principal principal){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("addressAddBindingModel",addressAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressAddBindingModel"
                    ,bindingResult);
            return "add-address";
        }

        this.orderService.addAddress(this.modelMapper.map(addressAddBindingModel,
                OrderServiceModel.class),principal.getName());

        return "successful-order";
    }

    @GetMapping("/orders")
    public String orders(Model model,@AuthenticationPrincipal Principal principal) {
        List<OrderViewModel> orders=this.orderService.getAllOrders(principal.getName());
        model.addAttribute("user",principal);
        model.addAttribute("orders",orders);
        return "orders";
    }

    @GetMapping("/orders-rest")
    public String ordersRestaurant(Model model,@AuthenticationPrincipal Principal principal) {
        List<RestaurantServiceModel> restaurants=this.restaurantService.getAllRestaurantsByOwnersName(principal.getName());
        List<OrderViewModel>orders=new ArrayList<>();
        for (RestaurantServiceModel restaurant : restaurants) {
            List<OrderViewModel> allOrdersFromRestaurant = this.orderService.getAllOrdersFromRestaurant(restaurant.getName());
            for (OrderViewModel orderViewModel : allOrdersFromRestaurant) {
                orders.add(orderViewModel);
            }
        }
        model.addAttribute("isRestaurateur",userService.isRestaurateur(principal.getName()));
        model.addAttribute("user",principal);
        model.addAttribute("orders",orders);
        return "orders";
    }

@GetMapping("/order-reset")
    public String orderReset(@RequestParam("id") String id){
        this.orderService.changeStatus(id);
        return "redirect:/order/orders-rest";
}
}
