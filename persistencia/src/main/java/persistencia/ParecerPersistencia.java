package persistencia;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

/**
 * @author alunoinf
 */
public class ParecerPersistencia extends Persistencia implements ParecerRepository {

    private final Gson gson = new Gson();

    public ParecerPersistencia(MongoDatabase banco) {
        super(banco);
    }

    @Override
    public void adicionaNota(String parecer, Nota nota) {
        Document doc = find("id",parecer, getColecaoDePareceres());

        if (doc != null){
            Parecer parecerOriginal = gson.fromJson(gson.toJson(doc), Parecer.class);
            List<Nota> notas = parecerOriginal.getNotas();
            notas.add(nota);
            //String notasJson = gson.toJson(notas);
            //update("id",parecer,"notas",notasJson,getColecaoDePareceres());
            Parecer novoParecer = new Parecer(
                    parecerOriginal.getId(),
                    parecerOriginal.getResolucao(),
                    parecerOriginal.getRadocs(),
                    parecerOriginal.getPontuacoes(),
                    parecerOriginal.getFundamentacao(),
                    notas
            );
            replace("id", parecer, gson.toJson(novoParecer), getColecaoDePareceres());
        }
        else throw new IdentificadorDesconhecido("Parecer com identificador " + parecer + " nao encontrado!");
    }

    @Override
    public void removeNota(String id, Avaliavel original) {
        Document documentoParaRemocao = new Document("notas", new Document("original", Document.parse(gson.toJson(original))));
        pull("id", id, documentoParaRemocao, getColecaoDePareceres());
    }


    @Override
    public void persisteParecer(Parecer parecer) {
        if (count("id", parecer.getId(), getColecaoDePareceres()) > 0){
            throw new IdentificadorDesconhecido("Parecer com identificador " + parecer + " nao encontrado!");
        } else {
            String parecerJSON = gson.toJson(parecer);
            persist(parecerJSON, getColecaoDePareceres());
        }
    }

    @Override
    public void atualizaFundamentacao(String parecer, String fundamentacao) {
        Document doc = find("id",parecer,getColecaoDePareceres());

        if (doc != null) {
            //Parecer parecerOriginal = gson.fromJson(gson.toJson(doc), Parecer.class);
            update("id",parecer,"fundamentacao", fundamentacao, getColecaoDePareceres());
        } else {

            throw new IdentificadorDesconhecido("Parecer com identificador " + parecer + " nao encontrado!");
        }
    }

    @Override
    public Parecer byId(String id) {
        Document doc = find("id", id, getColecaoDePareceres());
        if (doc != null){
            return gson.fromJson(gson.toJson(doc), Parecer.class);
        }
        else return null;
    }

    @Override
    public void removeParecer(String id) {
        delete("id", id, getColecaoDePareceres());
    }

    @Override
    public Radoc radocById(String identificador) {
        Document radoc = find("id", identificador, getColecaoDeRadocs());
        if (radoc != null) return gson.fromJson(gson.toJson(radoc), Radoc.class);
        else return null;
    }

    @Override
    public String persisteRadoc(Radoc radoc) {
        persist(gson.toJson(radoc), getColecaoDeRadocs());
        if (find("id", radoc.getId(),getColecaoDeRadocs()) != null){
            return radoc.getId();
        }
        else return null;
    }

    @Override
    public void removeRadoc(String identificador) {
        //Verificar se há algum parecer referenciando o Radoc na colecaoDePareceres:
        if ((advancedFind(getColecaoDePareceres(), new Document().append("radocs", identificador)) == null)){
            delete("id", identificador, getColecaoDeRadocs());
        }
    }

}