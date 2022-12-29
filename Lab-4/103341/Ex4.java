package com.mankings.cbd;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;


// dataset comes from: https://github.com/IgorRozani/pokemon-graph/blob/master/pokemon.cypher
public class Ex4 {
    private static final String SEEDDATA = "pokemon.cypher";
    private static final String OUTPUTFILE = "CBD_L44C_output.txt";

    public static void main( String[] args ) throws IOException {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1234"));

        Map<String, String> queries = new HashMap<>();

        try (Session session = driver.session()) {      

            // reset db
            System.out.println("Initializing Pokémon graph database...");
            session.run("match (n)-[r]->(m) delete (r)"); // delete all relationships
            session.run("match (n) delete n"); // delete all nodes
            
            // repopulate db
            session.run(new Query(readFile(SEEDDATA)));
            System.out.println("Done. Now running queries:");

            // queries
            String query, querystr;
            
            // 1
            query = "// 1. Find the name of all Pokémon that can mega-evolve.";
            querystr = "match (p:Pokemon)-[:MEGA_EVOLVE]->(me:MegaEvolution) return p.name as pokémon";
            queries.put(query, querystr);

            // 2
            query = "// 2. Find the 3 types that have more Pokémon.";
            querystr = "match (t:Type)<-[:IS]-(p:Pokemon) with t.name as type, count(p) as pokecount return type, pokecount order by pokecount desc limit 3";
            queries.put(query, querystr);

            // 3
            query = "// 3. For all Pokémon, find their name and their types.";
            querystr = "match (p:Pokemon)-[:IS]->(t:Type) with p.name as pkmn, collect(t.name) as types return pkmn, types";
            queries.put(query, querystr);

            // 4
            query = "// 4. Find all Pokémon with multiple evolutions. Present their name, and evolutions.";
            querystr = "match (p:Pokemon)-[:EVOLVE]->(evo:Pokemon) with p.name as pkmn, count(evo) as len, collect(evo.name) as evolutions where len > 1 return pkmn, evolutions";
            queries.put(query, querystr);

            // 5
            query = "// 5. Find the average level that a Pokémon that evolves through leveling has to be to evolve.";
            querystr = " match (p:Pokemon)-[e:EVOLVE]->(:Pokemon) where e.condition starts with 'Level ' with split(e.condition, ' ')[1] as level return avg(toInteger(level)) as averageLevel";
            queries.put(query, querystr);

            // 6
            query = "// 6. For all Pokémon with mega-evolutions, find the pokémon whose type(s) change(s) upon mega-evolving.";
            querystr = "match (t1:Type)<-[:IS]-(p:Pokemon)-[:MEGA_EVOLVE]->(m:MegaEvolution)-[:IS]->(t2:Type) with p.name as pkmn, collect(distinct t1.name) as types, collect(distinct t2.name) as mega_types with pkmn, types, mega_types, [x in types where not x in mega_types] + [x in mega_types where not x in types] as diff_types where size(diff_types) <> 0 return pkmn, types, mega_types, diff_types";
            queries.put(query, querystr);

            // 7
            query = "// 7. Find the 3 types that have the most Pokémon that don't evolve, don't have a regional form, and can't mega-evolve.";
            querystr = "match (p:Pokemon) where not (p)<-[:EVOLVE]->(:Pokemon) and not (p)-[:MEGA_EVOLVE]->(:MegaEvolution) and not (p)-[:HAS]->(:Form) match (t:Type)<-[:IS]-(p) with t.name as type, count(p) as pkmncount return type, pkmncount order by pkmncount desc limit 3";
            queries.put(query, querystr);

            // 8
            query = "// 8. Find all baby Pokémon (non-evolved) that can mega-evolve upon reaching their final form.";
            querystr = "match (baby:Pokemon)-[:EVOLVE*1..2]->(evo:Pokemon)-[:MEGA_EVOLVE]->(mega:MegaEvolution) with baby.name as babypkmn, mega.name as megaevo return babypkmn, megaevo";
            queries.put(query, querystr);

            // 9
            query = "// 9. Find all Pokémon that do not evolve through leveling.";
            querystr = "match (p:Pokemon)-[e:EVOLVE]->(b:Pokemon) where not e.condition starts with 'Level' return p.name as pkmn";
            queries.put(query, querystr);

            // 10
            query = "// 10. Find all single-type Pokémon that gain a typing upon evolving.";
            querystr = "match (t1:Type)<-[:IS]-(p1:Pokemon) with collect(t1.name) as preevo_type, p1 as pkmn where size(preevo_type) = 1 match (pkmn)-[e:EVOLVE*1..2]->(evo:Pokemon)-[:IS]->(t2:Type)  with collect(distinct t2.name) as evo_types, pkmn as pkmn, collect(distinct evo.name) as evos, preevo_type where size(evo_types) > 1 return pkmn.name as babypkmn, preevo_type, evos as evolutions, evo_types";
            queries.put(query, querystr);
            
            try {
                FileWriter fw = new FileWriter(OUTPUTFILE);
                for (String q : queries.keySet()) {
                    fw.write("\n\n" + q);
                    querystr = queries.get(q);
                    Result r = session.run(querystr);
                    while (r.hasNext()) {
                        Record record = r.next();
                        fw.write("\n" + record.fields().toString());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while writing to output file. Exiting...");
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        System.out.println("Finished. Closing driver and exiting.");
        driver.close();
    }

    public static String readFile(String SEEDDATA) throws IOException {
        StringBuilder sb = new StringBuilder();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(SEEDDATA);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
