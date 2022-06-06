package uz.abdulqodir.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.Location;
import uz.abdulqodir.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseModel {
    String username;
    String firstName;
    String lastName;
    String phoneNumber;
    Role role;
    List<Card> card = new ArrayList<>();
    Boolean isActive;
    List<Location> locations = new ArrayList<>();
}