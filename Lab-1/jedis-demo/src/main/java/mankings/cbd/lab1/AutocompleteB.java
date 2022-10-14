package mankings.cbd.lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

public class AutocompleteB {
    public static String NAMES_KEY = "names_pt";
    public static String FILE_PATH = "../nomes-pt-2021.csv";

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

        HashMap<String, String> map = new HashMap<>();
        while (fileScanner.hasNextLine()) {
            String s = fileScanner.nextLine();
            String name = s.split(";")[0];
            String entries = s.split(";")[1];
            map.put(name, entries);
        }
        jedis.hmset(NAMES_KEY, map);

        fileScanner.close();

        while (true) {
            System.out.print("Search for ('Enter' for quit): ");
            String input = inputScanner.nextLine();
            if (input.equals(""))
                break;

            Map<String, String> names = jedis.hgetAll(NAMES_KEY);
            names = sortByValue(names);

            for (Entry<String, String> entry : names.entrySet()) {
                String name = entry.getKey();
                String entries = entry.getValue();

                if (name.toLowerCase().startsWith(input.toLowerCase()))
                    System.out.printf("%-20s | %s%n", name, entries);
            }

            System.out.println();
        }

        inputScanner.close();
        jedis.close();
    }

    private static Map<String, String> sortByValue(Map<String, String> map) {

        List<Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {

            @Override
            public int compare(Entry<String, String> arg0, Entry<String, String> arg1) {
                Integer i1 = Integer.parseInt(arg0.getValue());
                Integer i2 = Integer.parseInt(arg1.getValue());

                if (i1 < i2)
                    return 1;

                else if (i1 > i2)
                    return -1;

                return 0;
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return sortedMap;
    }
}