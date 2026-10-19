import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("       REGISTRATION SYSTEM");
        System.out.println("=================================");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your South African cellphone number: ");
        String cellPhoneNumber = scanner.nextLine();

        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        if (user.checkUserName()) {
            System.out.println("Username successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted; "
                    + "please ensure that your username contains "
                    + "an underscore and is no more than five "
                    + "characters in length.");
        }

        if (user.checkPasswordComplexity()) {
            System.out.println("Password successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted; "
                    + "please ensure that the password contains "
                    + "at least eight characters, a capital letter, "
                    + "a number, and a special character.");
        }

        if (user.checkCellPhoneNumber()) {
            System.out.println("Cell phone number successfully added.");
        } else {
            System.out.println("Cell number is incorrectly formatted "
                    + "or does not contain an international code; "
                    + "please correct the number and try again.");
        }

        String registrationMessage = user.registerUser();

        System.out.println();
        System.out.println(registrationMessage);

        if (registrationMessage.equals("User registered successfully.")) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("            LOGIN");
            System.out.println("=================================");

            boolean loginSuccessful = false;
            int attempts = 0;

            while (!loginSuccessful && attempts < 3) {

                System.out.print("Enter your username: ");
                String enteredUsername = scanner.nextLine();

                System.out.print("Enter your password: ");
                String enteredPassword = scanner.nextLine();

                loginSuccessful =
                        user.loginUser(enteredUsername, enteredPassword);
                attempts++;

                System.out.println();
                System.out.println(
                        user.returnLoginStatus(loginSuccessful)
                );
            }

            if (loginSuccessful) {
                runQuickChat(scanner);
            } else {
                System.out.println("Too many failed login attempts. Goodbye.");
            }
        }

        scanner.close();
    }

    private static void runQuickChat(Scanner scanner) {

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int limit = askMessageLimit(scanner);
        int entered = 0;
        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("Choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (entered >= limit) {
                        System.out.println("You have already entered all "
                                + limit + " messages.");
                    } else {
                        if (enterMessage(scanner)) {
                            entered++;
                        }
                        if (entered >= limit) {
                            System.out.println();
                            System.out.println("You have entered all "
                                    + limit + " messages.");
                            System.out.println("Total messages sent: "
                                    + Message.returnTotalMessages());
                        }
                    }
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    System.out.println("Total messages sent: "
                            + Message.returnTotalMessages());
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        }
    }

    private static int askMessageLimit(Scanner scanner) {
        while (true) {
            System.out.print("How many messages do you wish to enter? ");
            try {
                int limit = Integer.parseInt(scanner.nextLine().trim());
                if (limit > 0) {
                    return limit;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    private static boolean enterMessage(Scanner scanner) {

        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            recipient = scanner.nextLine().trim();
            if (Message.isValidRecipient(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                break;
            }
            System.out.println("Cell phone number is incorrectly formatted or "
                    + "does not contain an international code. Please "
                    + "correct the number and try again.");
        }

        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = scanner.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("The message cannot be empty.");
            } else if (text.length() > Message.MAX_MESSAGE_LENGTH) {
                System.out.println(Message.checkMessageLength(text));
                System.out.println("Please enter a message of less than 250 "
                        + "characters.");
            } else {
                System.out.println(Message.checkMessageLength(text));
                break;
            }
        }

        Message message = new Message(recipient, text);
        System.out.println(message.messageIDStatement());

        String option;
        while (true) {
            System.out.println();
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Enter your choice: ");
            option = scanner.nextLine().trim();
            if (option.equals("1") || option.equals("2") || option.equals("3")) {
                break;
            }
            System.out.println("Invalid option. Please choose 1, 2 or 3.");
        }

        System.out.println(message.SentMessage(option));

        if (option.equals("1")) {
            System.out.println();
            System.out.println(message.getMessageDetails());
            return true;
        }

        if (option.equals("2")) {
            String answer = scanner.nextLine().trim();
            if (answer.equals("0")) {
                System.out.println("Message deleted.");
            } else {
                System.out.println("Message disregarded.");
            }
            return false;
        }

        return true; // stored
    }
}