package project.tastyfood.web;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.tastyfood.model.binding.TownAddBindingModel;
import project.tastyfood.model.service.MealServiceModel;
import project.tastyfood.model.service.PictureServiceModel;
import project.tastyfood.model.service.TownServiceModel;
import project.tastyfood.model.view.RestaurantViewModel;
import project.tastyfood.service.MealService;
import project.tastyfood.service.RestaurantService;
import project.tastyfood.service.TownService;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/town")
public class TownController {
    private final TownService townService;
    private final ModelMapper modelMapper;
    private final RestaurantService restaurantService;
    private final MealService mealService;
    @Autowired
    public TownController(TownService townService, ModelMapper modelMapper, RestaurantService restaurantService, MealService mealService) {
        this.townService = townService;
        this.modelMapper = modelMapper;
        this.restaurantService = restaurantService;
        this.mealService = mealService;
    }

    @GetMapping("/add")
    public String addTown(Model model) {
        if (!model.containsAttribute("townAddBindingModel")) {
            model.addAttribute("townAddBindingModel", new TownAddBindingModel());
        }
        return "add-town";
    }

    @PostMapping("/add")
    public String addTownConfirm(@Valid @ModelAttribute("townAddBindingModel")
            TownAddBindingModel townAddBindingModel, BindingResult bindingResult,
                                 @RequestParam("picture") MultipartFile file,
                                 RedirectAttributes redirectAttributes, @AuthenticationPrincipal Principal principal){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("townAddBindingModel",townAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.townAddBindingModel"
                    ,bindingResult);
            return "add-town";
        }
        PictureServiceModel pictureServiceModel=new PictureServiceModel();
        pictureServiceModel.setFile(file);
        this.townService.addTown(this.modelMapper.map(townAddBindingModel,
                TownServiceModel.class),pictureServiceModel,principal.getName());

        return "redirect:/home";
    }

    @GetMapping("/delete")
    public String deleteTown(@RequestParam("id")String id){
        List<RestaurantViewModel> restaurantsFromTownById = this.restaurantService.getAllRestaurantsFromTownById(id);
        for (RestaurantViewModel restaurantViewModel : restaurantsFromTownById) {
            List<MealServiceModel> allMealsByRestaurantId = this.mealService.findAllMealsByRestaurantId(restaurantViewModel.getId());
            for (MealServiceModel mealServiceModel : allMealsByRestaurantId) {
                this.mealService.delete(mealServiceModel.getId());
            }
            this.restaurantService.deleteRestaurantById(restaurantViewModel.getId());
        }

        this.townService.deleteTownById(id);
        return "redirect:/home";
    }
}
