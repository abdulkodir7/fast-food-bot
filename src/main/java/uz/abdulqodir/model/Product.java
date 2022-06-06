package uz.abdulqodir.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseModel{
    String name;
    String description;
    Category category;
    double price;
    boolean isAvailable;
    String imgPath;
}
