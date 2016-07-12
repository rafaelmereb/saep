package persistencia;

import br.ufg.inf.es.saep.sandbox.dominio.*;
import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ResolucaoPersistencia extends Persistencia implements ResolucaoRepository {


    private final Gson gson = new Gson();

    public ResolucaoPersistencia(MongoDatabase banco) {
        super(banco);
    }


    @Override
    public Resolucao byId(String s) {
        Document doc = find("id", s, getColecaoDeResolucoes());
        if (doc == null) return null;
        else return gson.fromJson(gson.toJson(doc), Resolucao.class);
    }


    @Override
    public String persiste(Resolucao resolucao) {

        if (resolucao.getId() == "") throw new CampoExigidoNaoFornecido("id");
        else {
            if (count("id", resolucao.getId(), getColecaoDeResolucoes()) == 0) {

                persist(gson.toJson(resolucao),getColecaoDeResolucoes());
                return resolucao.getId();
            } else {
                throw new IdentificadorExistente("id");
                //return null; //?
            }
        }
    }


    @Override
    public boolean remove(String s) {
        delete("id", s, getColecaoDeResolucoes());
        return (find("id", s, getColecaoDeResolucoes()) == null);
    }

    @Override
    public List<String> resolucoes() {
        return distinct("id", getColecaoDeResolucoes());
    }

    @Override
    public void persisteTipo(Tipo tipo) {
        String id = tipo.getId();
        //Se não houverem outros tipos de mesmo id, persistir:
        List<String> listaIdsTipos = distinct("id", getColecaoDeTipos());
        boolean jaExiste = false;
        for (String idPresenteNoBanco : listaIdsTipos) {
            //System.out.println("Id salvo: " + idPresenteNoBanco);
            if (idPresenteNoBanco.equals(id)) jaExiste = true;
        }
        if (jaExiste) throw new IdentificadorExistente("id");
        else persist(gson.toJson(tipo),getColecaoDeTipos());
    }

    @Override
    public void removeTipo(String s) {
        //falta achar referencias de uso para lançar exceção
        delete("id", s, getColecaoDeTipos());
    }

    @Override
    public Tipo tipoPeloCodigo(String s) {
        return gson.fromJson(gson.toJson(find("id", s, getColecaoDeTipos())), Tipo.class);
    }

    @Override
    public List<Tipo> tiposPeloNome(String s) {
        List<Tipo> listaTipos = new ArrayList<Tipo>();
        List<Document> listaDocumentos = findSimilars("nome", s, getColecaoDeTipos());
        for (Document documento : listaDocumentos){
            listaTipos.add(gson.fromJson(gson.toJson(documento), Tipo.class));
        }

        return listaTipos;
    }

}

