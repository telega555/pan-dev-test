package com.pandev;

import com.pandev.repository.RawDataRepository;
import com.pandev.service.CategoryService;
import com.pandev.service.ProducerService;
import com.pandev.service.impl.MainServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainServiceImplTest {

    @Mock
    private RawDataRepository rawDataRepository;
    @Mock
    private ProducerService producerService;

    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private MainServiceImpl mainService;

    private static final String VIEW_TREE_COMMAND = "/viewTree";
    private static final String ADD_ELEMENT_COMMAND = "/addElement";
    private static final String REMOVE_ELEMENT_COMMAND = "/removeElement";
    private static final String MOCKED_VIEW_TREE_RESULT = "Mocked View Tree Result";
    private static final String MOCKED_ADD_ELEMENT_RESULT = "Mocked Add Element Result";
    private static final String MOCKED_REMOVE_ELEMENT_RESULT = "Mocked Remove Element Result";
    private static final String TEST_OUTPUT = "Test Output";
    private static final Long CHAT_ID = 123L;

    @Test
    void testProcessTextMessage() {
        // given
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(CHAT_ID);

        // when
        mainService.processTextMessage(update);

        // then
        verify(rawDataRepository, times(1)).save(any());
        verify(producerService, times(1)).producerAnswer(any());
    }

    @Test
    void testProcessServiceCommand() {
        // given
        String command = VIEW_TREE_COMMAND;
        when(categoryService.viewTreeCategory()).thenReturn(MOCKED_VIEW_TREE_RESULT);

        // when
        String result = mainService.processServiceCommand(command);

        // then
        assertEquals(MOCKED_VIEW_TREE_RESULT, result);
    }

    @Test
    void testProcessAddElementCommand() {
        // given
        String[] commandParts = {ADD_ELEMENT_COMMAND, "CategoryName"};
        when(categoryService.addCategory("CategoryName", null)).thenReturn(MOCKED_ADD_ELEMENT_RESULT);

        // when
        String result = mainService.processAddElementCommand(commandParts);

        // then
        assertEquals(MOCKED_ADD_ELEMENT_RESULT, result);
    }

    @Test
    void testProcessRemoveElementCommand() {
        // given
        String[] commandParts = {REMOVE_ELEMENT_COMMAND, "CategoryName"};
        when(categoryService.removeCategoryByName("CategoryName")).thenReturn(MOCKED_REMOVE_ELEMENT_RESULT);

        // when
        String result = mainService.processRemoveElementCommand(commandParts);

        // then
        assertEquals(MOCKED_REMOVE_ELEMENT_RESULT, result);
    }

    @Test
    void testSendAnswer() {
        // given
        String output = TEST_OUTPUT;
        Long chatId = CHAT_ID;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);

        // when
        mainService.sendAnswer(output, chatId);

        // then
        verify(producerService, times(1)).producerAnswer(eq(sendMessage));
    }

    @Test
    void testSaveRawData() {
        // given
        Update update = mock(Update.class);

        // when
        mainService.saveRawData(update);

        // then
        verify(rawDataRepository, times(1)).save(any());
    }

}
