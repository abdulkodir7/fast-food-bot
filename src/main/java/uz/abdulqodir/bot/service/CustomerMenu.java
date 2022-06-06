package uz.abdulqodir.bot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.abdulqodir.Bot;
import uz.abdulqodir.DB;
import uz.abdulqodir.bot.model.UserActivity;
import uz.abdulqodir.bot.model.enums.Round;
import uz.abdulqodir.model.Card;
import uz.abdulqodir.model.Order;
import uz.abdulqodir.model.Product;
import uz.abdulqodir.model.enums.OrderStatus;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static uz.abdulqodir.bot.service.ReplyKeyboardButton.getAdminButtons;
import static uz.abdulqodir.bot.service.ReplyKeyboardButton.getCustomerButtons;

public class CustomerMenu {

    @SneakyThrows
    public static void mainMenu(Update update, UserActivity currentUser) {
        Bot bot = new Bot();
        Round currentRound = currentUser.getCurrentRound();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId().toString());

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(currentUser.getChatId().toString());

        if (update.hasMessage()) {
            switch (currentRound) {
                case START:
                    sendMessage.setText("Choose one of the following");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case SEND_LOCATION:
                    sendMessage.setText("Where to deliver? Send your location");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case GET_CATEGORIES:
                    sendMessage.setText("Select category");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case GET_CARD:
                    if (currentUser.getUser().getCard().isEmpty()) {
                        sendMessage.setText("Card is empty!");
                    } else {
                        String text = getCardText(currentUser);

                        sendMessage.setText(text);
                        sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                        sendMessage.setParseMode(ParseMode.MARKDOWN);
                    }
                    bot.execute(sendMessage);
                    break;
                case GET_PRODUCTS:
                    File file = new File(currentUser.getCurrentCategory().getImgPath());
                    InputFile inputFile = new InputFile(file, "photo");
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(inputFile);
                    sendPhoto.setChatId(currentUser.getChatId().toString());
                    bot.execute(sendPhoto);

                    sendMessage.setText("Select product");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case GET_SELECTED_PRODUCT:
                    Product currentProduct = currentUser.getCurrentProduct();
                    String name = currentProduct.getName();
                    String description = currentProduct.getDescription();
                    double price = currentProduct.getPrice();
                    String body = name + "\n" + description + "\nPrice: " + price;

                    SendPhoto sendPhoto1 = new SendPhoto(currentUser.getChatId().toString(), new InputFile(new File(currentProduct.getImgPath()), "aq"));
                    sendPhoto1.setReplyMarkup(getCustomerButtons(currentUser));

                    sendPhoto1.setCaption(body);
                    try {
                        bot.execute(sendPhoto1);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case PAY_TYPE:
                    sendMessage.setText("Please select a payment type");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case CONFIRMATION:
                    String cardText = getCardText(currentUser);
                    String s = cardText.replaceAll("In your card:", "Location: \n");
                    s += "\n\nPayment type: " + currentUser.getPayType() + "\n\n*Do you confirm?*";
                    sendMessage.setText(s);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case REJECT_CONFIRMATION:
                    currentUser.setCurrentProduct(null);
                    currentUser.setCurrentRound(Round.START);
                    currentUser.setCurrentCategory(null);
                    currentUser.setCurrentQuantity(1);
                    currentUser.setCancelingProduct(null);
                    currentUser.setPayType(null);
                    currentUser.getUser().getCard().clear();
                    currentUser.setTotalProductPrice(0);
                    sendMessage.setText("Select menu");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case ACCEPT_CONFIRMATION:
                    Order newOrder = new Order(currentUser, OrderStatus.NEW);
                    DB.orders.add(newOrder);

                    String orderInfo = getCardText(currentUser);
                    String text = orderInfo.replaceAll("In your card:",
                            "Order number: " + newOrder.getOrderNumber()
                                    + "\nStatus: " + newOrder.getStatus().getNameEn()
                                    + "\nPayment type: " + newOrder.getUserActivity().getPayType() + "\n"
                    );
                    text += "\n\n*Your order accepted.*";

                    currentUser.setCurrentProduct(null);
                    currentUser.setCurrentRound(Round.START);
                    currentUser.setCurrentCategory(null);
                    currentUser.setCurrentQuantity(1);
                    currentUser.setCancelingProduct(null);
                    currentUser.setPayType(null);
                    currentUser.getUser().getCard().clear();
                    currentUser.setTotalProductPrice(0);
                    sendMessage.setText(text);
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    sendNotificationToAdmin(update, currentUser, text);
                    break;
                case SETTINGS:
                case LEAVE_FEEDBACK:
                case MY_ORDERS:
                    // TODO: 1/7/2022
                    sendMessage.setText("Currently not available");
                    bot.execute(sendMessage);
                    break;


            }

        } else if (update.hasCallbackQuery()) {

            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();

            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            String text = update.getCallbackQuery().getMessage().getText();
            switch (currentRound) {
                case INCREMENT:
                    currentUser.setCurrentQuantity(currentUser.getCurrentQuantity() + 1);
                    currentUser.setCurrentRound(Round.GET_SELECTED_PRODUCT);
                    editMessageReplyMarkup.setReplyMarkup((InlineKeyboardMarkup) getCustomerButtons(currentUser));
                    editMessageReplyMarkup.setMessageId(messageId);
                    editMessageReplyMarkup.setChatId(currentUser.getChatId().toString());
                    bot.execute(editMessageReplyMarkup);
                    break;
                case DECREMENT:
                    if (currentUser.getCurrentQuantity() == 1) return;
                    currentUser.setCurrentQuantity(currentUser.getCurrentQuantity() - 1);
                    currentUser.setCurrentRound(Round.GET_SELECTED_PRODUCT);
                    editMessageReplyMarkup.setChatId(currentUser.getChatId().toString());
                    editMessageReplyMarkup.setMessageId(messageId);
                    editMessageReplyMarkup.setReplyMarkup((InlineKeyboardMarkup) getCustomerButtons(currentUser));
                    bot.execute(editMessageReplyMarkup);
                    break;
                case ADD_TO_CARD:
                    Card card = new Card(currentUser.getCurrentProduct(), currentUser.getCurrentQuantity());
                    currentUser.getUser().getCard().add(card);
                    currentUser.setCurrentRound(Round.GET_CATEGORIES);
                    DeleteMessage deleteMessage = new DeleteMessage(currentUser.getChatId().toString(), messageId);
                    bot.execute(deleteMessage);
                    String productName = currentUser.getCurrentProduct().getName();
                    int quantity = currentUser.getCurrentQuantity();
                    sendMessage.setText(quantity + " " + productName + "(s) added to card successfully. Select Category");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    currentUser.setCurrentQuantity(1);
                    break;
                case CANCEL_PRODUCT:
                    Card cancelingProduct = currentUser.getCancelingProduct();
                    currentUser.getUser().getCard().remove(cancelingProduct);
                    currentUser.setCurrentRound(Round.GET_CARD);

                    if (currentUser.getUser().getCard().isEmpty()) {
                        DeleteMessage deleteCard = new DeleteMessage(currentUser.getChatId().toString(), messageId);
                        bot.execute(deleteCard);
                        currentUser.setCurrentRound(Round.GET_CATEGORIES);
                        sendMessage.setText("Select category");
                        sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                        bot.execute(sendMessage);
                    } else {
                        String editText = getCardText(currentUser);
                        editMessageText.setMessageId(messageId);
                        editMessageText.setText(editText);
                        editMessageText.setReplyMarkup((InlineKeyboardMarkup) getCustomerButtons(currentUser));
                        bot.execute(editMessageText);
                    }
                    break;
                case CLEAR_CARD:
                    currentUser.getUser().getCard().clear();
                    DeleteMessage deleteCard = new DeleteMessage(currentUser.getChatId().toString(), messageId);
                    bot.execute(deleteCard);
                    currentUser.setCurrentRound(Round.GET_CATEGORIES);
                    sendMessage.setText("Select category");
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;
                case CONFIRM_ORDER:
                    currentUser.setCurrentRound(Round.SHARE_CONTACT);
                    sendMessage.setText("Click on *Share Contact* button to send your phone number");
                    sendMessage.setParseMode(ParseMode.MARKDOWN);
                    sendMessage.setReplyMarkup(getCustomerButtons(currentUser));
                    bot.execute(sendMessage);
                    break;


            }

        }

    }

    private static void sendNotificationToAdmin(Update update, UserActivity currentUser, String text) {
        Long chatId = update.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId("1085688741");
        Bot bot = new Bot();
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static String getCardText(UserActivity currentUser) {
        double totalProductPrice = 0;
        double deliveryPrice = 9000;
        String text = "In your card: \n";
        for (Card card : currentUser.getUser().getCard()) {
            int quantity = card.getQuantity();
            String name = card.getProduct().getName();
            double price = card.getProduct().getPrice();
            text += quantity + " x " + name + "\n";
            totalProductPrice += price * quantity;
        }
        text += "\nTotal product(s) price: " + totalProductPrice + "\n"
                + "Delivery fee: " + deliveryPrice + "\n"
                + "*Total: *" + (deliveryPrice + totalProductPrice);

        currentUser.setTotalProductPrice(totalProductPrice);
        currentUser.setDeliveryFee(deliveryPrice);
        return text;
    }

}
