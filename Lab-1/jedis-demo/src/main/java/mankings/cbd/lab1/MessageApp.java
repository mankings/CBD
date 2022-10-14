package mankings.cbd.lab1;

import java.util.Scanner;
import redis.clients.jedis.Jedis;

public class MessageApp {
    public static boolean escape = false;
    private static Jedis jedis;
    private static String loggedUser = "";

    // keys usadas
    private static String user_key = "users"; // "users" : [set dos users]
    private static String followers_key = ""; // "seguidores:<username>" : [set de seguidores]
    private static String messages_key = ""; // "mensagens:<username>" : [lista de mensagens]

    public static void main(String[] args) {
        jedis = new Jedis();
        jedis.flushDB();
        Scanner sc = new Scanner(System.in);

        System.out.print("Welcome! Please state your username: ");
        loggedUser = sc.next();
        jedis.sadd(user_key, loggedUser);

        setKeys(loggedUser);

        while (!escape) {
            escape = menu();
        }

        sc.close();
        jedis.close();
    }

    private static void setKeys(String user) {
        followers_key = "followers:" + user;
        messages_key = "messages:" + user;
    }

    private static boolean menu() {
        Scanner sc = new Scanner(System.in);

        String msg = "\nLogged in as: " + loggedUser +
                "\n 1 - Follow a user" +
                "\n 2 - Send message" +
                "\n 3 - Read messages" +
                "\n 4 - Unfollow a user" +
                "\n 5 - Check users you're following" +
                "\n 6 - Logout" +
                "\nChoose an option: ";

        System.out.print(msg);

        //try {
            int input = sc.nextInt();

            switch (input) {
                case 1:
                    option1();
                    break;

                case 2:
                    option2();
                    break;

                case 3:
                    option3();
                    break;

                case 4:
                    option4();
                    break;

                case 5:
                    option5();
                    break;

                case 6:
                    sc.close();             
                    return true;

                default:
                    System.out.println("ERROR: Invalid option!");
            }

            sc.close();
            return false;
        /*} catch (Exception e) {
            System.out.println("ERROR: Only numbers allowed!");
            return false;
        }*/

    }

    private static void option1() {
        Scanner scan = new Scanner(System.in);
        System.out.print("User to follow: ");
        String u = scan.nextLine();

        if (jedis.smembers(user_key).contains(u)) {
            jedis.sadd(followers_key, u);
        } else {
            System.out.println("ERROR: User given doesn't exist.");
        }

        scan.close();
    }

    private static void option2() {
        Scanner scan = new Scanner(System.in);

        System.out.print("Write your message: ");
        String message = scan.nextLine();

        jedis.lpush(messages_key, message);
        System.out.println("INFO: Messsage published successfully!");

        scan.close();
    }

    private static void option3() {
        for (String user : jedis.smembers(followers_key))
            for (String message : jedis.lrange("messages:" + user, 0, -1))
                System.out.println("[" + user + "] > " + message);
    }

    private static void option4() {
        Scanner scan = new Scanner(System.in);

        System.out.print("User to unfollow: ");
        String username = scan.nextLine();

        if (jedis.smembers(user_key).contains(username)) {
            jedis.srem(followers_key, username);

        } else {
            System.out.println("ERROR: User given doesn't exist.");
        }

        scan.close();
    }

    private static void option5() {
        for (String u : jedis.smembers(followers_key))
            System.out.println(u);

        if (jedis.smembers(user_key).isEmpty())
            System.out.println("[INFO]: List is empty");
    }
}
