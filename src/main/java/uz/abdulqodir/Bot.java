package uz.abdulqodir;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.abdulqodir.bot.service.AdminMenu;
import uz.abdulqodir.bot.service.CustomerMenu;
import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.bot.model.enums.Round;
import uz.abdulqodir.model.Card;
import uz.abdulqodir.model.Category;
import uz.abdulqodir.model.Product;
import uz.abdulqodir.model.User;
import uz.abdulqodir.model.enums.Role;


import static uz.abdulqodir.bot.Constant.*;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);

        // MESSAGE
        if (update.hasMessage()) {
            Message message = update.getMessage();
            UserActivity currentUser = getCurrentUser(message);

            if (currentUser.getUser().getRole().equals(Role.ADMIN)) {
                getAdminCondition(currentUser, update);
            }

            // TEXT
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals(START) && currentUser.getUser().getRole().equals(Role.CUSTOMER)) {
                    currentUser.setCurrentRound(Round.START);
                } else if (text.equals(MENU)) {
                    currentUser.setCurrentRound(Round.SEND_LOCATION);
                } else if (text.equals("My card (" + currentUser.getUser().getCard().size() + ")")) {
                    currentUser.setCurrentRound(Round.GET_CARD);
                } else if (getCategory(text, currentUser)) {
                    currentUser.setCurrentRound(Round.GET_PRODUCTS);
                } else if (getProduct(text, currentUser)) {
                    currentUser.setCurrentRound(Round.GET_SELECTED_PRODUCT);
                } else if (text.equals("Cash")) {
                    currentUser.setPayType("Cash");
                    currentUser.setCurrentRound(Round.CONFIRMATION);
                } else if (text.equals("Click")) {
                    currentUser.setPayType("Click");
                    currentUser.setCurrentRound(Round.CONFIRMATION);
                } else if (text.equals("Payme")) {
                    currentUser.setPayType("Payme");
                    currentUser.setCurrentRound(Round.CONFIRMATION);
                } else if (currentUser.getCurrentRound().equals(Round.CONFIRMATION) && text.equals("No")) {
                    currentUser.setCurrentRound(Round.REJECT_CONFIRMATION);
                } else if (currentUser.getCurrentRound().equals(Round.CONFIRMATION) && text.equals("Yes")) {
                    currentUser.setCurrentRound(Round.ACCEPT_CONFIRMATION);
                } else if (text.equals(ORDER_HISTORY)) {
                    currentUser.setCurrentRound(Round.MY_ORDERS);
                } else if (text.equals(SETTINGS)) {
                    currentUser.setCurrentRound(Round.SETTINGS);
                } else if (text.equals(LEAVE_FEEDBACK)) {
                    currentUser.setCurrentRound(Round.LEAVE_FEEDBACK);
                }

                getRoleAndMenu(update, currentUser);
            } else if (message.hasLocation()) {
                currentUser.getUser().getLocations().add(message.getLocation());
//                methodL(update);
                currentUser.setCurrentRound(Round.GET_CATEGORIES);
                getRoleAndMenu(update, currentUser);
            } else if (message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                currentUser.getUser().setPhoneNumber(phoneNumber);
                currentUser.setCurrentRound(Round.PAY_TYPE);
                getRoleAndMenu(update, currentUser);
            }
        }

        // CALL BACK QUERY
        else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Message message = callbackQuery.getMessage();
            UserActivity currentUser = getCurrentUser(message);

            if (data.equals("increment")) {
                currentUser.setCurrentRound(Round.INCREMENT);
            } else if (data.equals("decrement")) {
                currentUser.setCurrentRound(Round.DECREMENT);
            } else if (data.equals("addToCard")) {
                currentUser.setCurrentRound(Round.ADD_TO_CARD);
            } else if (data.equals(getCancelingProduct(data, currentUser))) {
                currentUser.setCurrentRound(Round.CANCEL_PRODUCT);
            } else if (data.equals("clear_card")) {
                currentUser.setCurrentRound(Round.CLEAR_CARD);
            } else if (data.equals("confirm_order")) {
                currentUser.setCurrentRound(Round.CONFIRM_ORDER);
            }
            getRoleAndMenu(update, currentUser);
        }
    }


    private void getAdminCondition(UserActivity currentUser, Update update) {
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                currentUser.setCurrentRound(Round.START);
            } else if (text.equals("Category")) {
                currentUser.setCurrentRound(Round.CATEGORY_CRUD);
            } else if (text.equals("Create") && currentUser.getCurrentRound().equals(Round.CATEGORY_CRUD)) {
                currentUser.setCurrentRound(Round.CREATE_CATEGORY);
            } else if (currentUser.getCurrentRound().equals(Round.CREATE_CATEGORY)) {
                currentUser.setCurrentRound(Round.ADD_CATEGORY);
            } else if (text.equals("Read") && currentUser.getCurrentRound().equals(Round.CATEGORY_CRUD)) {
                currentUser.setCurrentRound(Round.READ_CATEGORY);
            } else if (text.equals("Active orders")) {
                currentUser.setCurrentRound(Round.ACTIVE_ORDERS);
            } else if (text.equals(BACK)) {
                currentUser.setCurrentRound(Round.START);
            } else if (text.equals("Product")) {
                currentUser.setCurrentRound(Round.PRODUCT_CRUD);
            } else if (text.equals("Create") && currentUser.getCurrentRound().equals(Round.PRODUCT_CRUD)) {
                currentUser.setCurrentRound(Round.CREATE_PRODUCT); //****
            } else if (getCategory(text, currentUser)) {
                currentUser.setCurrentRound(Round.GET_PRODUCT_NAME);
            } else if (currentUser.getCurrentRound().equals(Round.SET_PRODUCT_NAME)) {
                currentUser.setCurrentRound(Round.GET_PRODUCT_DESC);
            } else if (currentUser.getCurrentRound().equals(Round.SET_PRODUCT_DESC)) {
                currentUser.setCurrentRound(Round.GET_PRODUCT_PRICE);
            } else if (currentUser.getCurrentRound().equals(Round.SET_PRODUCT_PRICE)) {
                currentUser.setCurrentRound(Round.ADD_NEW_PRODUCT);
            } else if (text.equals("Read") && currentUser.getCurrentRound().equals(Round.PRODUCT_CRUD)) {
                currentUser.setCurrentRound(Round.READ_PRODUCT);
            }
            getRoleAndMenu(update, currentUser);
        } else if (currentUser.getCurrentRound().equals(Round.SET_PRODUCT_PRICE) && update.getMessage().hasPhoto()) {

            // TODO: 1/9/2022  

        }

    }

    private String getCancelingProduct(String data, UserActivity currentUser) {
        for (Card card : currentUser.getUser().getCard()) {
            if (data.equals("cancel" + card.getProduct().getName())) {
                currentUser.setCancelingProduct(card);
                return "cancel" + card.getProduct().getName();
            }
        }
        return "";
    }

    private boolean getProduct(String text, UserActivity currentUser) {
        for (Product product : DB.products) {
            if (product.getName().equals(text)) {
                currentUser.setCurrentProduct(product);
                return true;
            }
        }
        return false;
    }

    private boolean getCategory(String text, UserActivity currentUser) {
        for (Category category : DB.categories) {
            if (category.getName().equals(text)) {
                currentUser.setCurrentCategory(category);
                return true;
            }
        }
        return false;
    }

    private void getRoleAndMenu(Update update, UserActivity currentUser) {
        switch (currentUser.getUser().getRole()) {

            case ADMIN:
                AdminMenu.mainMenu(update, currentUser);
                break;
            case CUSTOMER:
                CustomerMenu.mainMenu(update, currentUser);
                break;
            case DELIVERER:
                // TODO: 12/31/2021 DELIVERER PANEL
                break;
        }
    }


    private UserActivity getCurrentUser(Message message) {
        Long chatId = message.getChatId();

        for (UserActivity userActivity : DB.userActivities) {
            if (userActivity.getChatId().equals(chatId)) {
                return userActivity;
            }
        }

        User newUser = new User();
        String firstName = message.getFrom().getFirstName();
        newUser.setFirstName(firstName);
        newUser.setRole(Role.CUSTOMER);

        if (message.getFrom().getUserName() != null) {
            String userName = message.getFrom().getUserName();
            newUser.setUsername(userName);
        }
        if (message.getFrom().getLastName() != null) {
            String lastName = message.getFrom().getLastName();
            newUser.setLastName(lastName);
        }

        UserActivity newUserActivity = new UserActivity();
        newUserActivity.setUser(newUser);
        newUserActivity.setChatId(chatId);
        newUserActivity.setCurrentRound(Round.START);

        DB.users.add(newUser);
        DB.userActivities.add(newUserActivity);

        return newUserActivity;
    }

}
