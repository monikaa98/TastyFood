package project.tastyfood.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.tastyfood.model.binding.UserLoginBindingModel;
import project.tastyfood.model.binding.UserRegisterBindingModel;
import project.tastyfood.model.binding.UserUpdateProfileBindingModel;
import project.tastyfood.model.service.*;
import project.tastyfood.model.view.OrderedProductViewModel;
import project.tastyfood.model.view.UserViewModel;
import project.tastyfood.service.UserService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegisterBindingModel")) {
            model.addAttribute("userRegisterBindingModel", new UserRegisterBindingModel());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerConfirm(@Valid @ModelAttribute("userRegisterBindingModel")
                                          UserRegisterBindingModel userRegisterBindingModel,
                                  BindingResult bindingResult, HttpSession httpSession, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors() || !
                userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.
                    addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel",
                            bindingResult);
            return "register";
        }
        if (userService.existsUser(userRegisterBindingModel.getEmail())) {
            bindingResult.rejectValue
                    ("email", "error.email", "An account with this email already exists.");
            return "register";
        }
        UserServiceModel userServiceModel = this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class);
        this.userService.createAndLoginUser(userServiceModel);
        model.addAttribute("name", httpSession.getAttribute("application-name"));

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("userLoginBindingModel")) {
            model.addAttribute("userLoginBindingModel", new UserLoginBindingModel());

        }
        return "login";
    }

    @PostMapping("/login")
    public String loginConfirm(@Valid @ModelAttribute("userLoginBindingModel")
                                       UserLoginBindingModel userLoginBindingModel,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("userLoginBindingModel", userLoginBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userLoginBindingModel",
                    bindingResult);
            return "login";
        }
        UserViewModel user = this.userService.findUserByEmail(userLoginBindingModel.getEmail());
        if (user == null) {
            redirectAttributes.addFlashAttribute("userLoginBindingModel", userLoginBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userLoginBindingModel",
                    bindingResult);

            return "login";
        }

        this.userService.loginUser(user.getEmail(), userLoginBindingModel.getPassword());

        return "redirect:/home";

    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/";
    }

    @GetMapping("/shopping-bag")
    public String shoppingBag(Model model, @AuthenticationPrincipal Principal principal) {
        List<OrderedProductViewModel> productBag = this.userService.getProductBag(principal.getName());
        model.addAttribute("totalPrice", this.userService.totalPrice(principal.getName()));
        model.addAttribute("quantity", this.userService.totalPrice(principal.getName()));
        model.addAttribute("products", productBag);
        model.addAttribute("isUser",userService.isUser(principal.getName()));
        return "shopping-bag";
    }

    @GetMapping("/profile")
    public ModelAndView profile(ModelAndView modelAndView, @AuthenticationPrincipal Principal principal) {
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", principal);
        modelAndView.addObject("isUser",userService.isUser(principal.getName()));
        modelAndView.addObject("isRestaurateur",userService.isRestaurateur(principal.getName()));
        return modelAndView;
    }

    @GetMapping("/profile-info")
    public ModelAndView profileInfo(@RequestParam("email") String email, ModelAndView modelAndView,
                                @AuthenticationPrincipal Principal principal) {
        modelAndView.setViewName("user-information");
        UserViewModel userViewModel = this.userService.findUser(email);
        modelAndView.addObject("user", userViewModel);
        modelAndView.addObject("email", principal.getName());
        modelAndView.addObject("userUpdate",userViewModel);
        return modelAndView;
    }

    @GetMapping("/update-user-information")
    public ModelAndView updateUserInformation(@RequestParam("email") String email, ModelAndView modelAndView,
                                    @AuthenticationPrincipal Principal principal) {
        modelAndView.setViewName("update-user-information");
        UserViewModel userViewModel = this.userService.findUser(email);
        modelAndView.addObject("user", userViewModel);
        modelAndView.addObject("email", principal.getName());
        modelAndView.addObject("userUpdate",userViewModel);
        return modelAndView;
    }

    @PostMapping("/update")
    public ModelAndView profileUpdate(@RequestParam("email") String email,@Valid @ModelAttribute UserUpdateProfileBindingModel userUpdateProfileBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,ModelAndView modelAndView,Principal principal) throws IOException {
        if (bindingResult.hasErrors() || !userUpdateProfileBindingModel.getPassword()
                .equals(userUpdateProfileBindingModel.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userUpdate", userUpdateProfileBindingModel);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);
            modelAndView.addObject("email", email);
            modelAndView.setViewName("redirect:/user/update-user-information");
            return modelAndView;
        }
        UserServiceModel userServiceModel = this.modelMapper.map(userUpdateProfileBindingModel, UserServiceModel.class);
        userServiceModel.setEmail(userServiceModel.getEmail());
        this.userService.updateProfile(userServiceModel,email);
        modelAndView.addObject("email", email);
        modelAndView.setViewName("redirect:/user/profile-info");
        return modelAndView;
    }

    @GetMapping("/favouriteRestaurant")
    public ModelAndView favouriteRestaurants(@RequestParam("email")String email,ModelAndView modelAndView,
                                   @AuthenticationPrincipal Principal principal){
        modelAndView.setViewName("favourite-restaurants");
        modelAndView.addObject("favouriteRestaurants",this.userService.getAllFavouriteRestaurants(email));
        modelAndView.addObject("email",principal.getName());
        modelAndView.addObject("isRestaurateur",userService.isRestaurateur(principal.getName()));
        modelAndView.addObject("isUser",userService.isUser(principal.getName()));
        return modelAndView;
    }

    @GetMapping("/clearBag")
    public String clearBag(@AuthenticationPrincipal Principal principal){
          this.userService.deleteOrderedProducts(principal.getName());
        return "redirect:/user/shopping-bag";
    }
}