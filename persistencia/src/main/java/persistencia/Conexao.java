package persistencia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @author alunoinf
 */
public class Conexao {
    /*DISPONIBILIZO O CLIENTE*/

    private static MongoClient cliente = new MongoClient();
    private static MongoDatabase banco = cliente.getDatabase("banco");

    public static MongoDatabase receberBanco(){
        return banco;
    }

}
