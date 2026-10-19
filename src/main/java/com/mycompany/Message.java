import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    private static int totalMessagesSent = 0;
    private static final List<String> sentMessages = new ArrayList<>();
    private static String storageFile = "messages.json";

    public Message(String recipient, String messageText) {
        this(generateMessageID(), totalMessagesSent, recipient, messageText);
    }

    Message(String messageID, int messageNumber,
            String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    private static String generateMessageID() {
        long number = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", number);
    }

    public String getMessageID() {
        return messageID;
    }

    public String messageIDStatement() {
        return "Message ID generated: " + messageID;
    }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    public static boolean isValidRecipient(String number) {
        return number != null && number.matches("\\+27\\d{9}");
    }

    public String checkRecipientCell() {
        if (isValidRecipient(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the "
                + "number and try again.";
    }

    public static String checkMessageLength(String text) {
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int extra = text.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + extra
                + "; please reduce the size.";
    }

    public String createMessageHash() {
        String firstTwoDigits = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1]
                .replaceAll("[^a-zA-Z0-9]", "");

        return (firstTwoDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    public String SentMessage(String choice) {
        switch (choice) {
            case "1":
                sentMessages.add(getMessageDetails());
                totalMessagesSent++;
                return "Message successfully sent.";
            case "2":
                return "Press 0 to delete the message.";
            case "3":
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    public String getMessageDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        return String.join("\n\n", sentMessages);
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public boolean storeMessage() {
        String entry = "  {\n"
                + "    \"messageID\": \"" + messageID + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\"\n"
                + "  }";
        try {
            Path path = Paths.get(storageFile);
            String existing = Files.exists(path)
                    ? new String(Files.readAllBytes(path),
                            StandardCharsets.UTF_8).trim()
                    : "";
            int end = existing.lastIndexOf(']');
            String content;
            if (existing.isEmpty() || existing.equals("[]") || end < 0) {
                content = "[\n" + entry + "\n]\n";
            } else {
                content = existing.substring(0, end).trim()
                        + ",\n" + entry + "\n]\n";
            }
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
            return false;
        }
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    static void resetCounters() {
        totalMessagesSent = 0;
        sentMessages.clear();
    }

    static void setStorageFile(String path) {
        storageFile = path;
    }
}