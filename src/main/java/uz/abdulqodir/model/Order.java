package uz.abdulqodir.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.model.enums.OrderStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseModel {
    UserActivity userActivity;
    OrderStatus status;
    final int orderNumber = (int) (Math.random() * (9999 - 1000) + 1000);

}
