import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
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

    private String messageID;
    private int messageNumber;
    private String recipient;
    @SerializedName("message")
    private String messageText;
    private String messageHash;
    private static int totalMessagesSent = 0;

private static final List<Message> sentMessages = new ArrayList<>();
private static final List<Message> disregardedMessages = new ArrayList<>();
private static final List<Message> storedMessages = new ArrayList<>();
private static final List<String> messageHashes = new ArrayList<>();
private static final List<String> messageIDs = new ArrayList<>();

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

    messageIDs.add(messageID);
    messageHashes.add(messageHash);
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
            sentMessages.add(this);
            totalMessagesSent++;
            return "Message successfully sent.";

        case "2":
            disregardedMessages.add(this);
            return "Press 0 to delete the message.";

        case "3":
            if (storeMessage()) {
                storedMessages.add(this);
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

    List<String> details = new ArrayList<>();

    for (Message message : sentMessages) {
        details.add(message.getMessageDetails());
    }

    return String.join("\n\n", details);
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
    disregardedMessages.clear();
    storedMessages.clear();
    messageHashes.clear();
    messageIDs.clear();
}

    static void setStorageFile(String path) {
        storageFile = path;
    }
    
    public static String displayStoredSendersAndRecipients() {
    loadStoredMessages();

    if (storedMessages.isEmpty()) {
        return "No stored messages available.";
    }

    StringBuilder result = new StringBuilder();

    for (Message message : storedMessages) {
        result.append("Sender: QuickChat User\n");
        result.append("Recipient: ")
                .append(message.recipient)
                .append("\n\n");
    }

    return result.toString();
}
    
    public static String displayLongestStoredMessage() {
    loadStoredMessages();

    if (storedMessages.isEmpty()) {
        return "No stored messages available.";
    }

    Message longest = storedMessages.get(0);

    for (Message message : storedMessages) {
        if (message.messageText.length() > longest.messageText.length()) {
            longest = message;
        }
    }

    return "Longest stored message:\n" + longest.getMessageDetails();
}
    
    public static String searchStoredMessageByID(String searchID) {
    loadStoredMessages();

    for (Message message : storedMessages) {
        if (message.messageID.equals(searchID)) {
            return "Message found!\n"
                    + "Recipient: " + message.recipient
                    + "\nMessage: " + message.messageText;
        }
    }

    return "Message ID not found.";
}
    
    public static String searchStoredMessagesByRecipient(String searchRecipient) {
    loadStoredMessages();

    StringBuilder result = new StringBuilder();
    boolean found = false;

    for (Message message : storedMessages) {
        if (message.recipient.equals(searchRecipient)) {
            result.append(message.getMessageDetails())
                    .append("\n\n");
            found = true;
        }
    }

    if (!found) {
        return "No stored messages found for this recipient.";
    }

    return result.toString();
}
    
    public static String deleteStoredMessageByHash(String searchHash) {
    loadStoredMessages();

    boolean removed = storedMessages.removeIf(
            message -> message.messageHash.equals(searchHash)
    );

    if (!removed) {
        return "Message hash not found.";
    }

    try {
        Gson gson = new Gson();
        String json = gson.toJson(storedMessages);
        Files.writeString(
                Paths.get(storageFile),
                json,
                StandardCharsets.UTF_8
        );

        return "Stored message successfully deleted.";

    } catch (IOException e) {
        return "Error deleting message: " + e.getMessage();
    }
}
 
    public static String displayStoredMessageReport() {
    loadStoredMessages();

    if (storedMessages.isEmpty()) {
        return "No stored messages available.";
    }

    StringBuilder report = new StringBuilder();

    report.append("========== STORED MESSAGE REPORT ==========\n\n");

    for (Message message : storedMessages) {
        report.append("Message ID: ")
                .append(message.messageID)
                .append("\n");

        report.append("Message Hash: ")
                .append(message.messageHash)
                .append("\n");

        report.append("Recipient: ")
                .append(message.recipient)
                .append("\n");

        report.append("Message: ")
                .append(message.messageText)
                .append("\n");

        report.append("-------------------------------------------\n");
    }

    return report.toString();
}
public static void loadStoredMessages() {
    Path path = Paths.get(storageFile);

    if (!Files.exists(path)) {
        return;
    }

    try {
        String json = Files.readString(path, StandardCharsets.UTF_8);

        if (json.isBlank()) {
            return;
        }

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(json, Message[].class);

        storedMessages.clear();

        if (messages != null) {
            for (Message message : messages) {
                storedMessages.add(message);

                if (!messageIDs.contains(message.messageID)) {
                    messageIDs.add(message.messageID);
                }

                if (!messageHashes.contains(message.messageHash)) {
                    messageHashes.add(message.messageHash);
                }
            }
        }

    } catch (IOException e) {
        System.out.println("Could not load stored messages: " + e.getMessage());
    }
}
}
