package uz.abdulqodir.bot.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import uz.abdulqodir.bot.model.enums.Round;
import uz.abdulqodir.model.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActivity extends BaseModel {

    User user;
    Long chatId;
    Round lastRound;
    Round currentRound;
    Category currentCategory;
    Product currentProduct;
    Card cancelingProduct;
    String payType;
    double totalProductPrice;
    double deliveryFee;
    int currentQuantity = 1;
    String newProductName;
    String newProductDesc;
    Double newProductPrice;

}
