package uz.abdulqodir.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.abdulqodir.DB;
import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.bot.model.enums.Round;
import uz.abdulqodir.model.Card;
import uz.abdulqodir.model.Category;
import uz.abdulqodir.model.Order;
import uz.abdulqodir.model.Product;

import java.util.ArrayList;
import java.util.List;

import static uz.abdulqodir.bot.Constant.*;

public class ReplyKeyboardButton {
    public static ReplyKeyboard getCustomerButtons(UserActivity currentUser) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        Round currentRound = currentUser.getCurrentRound();
        int currentQuantity = currentUser.getCurrentQuantity();
        switch (currentRound) {
            case START:
                KeyboardRow menuRow = new KeyboardRow();
                menuRow.add(MENU);
                KeyboardRow historyRow = new KeyboardRow();
                historyRow.add(ORDER_HISTORY);
                KeyboardRow lastRow = new KeyboardRow();
                lastRow.add(LEAVE_FEEDBACK);
                lastRow.add(SETTINGS);
                rows.add(menuRow);
                rows.add(historyRow);
                rows.add(lastRow);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case SEND_LOCATION:
                KeyboardRow locationRow = new KeyboardRow();
                KeyboardButton location = new KeyboardButton();
                location.setText("Send Location");
                location.setRequestLocation(true);
                locationRow.add(location);
                rows.add(locationRow);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case GET_CATEGORIES:
                String myCard = "My card ";
                if (currentUser.getUser().getCard().isEmpty()) {
                    myCard += "(0)";
                } else {
                    int size = currentUser.getUser().getCard().size();
                    myCard += "(" + size + ")";
                }
                KeyboardRow cardRow = new KeyboardRow();
                cardRow.add(myCard);
                rows.add(cardRow);

                KeyboardRow rowN = new KeyboardRow();
                for (int i = 0; i < DB.categories.size(); i++) {
                    String name = DB.categories.get(i).getName();
                    rowN.add(name);
                    if (i % 2 != 0) {
                        rows.add(rowN);
                        rowN = new KeyboardRow();
                    }
                }
                rows.add(rowN);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case GET_CARD:
                List<InlineKeyboardButton> confirmRow = new ArrayList<>();
                List<InlineKeyboardButton> clearRow = new ArrayList<>();

                InlineKeyboardButton confirm = new InlineKeyboardButton();
                confirm.setCallbackData("confirm_order");
                confirm.setText("Confirm Order");
                confirmRow.add(confirm);

                InlineKeyboardButton clear = new InlineKeyboardButton();
                clear.setCallbackData("clear_card");
                clear.setText("Clear Card");
                clearRow.add(clear);

                keyboard.add(confirmRow);
                keyboard.add(clearRow);

                List<InlineKeyboardButton> rowM;
                InlineKeyboardButton buttonM;
                for (Card card : currentUser.getUser().getCard()) {
                    rowM = new ArrayList<>();
                    buttonM = new InlineKeyboardButton();

                    buttonM.setText("X " + card.getProduct().getName());
                    buttonM.setCallbackData("cancel" + card.getProduct().getName());
                    rowM.add(buttonM);
                    keyboard.add(rowM);
                }

                inlineKeyboardMarkup.setKeyboard(keyboard);
                return inlineKeyboardMarkup;
            case GET_PRODUCTS:
                Category currentCategory = currentUser.getCurrentCategory();
                List<Product> availableProducts = new ArrayList<>();
                for (Product product : DB.products) {
                    if (product.getCategory().equals(currentCategory)) {
                        availableProducts.add(product);
                    }
                }

                KeyboardRow rowP = new KeyboardRow();
                for (int i = 0; i < availableProducts.size(); i++) {
                    String name = availableProducts.get(i).getName();
                    rowP.add(name);
                    if (i % 2 != 0) {
                        rows.add(rowP);
                        rowP = new KeyboardRow();
                    }
                }
                rows.add(rowP);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case GET_SELECTED_PRODUCT:
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();

                InlineKeyboardButton decrement = new InlineKeyboardButton();
                decrement.setText("-");
                decrement.setCallbackData("decrement");
                row1.add(decrement);

                InlineKeyboardButton amount = new InlineKeyboardButton();
                amount.setText(String.valueOf(currentQuantity));
                amount.setCallbackData("amount");
                row1.add(amount);

                InlineKeyboardButton increment = new InlineKeyboardButton();
                increment.setText("+");
                increment.setCallbackData("increment");
                row1.add(increment);

                InlineKeyboardButton addToCard = new InlineKeyboardButton();
                addToCard.setText("Add to card");
                addToCard.setCallbackData("addToCard");
                row2.add(addToCard);

                keyboard.add(row1);
                keyboard.add(row2);
                inlineKeyboardMarkup.setKeyboard(keyboard);
                return inlineKeyboardMarkup;
            case SHARE_CONTACT:
                KeyboardRow contactRow = new KeyboardRow();
                KeyboardButton contact = new KeyboardButton();
                contact.setText("Share Contact");
                contact.setRequestContact(true);
                contactRow.add(contact);
                rows.add(contactRow);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case PAY_TYPE:
                KeyboardRow cashRow = new KeyboardRow();
                cashRow.add("Cash");
                rows.add(cashRow);
                KeyboardRow clickRow = new KeyboardRow();
                clickRow.add("Click");
                rows.add(clickRow);
                KeyboardRow paymeRow = new KeyboardRow();
                paymeRow.add("Payme");
                rows.add(paymeRow);
                KeyboardRow back = new KeyboardRow();
                back.add("Back");
                rows.add(back);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case CONFIRMATION:
                KeyboardRow optRow = new KeyboardRow();
                optRow.add("No");
                optRow.add("Yes");
                rows.add(optRow);
                KeyboardRow backRow = new KeyboardRow();
                backRow.add("Back");
                rows.add(backRow);
                replyKeyboardMarkup.setKeyboard(rows);

        }
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard getAdminButtons(UserActivity currentUser) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        KeyboardRow lastRow = new KeyboardRow();

        switch (currentUser.getCurrentRound()) {
            case START:
                firstRow.add("Category");
                firstRow.add("Product");
                secondRow.add("Users");
                secondRow.add("Order history");
                lastRow.add("Active orders");
                rows.add(lastRow);
                rows.add(firstRow);
                rows.add(secondRow);
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case PRODUCT_CRUD:
            case CATEGORY_CRUD:
                firstRow.add("Create");
                firstRow.add("Read");
                secondRow.add("Update");
                secondRow.add("Delete");
                rows.add(firstRow);
                rows.add(secondRow);
                rows.add(getBackButton());
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case ACTIVE_ORDERS:
                for (Order activeOrder : DB.activeOrders) {
                    KeyboardRow rowN = new KeyboardRow();
                    rowN.add(activeOrder.getStatus().getNameEn() + " - " + activeOrder.getOrderNumber());
                    rows.add(rowN);
                }

                rows.add(getBackButton());
                replyKeyboardMarkup.setKeyboard(rows);
                break;
            case GET_CATEGORIES:
                KeyboardRow rowN = new KeyboardRow();
                for (int i = 0; i < DB.categories.size(); i++) {
                    String name = DB.categories.get(i).getName();
                    rowN.add(name);
                    if (i % 2 != 0) {
                        rows.add(rowN);
                        rowN = new KeyboardRow();
                    }
                }
                rows.add(rowN);
                replyKeyboardMarkup.setKeyboard(rows);

        }

        return replyKeyboardMarkup;
    }

    private static KeyboardRow getBackButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(BACK);
        return row;
    }


}
