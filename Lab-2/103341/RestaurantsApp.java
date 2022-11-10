package mankings.cbd.lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.internal.operation.OrderBy;

public class RestaurantsApp {
    public static void main(String[] args) throws FileNotFoundException {
        MongoCollection<Document> collection = MongoClients.create().getDatabase("cbd").getCollection("restaurants");

        Document doc = new Document("address",
                new Document("building", "0420").append("coord", Arrays.asList(40.63970210386568, -8.653264655559203))
                        .append("rua", "Travessa da Rua Direita")
                        .append("zipcode", "3810"))
                .append("localidade", "Aveiro")
                .append("gastronomia", "Portuguese")
                .append("grades",
                        Arrays.asList(
                                new Document("date", "2022-10-31T01:13:54Z").append("grade", "B").append("score", 8)))
                .append("nome", "Doutores e Engenheiros")
                .append("restaurant_id", "43042096");

        System.out.println();
        System.out.println("\n       --=[ alínea a ]=--\n");

        insert(collection, doc);
        edit(collection, "nome", "Doutores Engenheiros", "Tia Micas");
        find(collection, new Document("localidade", "Aveiro"));

        System.out.println("\n       --=[ alínea b ]=--");

        long startTime, endTime, totalTime;
        System.out.println("\n-> index for \"localidade\"");
        startTime = System.nanoTime();
        find(collection, new Document("localidade", "Manhattan"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time without index: " + totalTime);

        createIndex(collection, "localidade");

        startTime = System.nanoTime();
        find(collection, new Document("localidade", "Manhattan"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time with index: " + totalTime);

        System.out.println("\n-> index for \"gastronomia\"");
        startTime = System.nanoTime();
        find(collection, new Document("gastronomia", "Portuguese"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time without index: " + totalTime);

        createIndex(collection, "gastronomia");

        startTime = System.nanoTime();
        find(collection, new Document("gastronomia", "Portuguese"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time with index " + totalTime);

        System.out.println("\n-> index for \"nome\"");
        startTime = System.nanoTime();
        find(collection, new Document("nome", "restaurant"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time without index: " + totalTime);

        createTextIndex(collection, "nome");

        startTime = System.nanoTime();
        find(collection, new Document("nome", "restaurant"));
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("time with index: " + totalTime);

        System.out.println("       --=[ alínea c ]=--");
        Bson filter, projection, sort, aggregate;

        System.out.println("\n2. Apresente os campos restaurant_id, nome, localidade e gastronomia para todos os documentos da coleção.");
        projection = Projections.include("restaurant_id", "nome", "localidade", "gastronomia");
        collection.find().projection(projection).forEach(r -> System.out.println(r.toJson()));;

        System.out.println("\n4. Indique o total de restaurantes localizados no Bronx.");
        filter = Filters.eq("localidade", "Bronx");
        collection.find(filter).forEach(r -> System.out.println(r.toJson()));
        
        System.out.println("\n7. Encontre os restaurantes que obtiveram uma ou mais pontuações (score) entre [80 e 100].");
        filter = Filters.and(Filters.gt("grades.score", 80), Filters.lte("grades.score", 100));
        collection.find(filter).forEach(r -> System.out.println(r.toJson()));

        System.out.println("\n17. Liste nome, gastronomia e localidade de todos os restaurantes ordenando por ordem crescente da gastronomia e, em segundo, por ordem decrescente de localidade.");
        projection = Projections.include("nome", "gastronomia", "localidade");
        sort = Sorts.orderBy(Sorts.ascending("gastronomia"), Sorts.descending("localidade"));
        collection.find().projection(projection).sort(sort).forEach(r -> System.out.println(r.toJson()));;

        System.out.println("\n19. Conte o total de restaurante existentes em cada localidade.");
        aggregate = Aggregates.group("$localidade", Accumulators.sum("count", 1));
        collection.aggregate(Arrays.asList(aggregate)).forEach(r -> System.out.println(r.toJson()));


        System.out.println("\n       --=[ alínea d ]=--");

        PrintWriter filewriter = new PrintWriter(new File("CBD_L204_103341.txt"));

        int locations = countLocalidades(collection);
        filewriter.println("Número de localidades distintas:\n -> " + locations);

        Map<String, Integer> map = countRestByLocalidade(collection);
        filewriter.println("\nNúmero de restaurantes por localidade:");
        Set<String> keys = map.keySet();
        for (String k : keys) {
            filewriter.println(" -> " + k + " - " + map.get(k));
        }

        List<String> lst = getRestWithNameCloserTo(collection, "Park");
        filewriter.println("\nNome de restaurantes contendo 'Park' no nome:");
        for (String res : lst) {
            filewriter.println(" -> " + res);
        }

        filewriter.close();
    }

    //
    // alínea a
    //
    public static void insert(MongoCollection<Document> col, Document doc) {
        col.insertOne(doc);
    }

    public static void edit(MongoCollection<Document> collection, String param, String oldvalue, String newvalue) {
        collection.updateOne(Filters.eq(param, oldvalue), new Document("$set", new Document(param, newvalue)));
    }

    public static void find(MongoCollection<Document> col, Document doc) {
        col.find(doc);
    }

    //
    // alínea b
    //
    public static void createIndex(MongoCollection<Document> col, String str) {
        col.createIndex(Indexes.ascending(str));
        System.out.println("-> created index for \"" + str + "\"");
    }

    public static void createTextIndex(MongoCollection<Document> col, String str) {
        col.createIndex(Indexes.text(str));
        System.out.println("-> created text index for \"" + str + "\"");
    }

    //
    // alínea d
    //
    public static int countLocalidades(MongoCollection<Document> col) {
        int count = 0;
        for(String s : col.distinct("localidade", String.class))
            count++;

        return count;
    }

    public static Map<String, Integer> countRestByLocalidade(MongoCollection<Document> col) {
        HashMap<String, Integer> countmap = new HashMap<>();

        Bson aggregate = Aggregates.group("$localidade", Accumulators.sum("count", 1));
        for(Document d : col.aggregate(Arrays.asList(aggregate))) {
            String l = d.getString("_id");
            int c = d.getInteger("count");
            countmap.put(l, c);
        }

        return countmap;
    }

    public static List<String> getRestWithNameCloserTo(MongoCollection<Document> col, String name) {
        ArrayList<String> lst = new ArrayList<>();
        Bson search = Filters.text(name);

        for (Document d : col.find(search)) {
            lst.add(d.getString("nome"));
        }

        return lst;
    }
}
