package uz.abdulqodir.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.abdulqodir.Bot;
import uz.abdulqodir.DB;
import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.bot.model.enums.Round;
import uz.abdulqodir.model.Category;
import uz.abdulqodir.model.Order;
import uz.abdulqodir.model.Product;
import uz.abdulqodir.model.enums.OrderStatus;

import static uz.abdulqodir.bot.service.ReplyKeyboardButton.getAdminButtons;

public class AdminMenu {
    @SneakyThrows
    public static void mainMenu(Update update, UserActivity currentUser) {

        Bot bot = new Bot();
        Round currentRound = currentUser.getCurrentRound();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId().toString());

        if (update.hasMessage()) {
            switch (currentRound) {
                case START:
                    getMenu(currentUser, bot, sendMessage, "Select Menu");
                    break;
                case CATEGORY_CRUD:
                case PRODUCT_CRUD:
                    getMenu(currentUser, bot, sendMessage, "Select");
                    break;
                case CREATE_CATEGORY:
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setText("Input *Category* name");
                    bot.execute(sendMessage);
                    break;
                case CREATE_PRODUCT:
                    currentUser.setCurrentRound(Round.GET_CATEGORIES);
                    sendMessage.setText("Select product category");
                    sendMessage.setReplyMarkup(getAdminButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case ADD_CATEGORY:
                    String categoryName = update.getMessage().getText();
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    DB.categories.add(newCategory);
                    currentUser.setCurrentRound(Round.START);
                    getMenu(currentUser, bot, sendMessage, "Category added successfully. Select Menu");
                    break;
                case GET_PRODUCT_NAME:
                    currentUser.setCurrentRound(Round.SET_PRODUCT_NAME);
                    sendMessage.setText("Input product name");
                    bot.execute(sendMessage);
                    break;
                case GET_PRODUCT_DESC:
                    String productName = update.getMessage().getText();
                    currentUser.setNewProductName(productName);
                    currentUser.setCurrentRound(Round.SET_PRODUCT_DESC);
                    sendMessage.setText("Input product description");
                    bot.execute(sendMessage);
                    break;
                case GET_PRODUCT_PRICE:
                    String productDesc = update.getMessage().getText();
                    currentUser.setNewProductDesc(productDesc);
                    currentUser.setCurrentRound(Round.SET_PRODUCT_PRICE);
                    sendMessage.setText("Input product price");
                    bot.execute(sendMessage);
                    break;
                case ADD_NEW_PRODUCT:
                    String productPrice = update.getMessage().getText();
                    double price = Double.parseDouble(productPrice);
                    currentUser.setNewProductPrice(price);
                    Product newProduct = new Product(currentUser.getNewProductName(),
                            currentUser.getNewProductDesc(),
                            currentUser.getCurrentCategory(),
                            price, true, null
                    );
                    DB.products.add(newProduct);
                    currentUser.setCurrentRound(Round.START);
                    sendMessage.setText("Product added successfully");
                    sendMessage.setReplyMarkup(getAdminButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case READ_CATEGORY:
                    String categories = "Available categories:";
                    for (Category category : DB.categories) {
                        categories += "\n" + category.getName();
                    }

                    sendMessage.setText(categories);
                    bot.execute(sendMessage);
                    currentUser.setCurrentRound(Round.START);
                    getMenu(currentUser, bot, sendMessage, "Select menu");
                    break;
                case READ_PRODUCT:
                    String products = "Available products:";
                    for (Product product : DB.products) {
                        products += "\n - " + product.getCategory().getName() + " -> " + product.getName();
                    }

                    sendMessage.setText(products);
                    bot.execute(sendMessage);
                    currentUser.setCurrentRound(Round.START);
                    getMenu(currentUser, bot, sendMessage, "Select menu");
                    break;
                case ACTIVE_ORDERS:
                    DB.activeOrders.clear();
                    for (Order order : DB.orders) {
                        if (!order.getStatus().equals(OrderStatus.CLOSED)) {
                            DB.activeOrders.add(order);
                        }
                    }
                    if (DB.activeOrders.isEmpty()) {
                        sendMessage.setText("No active orders!");
                        bot.execute(sendMessage);
                    } else {
                        getMenu(currentUser, bot, sendMessage, "Active orders");
                    }
                    break;
            }
        }
    }

    private static void getMenu(UserActivity currentUser, Bot bot, SendMessage sendMessage, String s) throws org.telegram.telegrambots.meta.exceptions.TelegramApiException {
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(getAdminButtons(currentUser));
        bot.execute(sendMessage);
    }
}
