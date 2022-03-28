package project.tastyfood.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.tastyfood.error.PasswordNotProvidedException;
import project.tastyfood.error.UserNotFoundException;
import project.tastyfood.model.entity.*;
import project.tastyfood.model.service.*;

import project.tastyfood.model.view.*;

import project.tastyfood.repository.UserRepository;
import project.tastyfood.service.*;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final PictureService pictureService;
    private final FavouriteRestaurantService favouriteRestaurantService;
    private final OrderedProductService orderedProductService;
    private static final Logger LOGGER= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder, @Qualifier("userDetailsServiceImpl")
                                   UserDetailsService userDetailsService, PictureService pictureService, FavouriteRestaurantService favouriteRestaurantService, OrderedProductService orderedProductService) {
        this.userRepository = userRepository;
        this.modelMapper=modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.pictureService = pictureService;
        this.favouriteRestaurantService = favouriteRestaurantService;
        this.orderedProductService = orderedProductService;
    }

    @Override
    public boolean existsUser(String username) {
        Objects.requireNonNull(username);
        return userRepository.findByEmail(username).isPresent();
    }

    @Override
    public void createAndLoginUser(UserServiceModel userServiceModel) {
        UserEntity newUser=createUser(userServiceModel);
        UserDetails userDetails=userDetailsService.loadUserByUsername(newUser.getEmail());
        Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,userServiceModel.getPassword(),
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void loginUser(String username,String password) {
        UserDetails userDetails=userDetailsService.loadUserByUsername(username);
        Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,password,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void addMealToUserProduct(OrderedProductServiceModel orderedProductServiceModel, String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity!=null){
            List<OrderedProduct>bag=new ArrayList<>();
            bag=userEntity.getBag();
            OrderedProduct orderedProduct=new OrderedProduct();
            orderedProduct.setId(orderedProductServiceModel.getId());
            orderedProduct.setMealName(orderedProductServiceModel.getMealName());
            orderedProduct.setMealId(orderedProductServiceModel.getMealId());
            orderedProduct.setMealPictureUrl(orderedProductServiceModel.getMealPictureUrl());
            orderedProduct.setPrice(orderedProductServiceModel.getPrice());
            orderedProduct.setRestaurantName(orderedProductServiceModel.getRestaurantName());
            bag.add(orderedProduct);
            userEntity.setBag(bag);
            this.userRepository.save(userEntity);
        }
    }

    @Override
    public List<OrderedProductViewModel> getProductBag(String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);

        if(userEntity!=null){
            List<OrderedProduct>bag=userEntity.getBag();
            List<OrderedProductViewModel>orderedProductViewModels=new ArrayList<>();
            for(OrderedProduct orderedProduct : bag){
               OrderedProductViewModel orderedProductViewModel=new OrderedProductViewModel();
               orderedProductViewModel.setId(orderedProduct.getId());
               orderedProductViewModel.setMealId(orderedProduct.getMealId());
               orderedProductViewModel.setMealName(orderedProduct.getMealName());
               orderedProductViewModel.setMealPictureUrl(orderedProduct.getMealPictureUrl());
               orderedProductViewModel.setPrice(orderedProduct.getPrice());
               orderedProductViewModel.setRestaurantName(orderedProduct.getRestaurantName());
               orderedProductViewModels.add(orderedProductViewModel);
            }
            return orderedProductViewModels;
        }

        throw new UserNotFoundException(email);
    }

    @Override
    public Double totalPrice(String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity!=null){
            List<OrderedProduct> bag = userEntity.getBag();
            double totalPrice=0;
            for(OrderedProduct orderedProduct : bag){
                totalPrice += orderedProduct.getPrice();
            }
            return totalPrice;
        }
        throw new UserNotFoundException(email);
    }

    @Override
    public void clearBag(String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity!=null){
            List<OrderedProduct>bag=userEntity.getBag();
            userEntity.setBag(new ArrayList<>());
            List<OrderedProduct>products=userEntity.getProducts();
            for (OrderedProduct orderedProduct : bag) {
                products.add(orderedProduct);
            }
            userEntity.setProducts(products);
            this.userRepository.save(userEntity);
        }else{
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public boolean isAdmin(String email) {
        List<String>roles=new ArrayList<>();
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity==null){
            throw new UserNotFoundException(email);
        }
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getRole());
        }
        if(roles.contains("ADMIN")){
            return true;
        }
        return false;
    }

    @Override
    public void createAdmin() {
        if(this.userRepository.count()==0){
            UserEntity userEntity=new UserEntity();
            userEntity.setEmail("admin@abv.bg");
            userEntity.setPassword("admin");
            List<RoleEntity> roles = new ArrayList<>();
            RoleEntity roleEntity=new RoleEntity();
            roleEntity.setRole("ADMIN");
            roles.add(roleEntity);
            userEntity.setRoles(roles);
            userEntity.setFirstName("Admin");
            userEntity.setLastName("Admin");
            userEntity.setRoleName("Admin");
            this.userRepository.saveAndFlush(userEntity);
        }
    }

    @Override
    public boolean isRestaurateur(String email) {

        List<String>roles=new ArrayList<>();
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity==null){
            throw new UserNotFoundException(email);
        }
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getRole());
        }
        if(roles.contains("РЕСТОРАНТЬОР")){
            return true;
        }
        return false;
    }


    @Override
    public boolean isUser(String email) {
        List<String>roles=new ArrayList<>();
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity==null){
            throw new UserNotFoundException(email);
        }
        for (RoleEntity role : userEntity.getRoles()) {
            roles.add(role.getRole());
        }
        if(roles.contains("КЛИЕНТ")){
            return true;
        }
        return false;
    }

    @Override
    public void updateProfile(UserServiceModel userServiceModel,String email)  {
        UserEntity userEntity = this.userRepository.findByEmail(email).orElse(null);
        userEntity.setPassword(passwordEncoder.encode(userServiceModel.getPassword()));
        userEntity.setFirstName(userServiceModel.getFirstName());
        userEntity.setLastName(userServiceModel.getLastName());
        userEntity.setEmail(userServiceModel.getEmail());
        userEntity.setRoleName(userServiceModel.getRoleName());
        List<RoleEntity> roles = userEntity.getRoles();
        roles.clear();
        RoleEntity roleEntity=new RoleEntity();
        roleEntity.setRole(userServiceModel.getRoleName());
        roles.add(roleEntity);
        userEntity.setRoles(roles);
        this.userRepository.save(userEntity);
    }

    @Override
    public void addToFavouriteRestaurant(RestaurantServiceModel restaurantServiceModel, String name) {
        UserEntity userEntity = this.userRepository.findByEmail(name).orElse(null);
        if(userEntity!=null){
            List<FavouriteRestaurant> favouriteRestaurants = userEntity.getFavouriteRestaurants();
            FavouriteRestaurantServiceModel favouriteRestaurantServiceModel = this.favouriteRestaurantService.createFavouriteRestaurant(restaurantServiceModel);
            FavouriteRestaurant favouriteRestaurantEntity =new FavouriteRestaurant();
            favouriteRestaurantEntity.setName(favouriteRestaurantServiceModel.getName());
            favouriteRestaurantEntity.setRestaurantId(favouriteRestaurantServiceModel.getRestaurantId());
            favouriteRestaurantEntity.setId(favouriteRestaurantServiceModel.getId());
            favouriteRestaurantEntity.setPictureUrl(this.pictureService.findByUrl(restaurantServiceModel.getPictureUrl()).getPictureUrl());
            favouriteRestaurants.add(favouriteRestaurantEntity);
            userEntity.setFavouriteRestaurants(favouriteRestaurants);
            this.userRepository.save(userEntity);
        }
    }

    @Override
    public List<RestaurantViewModel> getAllFavouriteRestaurants(String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity!=null){
            List<RestaurantViewModel>favouriteRestaurants=new ArrayList<>();
            for (FavouriteRestaurant favouriteRestaurant : userEntity.getFavouriteRestaurants()) {
                RestaurantViewModel restaurantViewModel=new RestaurantViewModel();
                restaurantViewModel.setPictureUrl(favouriteRestaurant.getPictureUrl());
                restaurantViewModel.setName(favouriteRestaurant.getName());
                restaurantViewModel.setId(favouriteRestaurant.getRestaurantId());
                restaurantViewModel.setFavouriteRestaurantId(favouriteRestaurant.getId());
                favouriteRestaurants.add(restaurantViewModel);
            }
            return favouriteRestaurants;

        }
        return null;
    }

    @Override
    public void deleteAMealFromBag(String id, String email) {
        UserEntity userEntity = this.userRepository.findByEmail(email).orElse(null);
        List<OrderedProduct> bag = userEntity.getBag();
        for (OrderedProduct orderedProduct : bag) {
            if(orderedProduct.getId().equals(id)){
                bag.remove(orderedProduct);
                break;
            }

        }
       userEntity.setBag(bag);
        this.userRepository.save(userEntity);
    }

    @Override
    public void deleteOrderedProducts(String email) {
        UserEntity userEntity=this.userRepository.findByEmail(email).orElse(null);
        if(userEntity!=null){
            List<OrderedProduct>bag=userEntity.getBag();
            userEntity.setBag(new ArrayList<>());
            for (OrderedProduct orderedProduct : bag) {
               this.orderedProductService.deleteProductById(orderedProduct.getId());
            }

            this.userRepository.save(userEntity);
        }else{
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public UserViewModel findUser(String email) {
        UserEntity userEntity =this.userRepository.findByEmail(email).orElse(null);
        UserViewModel userViewModel=new UserViewModel();
        if(userEntity ==null){
            throw new UserNotFoundException(email);
        }else{
            userViewModel=this.modelMapper.map(userEntity,UserViewModel.class);
            List<RoleViewModel>roles=new ArrayList<>();
            for (RoleEntity role : userEntity.getRoles()) {
                RoleViewModel roleViewModel=this.modelMapper.map(role,RoleViewModel.class);
                roles.add(roleViewModel);
            }
            userViewModel.setRoles(roles);
        }
        return userViewModel;
    }

    @Override
    public UserViewModel findUserByEmail(String username) {
        UserEntity userEntity=this.userRepository.findByEmail(username).orElse(null);
        if(userEntity!=null){
            UserViewModel userViewModel=this.modelMapper.map(userEntity,UserViewModel.class);
            userViewModel.setEmail(userEntity.getEmail());
            return userViewModel;
        }
        throw new UserNotFoundException(username);
    }

    private UserEntity createUser(UserServiceModel userServiceModel){
        UserEntity userEntity=new UserEntity();
        LOGGER.info("Creating a new user with email [GDPR].");
        userEntity=this.createUserWithRoles(userServiceModel,userServiceModel.getRoleName());
        return userRepository.save(userEntity);
    }
    private UserEntity createUserWithRoles(UserServiceModel userServiceModel,String role){
        UserEntity userEntity = this.modelMapper.map(userServiceModel, UserEntity.class);


        if(userServiceModel.getPassword()!=null){
            userEntity.setPassword(passwordEncoder.encode(userServiceModel.getPassword()));
        }else{
            throw new PasswordNotProvidedException();
        }
        RoleEntity userRole=new RoleEntity();
        userRole.setRole(role);
        userEntity.setRoles(List.of(userRole));
        return userEntity;
    }

}

