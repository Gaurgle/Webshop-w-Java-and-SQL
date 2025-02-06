import java.util.Scanner;

public class UserSession {
    private static int customerId = -1;

    // login or create user.
    public static int authenticateUser() {
        Scanner scanner = new Scanner(System.in);

        while (customerId == -1) {                                              // user ID is always > 1 in DB.
            System.out.println("Do you have an account? (Y/N)");
            String response = scanner.next().trim().toLowerCase();

            if (response.equals("y")) {
                System.out.println("Enter username or email");
                String login = scanner.next();
                System.out.println("Enter password");
                String password = scanner.next();

                customerId = UserDAO.authenticateUser(login, password);
                String userFirstName = UserDAO.getUserFirstName();

                if (customerId == -1) {
                    System.out.println("Invalid username or password");
                } else {
                    System.out.println("\nWelcome " + userFirstName + "!");     // greet user
                    System.out.println("Shopping for shoes?\n");
                }

            } else if (response.equals("n")) {
                while (true) {
                    System.out.println("Would you like to create an account? (Y/N)");
                    String responseCreateAccount = scanner.next().trim().toLowerCase();

                    if (responseCreateAccount.equals("n")) {                    //early return
                        System.out.println("Exiting site. Good bye!");
                        System.exit(0);
                    } else if (responseCreateAccount.equals("y")) {
                        customerId = registerUser(scanner);
                        break;
                    } else {
                        System.out.println("Invalid choice");
                    }
                }
            } else {
                System.out.println("Invalid choice");
            }
        }
        return customerId;
    }

    // create user
    private static int registerUser(Scanner scanner) {

        System.out.println("Registering new account.");
        System.out.println("First name: ");
        String firstName = scanner.next();
        System.out.println("Last name: ");
        String lastName = scanner.next();

        String username;
        while (true) {
            System.out.println("Username: ");
            username = scanner.next();

            int userNameTaken = UserDAO.checkUserName(username);
            if (userNameTaken > 0) {
                System.out.println("Username is taken, try another one");
            } else {
                break;
            }
        }

        // enter info
        System.out.println("Pnr: ");
        String pnr = scanner.next();
        scanner.nextLine();
        System.out.println("Address: ");
        String address = scanner.nextLine();
        System.out.println("City: ");
        String city = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.next();
        System.out.println("Phone: ");
        String phone = scanner.next();
        System.out.println("Password: ");
        scanner.nextLine();
        String password = scanner.next();

        String creditcard;
        while (true) {
            System.out.println("Add credit card information?: (Y/N)");
            String response = scanner.next().trim().toLowerCase();
            if (response.equals("y")) {
                System.out.println("Enter credit card number, 16 digits: ");

                creditcard = scanner.next();
                try {
                    creditcard = formatCreditCard(creditcard);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid credit card");
                }
            } else if (response.equals("n")) {
                creditcard = null;
                break;
            }
        }

        System.out.println("encryptionKey: (keep this safe)");
        String encryptionKey = scanner.next();

        int customerId = UserDAO.registerUser(firstName, lastName, username, pnr, address, city, email, phone, password, creditcard, encryptionKey);

        if (customerId != -1) {
            System.out.println("Account created");
            return customerId;
        } else {
            System.out.println("Error when creating account");
            return -1;
        }
    }

    // format credit card number (xxxx-xxxx-xxxx-xxxx)
    public static String formatCreditCard(String creditcard) {
        String digitsOnly = creditcard.replaceAll("\\D", ""); // changes every non digit char to ""

        if (digitsOnly.length() != 16) {
            throw new IllegalArgumentException("Invalid card number, must be 16 digits");
        }
        return digitsOnly.replaceAll("(\\d{4})(?=\\d)", "$1-"); // 
    }
}
