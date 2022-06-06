package uz.abdulqodir;

import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.model.Category;
import uz.abdulqodir.model.Order;
import uz.abdulqodir.model.Product;
import uz.abdulqodir.model.User;
import uz.abdulqodir.model.enums.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DB {
    public static List<Category> categories = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();
    public static List<UserActivity> userActivities = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    public static List<Order> orders = new ArrayList<>();
    public static List<Order> activeOrders = new ArrayList<>();

    public static void initializeData() {

        categories.addAll(Arrays.asList(
                new Category("Set", "src/main/resources/admin/category-images/set.jpg"),
                new Category("Lavash", "src/main/resources/admin/category-images/lavash.jpg"),
                new Category("Shaurma", "src/main/resources/admin/category-images/shaurma.jpg"),
                new Category("Donar", "src/main/resources/admin/category-images/donar.jpg"),
                new Category("Burger", "src/main/resources/admin/category-images/burger.jpg"),
                new Category("Hot-Dog", "src/main/resources/admin/category-images/hot-dog.jpg"),
                new Category("Desserts", "src/main/resources/admin/category-images/dessert.jpg"),
                new Category("Drinks", "src/main/resources/admin/category-images/drinks.jpg"),
                new Category("Garnishes", "src/main/resources/admin/category-images/garnishes.jpg")

        ));
        products.addAll(Arrays.asList(
                new Product("Kids COMBO", "Great offer for little guests including juice, fri, hot-dog. ", categories.get(0), 15000, true, "src/main/resources/admin/product-images/kombo-kids.jpg"),
                new Product("FITTER", "Chicken meat, crispy iceberg lettuce, fresh cucumbers and tomatoes, fetaxa and our signature sauce - all wrapped in green lavash", categories.get(1), 20000, true, "src/main/resources/admin/product-images/fitter.jpg"),
                new Product("Beef Lavash with pepper", "Juicy beef wrapped in pita bread with crispy chips, fresh cucumbers and tomatoes, with a special spicy sauce", categories.get(1), 22000, true, "src/main/resources/admin/product-images/beef-pepper-lavash.jpg"),
                new Product("Chicken Lavash with pepper", "Fried chicken fillet in pita bread with fresh cucumbers and tomatoes, crispy chips and specialty hot sauce", categories.get(1), 22000, true, "src/main/resources/admin/product-images/chicken-pepper-lavash.jpg"),
                new Product("Beef Lavash", "Juicy beef wrapped in pita bread with crispy chips, fresh cucumbers and tomatoes, with our signature tomato sauce", categories.get(1), 22000, true, "src/main/resources/admin/product-images/beef-lavash.jpg"),
                new Product("Beef Lavash", "Juicy beef wrapped in pita bread with crispy chips, fresh cucumbers and tomatoes, with our signature tomato sauce", categories.get(1), 22000, true, "src/main/resources/admin/product-images/beef-lavash.jpg")

        ));
        User admin = new User("Ganiev_A", "Abdulqodir", "G'aniyev", "+998901570711", Role.ADMIN, null, true, null);
        users.add(admin);
        UserActivity userActivity = new UserActivity();
//        userActivity.setUser(admin);
//        userActivity.setChatId(1085688741L);
//        userActivities.add(userActivity);
    }

}
