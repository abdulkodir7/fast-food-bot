package uz.abdulqodir.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseModel {
    final UUID id = UUID.randomUUID();
    final String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh-mm dd/MM/yyyy"));
    String updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh-mm dd/MM/yyyy"));
}
