package mankings.cbd.lab1;

import redis.clients.jedis.Jedis;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutocompleteA {
    public static String NAMES_KEY = "names";
    public static String FILE_PATH = "../names.txt";

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushDB();
        Scanner inputScanner = new Scanner(System.in);
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(new File(FILE_PATH));
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException. Exiting.");
            System.exit(1);
        }

        while(fileScanner.hasNextLine()) {
            String s = fileScanner.nextLine();
            jedis.sadd(NAMES_KEY, s);
        }

        fileScanner.close();

        while(true) {
            System.out.println();
            System.out.print("Search for ('Enter' for quit): ");
            String input = inputScanner.nextLine();
            if(input.equals("")) break;

            Set<String> names = jedis.smembers(NAMES_KEY);
            List<String> matches = new ArrayList<>();
            for(String name : names) 
                if(name.toLowerCase().startsWith(input.toLowerCase())) matches.add(name);

            Collections.sort(matches);
            for(String name : matches)
                System.out.println(name);

            System.out.println();
        }

        inputScanner.close();
        jedis.close();
    }
}