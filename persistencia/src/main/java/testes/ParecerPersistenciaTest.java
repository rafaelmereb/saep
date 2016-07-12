package testes;


import br.ufg.inf.es.saep.sandbox.dominio.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.*;
import persistencia.Conexao;
import persistencia.Persistencia;
import persistencia.ParecerPersistencia;
import persistencia.ResolucaoPersistencia;

import java.util.*;


public class ParecerPersistenciaTest {

    Conexao conexao;
    ResolucaoPersistencia resPersistencia;
    ParecerPersistencia parPersistencia;
    List<String> dependeDe = new ArrayList<String>();
    List<Regra> listaDeRegras = new ArrayList<Regra>()

    @Before
    public void setUp() throws Exception {
        conexao = new Conexao();

        parPersistencia = new ParecerPersistencia(conexao.receberBanco());
        resPersistencia.removeAll(resPersistencia.getColecaoDeResolucoes());
        resPersistencia.removeAll(resPersistencia.getColecaoDePareceres());
        resPersistencia.removeAll(resPersistencia.getColecaoDeTipos());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAdicionaNota() throws Exception {

    }

    @Test
    public void testRemoveNota() throws Exception {

    }

    @Test
    public void testPersisteParecer() throws Exception {

    }

    @Test
    public void testAtualizaFundamentacao() throws Exception {

    }

    @Test
    public void testById() throws Exception {

    }

    @Test
    public void testRemoveParecer() throws Exception {

    }

    @Test
    public void testRadocById() throws Exception {

    }

    @Test
    public void testPersisteRadoc() throws Exception {

    }

    @Test
    public void testRemoveRadoc() throws Exception {

    }

    public Parecer getParecer1(){

        List<String> radocs = new ArrayList<String>();
        radocs.add("radoc01");
        radocs.add("radoc02");

        List<Pontuacao> pontuacoes = new ArrayList<Pontuacao>();

        Valor valor01 = new Valor(01);
        Pontuacao pontuacaoReal = new Pontuacao("atributo01-real", valor01);
        pontuacoes.add(pontuacaoReal);

        Valor valor02 = new Valor(true);
        Pontuacao pontuacaoLogica = new Pontuacao("atributo02-logico", valor02);
        pontuacoes.add(pontuacaoLogica);

        Valor valor03 = new Valor("resultado");
        Pontuacao pontuacaoString = new Pontuacao("atributo03-string", valor03);
        pontuacoes.add(pontuacaoString);


        Avaliavel origem = new Avaliavel() {
            @Override
            public Valor get(String atributo) {
                return null;
            }
        };

        Avaliavel destino = new Avaliavel() {
            @Override

            public Valor get(String atributo) {
                return null;
            }
        };
        String justificativa = "justificativa";


        List<Nota> notas = new ArrayList<Nota>();
        Nota nota01 = new Nota(origem, destino, justificativa);
        notas.add(nota01);

        return new Parecer(
               "001",                 //id
                "001",               // resolucaoId
                radocs,              // radocsIds
                pontuacoes,         // pontuacoes
                "fundamentacao",    // notas
                notas
        );
    }
}
