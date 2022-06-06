package uz.abdulqodir.model.enums;

public enum OrderStatus {
    NEW("New"),
    DELIVERING("Delivering"),
    CLOSED("Delivered");
    private String nameEn;

    OrderStatus(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameEn() {
        return nameEn;
    }
}
