package project.tastyfood.model.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="favourite_restaurants")
public class FavouriteRestaurant extends BaseEntity {
    private String restaurantId;
    private String name;
    private String pictureUrl;
    public FavouriteRestaurant() {
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() { return pictureUrl; }

    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
}
