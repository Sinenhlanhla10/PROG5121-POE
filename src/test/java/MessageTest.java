import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private static final String MIKE_NUMBER = "+27718693002";
    private static final String MIKE_TEXT =
            "Hi Mike, can you join us for dinner tonight?";
    private static final String KEEGAN_NUMBER = "08575975889";
    private static final String KEEGAN_TEXT =
            "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Message.resetCounters();
        Message.setStorageFile(tempDir.resolve("messages.json").toString());
    }

    private Message mike() {
        return new Message("0012345678", 0, MIKE_NUMBER, MIKE_TEXT);
    }

    private Message keegan() {
        return new Message("0098765432", 1, KEEGAN_NUMBER, KEEGAN_TEXT);
    }

    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.",
                Message.checkMessageLength(MIKE_TEXT));
    }

    @Test
    void messageLengthFailure() {
        String tooLong = "a".repeat(255);
        assertEquals("Message exceeds 250 characters by 5; "
                + "please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    @Test
    void recipientNumberSuccess() {
        assertEquals("Cell phone number successfully captured.",
                mike().checkRecipientCell());
    }

    @Test
    void recipientNumberFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does "
                + "not contain an international code. Please correct the "
                + "number and try again.",
                keegan().checkRecipientCell());
    }

    @Test
    void messageHashTestCase1() {
        assertEquals("00:0:HITONIGHT", mike().createMessageHash());
    }

    @Test
    void messageHashesInLoop() {
        String[] ids = {"0012345678", "0098765432", "1234567890"};
        int[] numbers = {0, 1, 2};
        String[] texts = {MIKE_TEXT, KEEGAN_TEXT, "Okay, see you soon!"};
        String[] expected = {
                "00:0:HITONIGHT", "00:1:HIPAYMENT", "12:2:OKAYSOON"};

        for (int i = 0; i < ids.length; i++) {
            Message m = new Message(ids[i], numbers[i], MIKE_NUMBER, texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    @Test
    void messageIDIsCreated() {
        Message m = mike();
        assertEquals("Message ID generated: 0012345678",
                m.messageIDStatement());
        assertTrue(m.checkMessageID());
    }

    @Test
    void randomMessageIDIsTenDigits() {
        Message m = new Message(MIKE_NUMBER, MIKE_TEXT);
        assertEquals(10, m.getMessageID().length());
        assertTrue(m.getMessageID().matches("\\d{10}"));
    }

    @Test
    void messageSentSend() {
        assertEquals("Message successfully sent.", mike().SentMessage("1"));
    }

    @Test
    void messageSentDisregard() {
        assertEquals("Press 0 to delete the message.",
                keegan().SentMessage("2"));
    }

    @Test
    void messageSentStore() {
        assertEquals("Message successfully stored.", mike().SentMessage("3"));
    }

    @Test
    void totalMessagesSentCountsOnlySentMessages() {
        mike().SentMessage("1");      // sent
        keegan().SentMessage("2");    // disregarded
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesReturnsSentMessages() {
        mike().SentMessage("1");
        String all = Message.printMessages();
        assertTrue(all.contains("Message ID: 0012345678"));
        assertTrue(all.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(all.contains(MIKE_NUMBER));
        assertTrue(all.contains(MIKE_TEXT));
    }

    @Test
    void storedMessagesAreValidJsonArray() throws IOException {
        mike().SentMessage("3");
        keegan().SentMessage("3");

        String json = new String(
                Files.readAllBytes(tempDir.resolve("messages.json")),
                StandardCharsets.UTF_8).trim();

        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageID\": \"0098765432\""));
    }
}