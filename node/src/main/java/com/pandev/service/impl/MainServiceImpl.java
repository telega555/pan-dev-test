package com.pandev.service.impl;

import com.pandev.entity.CategoryEntity;
import com.pandev.entity.RawData;
import com.pandev.repository.RawDataRepository;
import com.pandev.service.CategoryService;
import com.pandev.service.MainService;
import com.pandev.service.ProducerService;
import com.pandev.service.enums.ServiceCommand;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static com.pandev.service.enums.ServiceCommand.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    public static final String EMPTY_OUTPUT = "";
    public static final String CANCELLED_COMMAND = "Команда отменена!";
    public static final String STARTED_COMMAND = "Приветствую! Чтобы посмотреть список доступных команд введите /help";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
    public static final String RETRY_COMMAND = "Неизвестная ошибка! Введите /start и попробуйте снова!";
    public static final String INVALID_ADD_ELEMENT_FORMAT = "Не правильный формат команды. Используйте: /addElement <родительский_элемент> <дочерний_элемент>";
    public static final String UNABLE_CREATE_PARENT_CATEGORY = "Не возможно создать родительскую категорию: ";
    public static final String INVALID_REMOVE_ELEMENT_FORMAT = "Не правильный формат команды. Используйте: /removeElement <название_категорий>";

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final CategoryService categoryService;

    @Transactional
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var text = update.getMessage().getText();
        var output = EMPTY_OUTPUT;

        var serviceCommand = ServiceCommand.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            output = CANCELLED_COMMAND;
        } else if (HELP.equals(serviceCommand)) {
            output = showHelp();
        } else if (START.equals(serviceCommand)) {
            output = STARTED_COMMAND;
        } else {
            output = processServiceCommand(text);
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    public String processServiceCommand(String command) {
        String[] commandParts = command.split(" ");
        var serviceCommand = ServiceCommand.fromValue(commandParts[0]);

        if (serviceCommand == null) {
            return UNKNOWN_COMMAND;
        }

        return switch (serviceCommand) {
            case START -> STARTED_COMMAND;
            case VIEW_TREE -> categoryService.viewTreeCategory();
            case ADD_ELEMENT -> processAddElementCommand(commandParts);
            case REMOVE_ELEMENT -> processRemoveElementCommand(commandParts);
            default -> UNKNOWN_COMMAND;
        };
    }

    public String processAddElementCommand(String[] commandParts) {
        if (commandParts.length < 2 || commandParts[1].trim().isEmpty()) {
            return INVALID_ADD_ELEMENT_FORMAT;
        }

        if (commandParts.length > 3) {
            return INVALID_ADD_ELEMENT_FORMAT;
        }

        String parentName = commandParts[1].replace("_", " ");

        if (commandParts.length == 3) {
            String childName = commandParts[2].replace("_", " ");
            Optional<CategoryEntity> parentCategoryOpt = categoryService.findByName(parentName);

            Long parentId;

            if (parentCategoryOpt.isEmpty()) {
                categoryService.addCategory(parentName, null);
                parentCategoryOpt = categoryService.findByName(parentName);
                if (parentCategoryOpt.isEmpty()) {
                    return UNABLE_CREATE_PARENT_CATEGORY + parentName;
                }
            }

            parentId = parentCategoryOpt.get().getId();

            return categoryService.addCategory(childName, parentId);
        } else {
            return categoryService.addCategory(parentName, null);
        }
    }

    public String processRemoveElementCommand(String[] commandParts) {
        if (commandParts.length != 2 || commandParts[1].trim().isEmpty()) {
            return INVALID_REMOVE_ELEMENT_FORMAT;
        }

        String categoryName = commandParts[1].replace("_", " ");
        return categoryService.removeCategoryByName(categoryName);
    }

    public void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    public void saveRawData(Update update) {
        var rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }

    private String showHelp() {
        return "Список доступных команд:\n"
                + "/start - выполнить команды по категориям;\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/viewTree - Отображение категории в деревовидном виде;\n"
                + "/addElement <название_категорий> - Добавление категорий, если у него нет родительской категорий, " +
                "то категория будет являться корневым;\n"
                + "/addElement <родительская_категория> <дочерная_категория> - Добавление дочерной категорий к родительской;\n"
                + "/removeElement <название_категорий> - удаление категорий;\n"
                + "/help - вывод всех доступных команд.";
    }
}
