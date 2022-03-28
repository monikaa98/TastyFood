package project.tastyfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.tastyfood.model.entity.FavouriteRestaurant;

@Repository
public interface FavouriteRestaurantRepository extends JpaRepository<FavouriteRestaurant,String> {

}
