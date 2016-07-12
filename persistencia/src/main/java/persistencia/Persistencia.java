package persistencia;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafa.
 */
public class Persistencia {

    private MongoDatabase banco;

    private final String  colecaoDeResolucoes = "resolucao";
    private final String colecaoDePareceres = "parecer";
    private final String  colecaoDeTipos = "tipo";
    private final String colecaoDeRadocs = "radoc";

    public String getColecaoDeResolucoes() {
        return colecaoDeResolucoes;
    }

    public String getColecaoDeTipos() {
        return colecaoDeTipos;
    }

    public String getColecaoDePareceres() {
        return colecaoDePareceres;
    }

    public String getColecaoDeRadocs() {
        return colecaoDeRadocs;
    }



    Persistencia( MongoDatabase banco){
        this.banco = banco;
    }

    /**
     * Retorna a coleção correspondente à String recebida.
     *
     * @param nomeDaColecao - String contendo o nome da coleção desejada.
     * @return - Coleção correspondente ao nome recebido.
     */
    private MongoCollection<Document> receberColecao(String nomeDaColecao) {

        return banco.getCollection(nomeDaColecao);
    }

    /**
     * Persiste um objeto fornecido numa coleção também identificada.
     *
     * @param objetoJson - Objeto em Json a ser persistido
     * @param nomeColecao
     */
    void persist(String objetoJson, String nomeColecao) {

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        Document documento = Document.parse(objetoJson);
        colecao.insertOne(documento);
    }

    /**
     * Busca um objeto de uma coleção a partir do atributo (chave) identificador.
     *
     * @param atributoId - Especifica qual é o atributo identificador do objeto salvo no Banco de dados. (Ex: "id")
     * @param valor - Valor do atributo identificador a ser buscado no banco (Ex: "201301571").
     * @param nomeColecao - Nome da coleção correspondente ao objeto utilizado na busca.*
     * @return - Objeto do tipo Document, resultado da consulta ao banco de dados.
     */
    Document find(String atributoId, String valor, String nomeColecao) {

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        return colecao.find(new Document().append(atributoId,valor)).first();
    }

    /**
     * Realiza uma consulta avançada, segundo parâmetro recebido.
     *
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     * @param consulta - "Query"/"Filtro" utilizado na consulta.
     * @return - Elemento encontrado na consulta.
     */
    Document advancedFind(String nomeColecao, Document consulta){

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        return colecao.find(consulta).first();
    }

    /**
     * Busca objetos similares de uma coleção a partir do padrão informado.
     *
     * @param atributoParaBusca - Especifica qual é o atributo (chave) utilizado na busca no Banco de Dados.
     * @param sequencia - Representa o padrão em String utilizado para identificar similares.
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     * @return - Lista de Documentos contendo o resultado da consulta.
     */
    List<Document> findSimilars(String atributoParaBusca, String sequencia, String nomeColecao){

        //MongoCollection<Document> colecao = receberColecao(nomeColecao);
        List<Document> lista = new ArrayList<Document>();
        //Pattern padraoParaBusca = Pattern.compile(sequencia);
        //Document consulta = new Document().append(atributoParaBusca, sequencia);

        for (String similar : distinct(atributoParaBusca, nomeColecao)){
            if (sequencia.contains(similar)){
                lista.add(find(atributoParaBusca, similar, nomeColecao));
            }
        }

        return lista;
    }

    /**
     * Realiza a contagem do número de elementos de uma coleção de acordo com os parâmetros fornecidos
     *
     * @param atributo - Atributo utilizado na consulta.
     * @param valor - Valor correspondente ao atributo utilizado na consulta
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     * @return - Número de elementos resultantes da consulta realizada.
     */
    long count(String atributo, String valor, String nomeColecao) {

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        return colecao.count(new Document().append(atributo,valor));
    }

    /**
     * Substitui completamente um objeto presente no banco de dados pelo fornecido
     *
     * @param atributoId - Atributo identificador do objeto presente no banco.
     * @param valor - Valor correspondente ao atributo utilizado na consulta.
     * @param objetoJson - Objeto em formato Json que substituirá o original.
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     */
    void replace(String atributoId, String valor, String objetoJson, String nomeColecao) {

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        Document documentoOriginal = new Document().append(atributoId, valor);
        Document novoDocumento = Document.parse(objetoJson);
        colecao.replaceOne(documentoOriginal, novoDocumento);
    }

    /**
     * Atualiza conteúdo de determinado(s) objeto(s) presente(s) no banco de dados.
     *
     * @param atributoId - Atributo identificador do objeto presente no banco.
     * @param valor - Valor correspondente ao atributo utilizado na consulta.
     * @param atributoModificado - Atributo que será modificado na operação.
     * @param novoConteudo - Novo conteúdo do atributo que será modificado na operação.
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     */
    void update(String atributoId, String valor, String atributoModificado, String novoConteudo, String nomeColecao) {

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        Document documentoOriginal = new Document().append(atributoId, valor);
        Document alteracao = new Document().append("$set", new Document().append(atributoModificado, novoConteudo));
        colecao.updateOne(documentoOriginal, alteracao);

    }

    /**
     * Remove um objeto do banco de dados a partir de seu identificador.
     *
     * @param atributoId - Atributo identificador do objeto presente no banco.
     * @param valor - Valor correspondente ao atributo utilizado na consulta.
     * @param nomeColecao - Nome da coleção pela qual a consulta será realizada.
     */
    void delete(String atributoId, String valor, String nomeColecao){

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        colecao.deleteOne(new Document().append(atributoId, valor));
    }

    /**
     * Resgata os valores distintos de um determinado campo (atributo).
     *
     * @param atributo - Atributo que será utilizado na busca.
     * @param nomeColecao - Nome da coleção pela qual a busca será realizada.
     * @return - Lista de Strings contendo os valores distintos do campo fornecido.
     */
    List<String> distinct(String atributo, String nomeColecao){

        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        List<String> lista = new ArrayList<String>();

        for (String id : colecao.distinct(atributo, String.class)){
            lista.add(id);
        }

        /* Outra forma (teste):
        *
        MongoCursor<String> cursor = colecao.distinct("id", String.class).iterator();
        while (cursor.hasNext()){
            lista.add(cursor.next());
        }
        */

        return lista;
    }

    void pull(String atributoId, String valor, Document documentoParaRemocao, String nomeColecao){
        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        Document pull = new Document("$pull", documentoParaRemocao);
        colecao.updateOne(new Document().append(atributoId, valor), documentoParaRemocao);
    }

    public void removeAll(String nomeColecao){
        MongoCollection<Document> colecao = receberColecao(nomeColecao);
        colecao.drop();
    }


}
