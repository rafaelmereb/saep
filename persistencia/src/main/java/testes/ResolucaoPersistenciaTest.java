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


import static org.junit.Assert.*;

/**
 * Created by RAFAEL-OOBJ on 12/07/2016.
 */
public class ResolucaoPersistenciaTest {

    Conexao conexao;
    ResolucaoPersistencia resPersistencia;
    ParecerPersistencia parPersistencia;
    List<String> dependeDe = new ArrayList<String>();
    List<Regra> listaDeRegras = new ArrayList<Regra>();

    @Before
    public void setUp() throws Exception {
        conexao = new Conexao();
        resPersistencia = new ResolucaoPersistencia(conexao.receberBanco());

        resPersistencia.removeAll(resPersistencia.getColecaoDeResolucoes());
        resPersistencia.removeAll(resPersistencia.getColecaoDePareceres());
        resPersistencia.removeAll(resPersistencia.getColecaoDeTipos());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    /**
     * Testa se o retorno obtido do método byId() é uma Resolução.
     */
    public void testeById_retornaResolucao() {

        resPersistencia.persiste(getResolucao1());
        Resolucao resolucao = resPersistencia.byId(getResolucao1().getId());
        Assert.assertEquals(getResolucao1(), resolucao);
    }

    @Test
    /**
     * Testa se o retorno obtido pelo método byId é nulo quando não é encontrado um objeto no banco.
     */
    public void testeById_retornoNulo() {

        resPersistencia.persiste(getResolucao1());
        Resolucao resolucao = resPersistencia.byId("0000000000000"); //Presume-se que só há o objeto de id 001
        Assert.assertEquals(null, resolucao);
    }

    @Ignore
    @Test(expected = CampoExigidoNaoFornecido.class)
    public void testePersiste_campoExigidoNaoFornecido() {
        //Verificar porque está retornando "nome"
        try{resPersistencia.persiste(getResolucao1_IdNull());}
        catch(CampoExigidoNaoFornecido ex){ Assert.assertEquals("id", ex.getMessage());}
    }

    @Ignore
    @Test
    public void testePersiste_identificadorExistente() {

    }


    @Test
    public void testeRemove_retornoNulo(){
        resPersistencia.persiste(getResolucao1());
        resPersistencia.remove(getResolucao1().getId());

        //Se a busca pelo id ter resultado nulo, a remoção foi um sucesso:
        Assert.assertEquals(null ,resPersistencia.byId(getResolucao1().getId()) );
    }

    @Test
    public void testeResolucoes() throws Exception {
        //Inserindo objetos no banco
        resPersistencia.persiste(getResolucao1());
        resPersistencia.persiste(getResolucao2());

        //Criando a lista para comparaçao com os ids dos objetos persistidos:
        List<String> listaIdsResolucoes = new ArrayList<String>();
        listaIdsResolucoes.add(getResolucao1().getId());
        listaIdsResolucoes.add(getResolucao2().getId());

        //Comparando as duas listas:
        Assert.assertEquals(listaIdsResolucoes, resPersistencia.resolucoes());
    }

    @Ignore
    @Test
    public void testePersisteTipo() throws Exception {
        resPersistencia.persisteTipo(getTipo1());
        resPersistencia.persisteTipo(getTipo2());
        Assert.assertEquals(getTipo1(), resPersistencia.tipoPeloCodigo(getTipo1().getId()));
    }

    @Test
    public void testeRemoveTipo() throws Exception {
       //Adicionando objetos:
        resPersistencia.persisteTipo(getTipo1());


        //Removendo objetos
        resPersistencia.removeTipo(getTipo1().getId());


        //Consulta por qualquer um dos dois deve retornar nulo.
        Assert.assertEquals(null, resPersistencia.tipoPeloCodigo(getTipo1().getId()));
    }

    @Test
    public void testeTipoPeloCodigo_retornaResolucao(){
       resPersistencia.persisteTipo(getTipo1());
       Assert.assertEquals(getTipo1(), resPersistencia.tipoPeloCodigo(getTipo1().getId()));
    }

    @Test
    public void testeTiposPeloNome() throws Exception {
        //Inserindo um objeto cujo nome é idêntico ao parâmetro de similaridade:
        resPersistencia.persisteTipo(getTipo1());

        //Inserindo um objeto com nome não similar:
        resPersistencia.persisteTipo(getTipo2());

        //Inserindo um objeto com nome similar:
        resPersistencia.persisteTipo(getTipo3());


        List<Tipo> listaTiposSimilares = new ArrayList<Tipo>();
        listaTiposSimilares.add(getTipo1()); // Um nome é similar a ele próprio
        listaTiposSimilares.add(getTipo3()); // Pois o tipo3 possui 01 no nome.

        List<Tipo> listaObtida = resPersistencia.tiposPeloNome(getTipo1().getNome());
        for (Tipo tipo: listaObtida){
            System.out.println("Similar a " + getTipo1().getNome() + ": " + tipo.getNome() + " (Objeto: " + tipo.getId() + ")");
        }
        Assert.assertEquals(listaTiposSimilares, listaObtida);
    }

    public Resolucao getResolucao1() {
        //Objeto 1
        Regra regra1 = new Regra(
                "Monstros",          // variavel
                Regra.PONTOS,       // tipo
                "Somatorio",       // descricao
                100,              // valorMaximo
                0,               // valorMinimo
                "+",            // expressao
                "entao",       // entao
                "senao",      // senao
                "0",         // tipoRelato
                20,         // pontosPorItem
                dependeDe  // dependeDe
        );

        listaDeRegras.add(regra1);


        return (new Resolucao(
                "001",                  // id
                "res001",              // nome
                "Primeira Resolucao", // descricao
                new Date(),         // dataAprovacao
                listaDeRegras       // regras
        ));
    }
    public Resolucao getResolucao2() {
        //Objeto 2
        Regra regra2 = new Regra(
                "Mutantes",           // variavel
                Regra.PONTOS,        // tipo
                "Multiplicacao",    // descricao
                100,               // valorMaximo
                0,                // valorMinimo
                "+",             // expressao
                "entao",        // entao
                "senao",       // senao
                "0",          // tipoRelato
                20,          // pontosPorItem
                dependeDe   // dependeDe
        );
        listaDeRegras.clear();
        listaDeRegras.add(regra2);

        return new Resolucao(
                "002",                 // id
                "res002",             // nome
                "Segunda Resolucao", // descricao
                new Date(),         // dataAprovacao
                listaDeRegras      // regras
        );
    }
    public Resolucao getResolucao1_IdNull() {
        //Objeto 1
        Regra regra1 = new Regra(
                "Monstros",          // variavel
                Regra.PONTOS,       // tipo
                "Somatorio",       // descricao
                100,              // valorMaximo
                0,               // valorMinimo
                "+",            // expressao
                "entao",       // entao
                "senao",      // senao
                "0",         // tipoRelato
                20,         // pontosPorItem
                dependeDe  // dependeDe
        );

        listaDeRegras.add(regra1);


        return (new Resolucao(
                "",                  // id
                "res001",              // nome
                "Primeira Resolucao", // descricao
                new Date(),         // dataAprovacao
                listaDeRegras       // regras
        ));
    }
    public Tipo getTipo1(){
        Set<Atributo> listaDeAtributos = new HashSet<Atributo>();
        Atributo atributo1 = new Atributo("atributo1","Este e o atributo1",Atributo.LOGICO);
        Atributo atributo2 = new Atributo("atributo2","Este e o atributo2",Atributo.STRING);
        listaDeAtributos.add(atributo1);
        listaDeAtributos.add(atributo2);

        return new Tipo(
                "001",
                "Tipo01",
                "Esse e um objeto do tipo 01",
                listaDeAtributos
        );
    }
    public Tipo getTipo2(){
        Set<Atributo> listaDeAtributos = new HashSet<Atributo>();
        Atributo atributo1 = new Atributo("atributo3","Este e o atributo3",Atributo.LOGICO);
        Atributo atributo2 = new Atributo("atributo4","Este e o atributo4",Atributo.STRING);
        listaDeAtributos.add(atributo1);
        listaDeAtributos.add(atributo2);

        return new Tipo(
                "002",
                "Tipo02",
                "Esse e um objeto do tipo 02",
                listaDeAtributos
        );
    }
    public Tipo getTipo3(){
        Set<Atributo> listaDeAtributos = new HashSet<Atributo>();
        Atributo atributo1 = new Atributo("atributo5","Este e o atributo5",Atributo.LOGICO);
        Atributo atributo2 = new Atributo("atributo6","Este e o atributo6",Atributo.STRING);
        listaDeAtributos.add(atributo1);
        listaDeAtributos.add(atributo2);

        return new Tipo(
                "003",
                "01",
                "Esse e um objeto do tipo 02",
                listaDeAtributos
        );
    }
}