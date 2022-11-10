package mankings.cbd.lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;

public class PokémonApp {
    public static void main(String[] args) throws FileNotFoundException {
        MongoCollection<Document> collection = MongoClients.create().getDatabase("cbd").getCollection("pokemon");

        PrintWriter filewriter = new PrintWriter(new File("CBD_L205_103341.txt"));

        Bson filter, projection, sort, aggregate;

        filewriter.println("     --=[ alínea c) ]=--");

        filewriter.println("\nQuery 1 - Get name and catch rate of all legendary Pokémon.");
        filter = eq("is_legendary", 1);
        projection = fields(include("name", "capture_rate"), excludeId());
        collection.find(filter).projection(projection).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println(
                "\nQuery 2 - Get name and pokédex number of all Pokémon that have a male rate of at least 70%.");
        filter = gte("percentage_male", 70);
        projection = fields(include("name", "pokedex_number"), excludeId());
        collection.find(filter).projection(projection).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 3 - Get name, pokédex number and type of all Pokémon without a secondary type.");
        filter = eq("type2", "");
        projection = fields(include("name", "pokedex_number", "type1"), excludeId());
        collection.find(filter).projection(projection).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 4 - Get the name and height of the 10 heaviest Pokémon that are not legendaries.");
        filter = eq("is_legendary", 0);
        projection = fields(include("name", "height_m", "weight_kg"), excludeId());
        sort = descending("weight_kg");
        collection.find(filter).projection(projection).sort(sort).limit(10)
                .forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 5 - Get the 5 steel type Pokémon with the highest defense.");
        filter = or(eq("type1", "steel"), eq("type2", "steel"));
        projection = fields(include("name", "defense"), excludeId());
        sort = ascending("defense");
        collection.find(filter).projection(projection).sort(sort).limit(5).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 6 - Get all the Pokémon that resist or are immune to ghost type attacks.");
        filter = lt("against_ghost", 1);
        projection = fields(include("name", "against_ghost", "type1", "type2"), excludeId());
        collection.find(filter).projection(projection).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\n     --=[ alínea d) ]=--");

        filewriter
                .println("\nQuery 1 - Get all types ordered by how many legendaries have that type as their primary.");
        collection.aggregate(Arrays.asList(
                match(eq("is_legendary", 1)),
                group("$type1", sum("legendcount", 1)),
                project(fields(include("type1", "legendcount"))),
                sort(descending("legendcount")))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 2 - Get the 3 hardest types to catch by average.");
        collection.aggregate(Arrays.asList(
                group("$type1", avg("avg_catch_rate", "$capture_rate")),
                project(fields(eq("type", "$type1"), include("avg_catch_rate"))),
                sort(descending("avg_catch_rate")),
                limit(3))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 3 - Get how many fighting type Pokémon are in each generation.");
        collection.aggregate(Arrays.asList(
                match(or(eq("type1", "fighting"), eq("type2", "fighting"))),
                group("$generation", sum("count", 1)),
                project(fields(include("generation", "count"))),
                sort(ascending("_id")))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 4 - Get the average stat total of each generation.");
        collection.aggregate(Arrays.asList(
                group("$generation", avg("avg_base_total", "$base_total")),
                project(include("avg_base_total")),
                sort(ascending("_id")))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 5 - Get the average stat total and how many Pokémon there are per base egg steps required to hatch it's egg.");
        collection.aggregate(Arrays.asList(
                group("$base_egg_steps", avg("avg_base_total", "$base_total"), sum("pkmncount", 1)),
                project(include("avg_base_total", "pkmncount")),
                sort(ascending("_id")))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.println("\nQuery 6 - Get how many legendaries there are per experience growth tier.");
        collection.aggregate(Arrays.asList(
                match(eq("is_legendary", 1)),
                group("$experience_growth", sum("legendcount", 1)),
                project(include("legendcount")),
                sort(ascending("_id")))).forEach(p -> filewriter.println(p.toJson()));

        filewriter.close();

    }
}
