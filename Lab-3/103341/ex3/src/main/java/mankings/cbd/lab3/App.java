package mankings.cbd.lab3;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Row;

public class App {
    static Session session;

    public static void main(String[] args) {
        try {
            Cluster cluster = Cluster.builder().addContactPoint("127.19.0.2").build();
            session = cluster.connect("cbd_103341_ex2");
            
            System.out.println(">> Connected to Cassandra (127.19.0.2:9042)");

            alineaA();
            alineaB();

        } catch (Exception e) {
            System.err.println("Couldn't connect to Cassandra at 127.19.0.2:9042");
            System.exit(1);
        }
    }

    private static void alineaA() {
        // data insertion
        try {
            session.execute("INSERT INTO ratings (id_video, rate) VALUES (10, 5);");
            session.execute("INSERT INTO videos (id, author, name, description, tags, ts) VALUES (15, 'tiagovski', 'Nova intro do canal', 'Musica bombada', {'gaming'}, toTimestamp(now()));");
            session.execute("INSERT INTO videos_by_author (id, author, name, description, tags, ts) VALUES (15, 'tiagovski', 'Nova intro do canal', 'Musica bombada', {'gaming'}, toTimestamp(now()));");

            System.out.println("Inserts done.\n");
        } catch (Exception e) {
            System.err.println("Inserts failed. Exiting.\n" + e.getMessage());
            System.exit(1);
        }

        // data updating
        try {
            System.out.println("Update name to 'Mancósmico' where username is 'mankings'. ");
            session.execute("UPDATE users SET name='Mancósmico' WHERE username='mankings';");
            for (Row r : session.execute("SELECT * FROM users WHERE username='mankings';")) {
                System.out.println(r.toString());
            }

            System.out.println("Update description to 'Mega likezão' where video id is 1.");
            session.execute("UPDATE videos SET description='Mega likezão' WHERE id=1;");
            for (Row r : session.execute("SELECT * FROM videos WHERE id=1;")) {
                System.out.println(r.toString());
            }

            System.out.println("Updates done.\n");
        } catch (Exception e) {
            System.err.println("Updates failed. Exiting.\n" + e.getMessage());
            System.exit(1);
        }


        // data searching
        try {
            System.out.println("Get all videos posted by username 'mankings'.");
            for (Row r : session.execute("SELECT * FROM videos WHERE username='mankings';")) {
                System.out.println(r.toString());
            }

            System.out.println("Get all followers of video with id 7.");
            for (Row r : session.execute("SELECT * FROM followers WHERE id_video=7;")) {
                System.out.println(r.toString());
            }

            System.out.println("Get 5 most recent videos.");
            for (Row r : session.execute("SELECT * FROM videos LIMIT 5;")) {
                System.out.println(r.toString());
            }

            System.out.println("Searches done.\n");
        } catch (Exception e) {
            System.err.println("Search failed. Exiting.\n" + e.getMessage());
            System.exit(1);
        }
    }

    private static void alineaB() {
        try {
            System.out.println("2. Lista das tags de determinado vídeo");
            for (Row r : session.execute("select id, tags from videos where id=5;")) {
                System.out.println(r.toString());
            }

            System.out.println("4. Os últimos 5 eventos de determinado vídeo realizados por um utilizador");
            for (Row r : session.execute("select * from events where username='lucas' and id_video=7 limit 5;")) {
                System.out.println(r.toString());
            }

            System.out.println("5. Vídeos partilhados por determinado utilizador (maria1987, por exemplo) num determinado período de tempo (Agosto de 2017, por exemplo)");
            for (Row r : session.execute("select * from videos_by_author where author='marychannel' and ts > '2022-11-01';")) {
                System.out.println(r.toString());
            }

            System.out.println("7. Todos os seguidores (followers) de determinado vídeo");
            for (Row r : session.execute("select users from followers where id_video=1;")) {
                System.out.println(r.toString());
            }
        } catch (Exception e) {
            System.err.println("Queries failed. Exiting.\n" + e.getMessage());
            System.exit(1);
        }
    }
}
