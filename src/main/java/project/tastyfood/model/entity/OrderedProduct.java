package project.tastyfood.model.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ordered_product")
public class OrderedProduct extends BaseEntity{
    private String mealId;
    private String mealName;
    private String mealPictureUrl;
    private double price;
    private String restaurantName;
    public OrderedProduct() {
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealPictureUrl() { return mealPictureUrl; }

    public void setMealPictureUrl(String mealPictureUrl) { this.mealPictureUrl = mealPictureUrl; }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
