package persistencia;


import br.ufg.inf.es.saep.sandbox.dominio.*;
import com.google.gson.Gson;
import org.bson.Document;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParecerPersistenciaTest {

    Conexao conexao;
    ResolucaoPersistencia resPersistencia;
    ParecerPersistencia parPersistencia;


    @Before
    public void setUp() throws Exception {
        conexao = new Conexao();
        parPersistencia = new ParecerPersistencia(conexao.receberBanco());
        parPersistencia.removeAll(parPersistencia.getColecaoDePareceres());
        parPersistencia.removeAll(parPersistencia.getColecaoDeRadocs());
        parPersistencia.removeAll(parPersistencia.getColecaoDeTipos());
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Verifica se uma nota é adicionada com sucesso
     */
    @Test
    public void testeAdicionaNota() {
        Parecer parecer = getParecer();
        parPersistencia.persisteParecer(parecer);
        int qtdNotasOriginal = parPersistencia.byId("001").getNotas().size();

        Nota nota = getNewNota();
        parPersistencia.adicionaNota(parecer.getId(), nota);
        int qtdNotasAtual = parPersistencia.byId("001").getNotas().size();

        Assert.assertEquals(qtdNotasOriginal + 1, qtdNotasAtual);
    }

    /**
     * Testa se a exceção IdentificadorDesconhecido é lançada ao tentar adicionar uma nota referenciando um id de um parecer inexistente no banco.
     */
    @Test(expected = IdentificadorDesconhecido.class)
    public void testeAdicionaNota_ParecerInexistente() {
        Parecer parecer = getParecer();
        Nota nota = getNota();
        parPersistencia.adicionaNota(parecer.getId(), nota);
    }

    /**
     * Testa se uma nota é removida com sucesso
     */
    @Test
    public void testeRemoveNota() {
        Parecer parecer = getParecer();
        int qtdNotasOriginal = parecer.getNotas().size();
        Nota nota = parecer.getNotas().get(0);
        parPersistencia.persisteParecer(parecer);
        parPersistencia.removeNota(parecer.getId(), nota.getItemOriginal());
        int qtdNotasAtual = parPersistencia.byId(parecer.getId()).getNotas().size();
        Assert.assertEquals(qtdNotasOriginal - 1, qtdNotasAtual);
    }

    /**
     * Testa se o parecer é persistido
     */
    @Test
    public void testePersisteParecer() {
        Parecer parecer = getParecer();
        parPersistencia.persisteParecer(parecer);
        Assert.assertEquals(parecer.getId(), parPersistencia.byId(parecer.getId()).getId());
    }

    /**
     * Testa se uma uma atualização de fundamentação é realmente realizada
     */
    @Test
    public void testeAtualizaFundamentacao() {
        Parecer parecer = getParecer();
        parPersistencia.persisteParecer(parecer);
        String fundamentacao = "Nova Fundamentacao";
        parPersistencia.atualizaFundamentacao(parecer.getId(), fundamentacao);
        Assert.assertEquals(fundamentacao, parPersistencia.byId(parecer.getId()).getFundamentacao());
    }

    /**
     * Testa se a exceção Identificador desconhecido é lançada ao tentar atualizar
     * a fundamentação de um parecer inexistente no banco de dados.
     */
    @Test(expected = IdentificadorDesconhecido.class)
    public void testeAtualizaFundamentacao_ParecerInexistente() {
        String fundamentacao = "Nova Fundamentacao";
        parPersistencia.atualizaFundamentacao("9999999", fundamentacao);
    }

    /**
     * Testa se uma busca de parecer por seu identificador resulta em sucesso.
     */
    @Test
    public void testeById() {
        Parecer parecer = getParecer();
        parPersistencia.persisteParecer(parecer);
        Assert.assertEquals(parecer.getId(), parPersistencia.byId(parecer.getId()).getId());
    }

    /**
     * Testa se um parecer persistido é removido do banco com sucesso.
     */
    @Test
    public void testRemoveParecer() {
        parPersistencia.persisteParecer(getParecer());
        parPersistencia.removeParecer(getParecer().getId());
        Assert.assertNull(parPersistencia.byId(getParecer().getId()));
    }

    /**
     * Testa se uma busca de radoc por seu identificador resulta em sucesso.
     */
    @Test
    public void testRadocById() {
        Radoc radoc = getRadoc("001");
        parPersistencia.persisteRadoc(radoc);
        Assert.assertEquals(radoc.getId(), parPersistencia.radocById(radoc.getId()).getId());
    }

    /**
     * Testa se um Radoc é persistido no banco de dados com sucesso.
     */
    @Test
    public void testPersisteRadoc() {
        Radoc radoc = getRadoc("001");
        parPersistencia.persisteRadoc(radoc);
        Assert.assertTrue(parPersistencia.radocById(radoc.getId()).getId().equals(radoc.getId()));
    }

    /**
     * Testa se a remoção de um Radoc é efetuada com sucesso.
     */
    @Test
    public void testRemoveRadoc() {
        Radoc radoc = getRadoc("001");
        parPersistencia.persisteRadoc(radoc);
        parPersistencia.removeRadoc(radoc.getId());
        Assert.assertNull(parPersistencia.radocById(radoc.getId()));
    }

    Gson gson = new Gson();

    @Test
    public void test(){

    }

    public Parecer getParecer() {
        return new Parecer(
                "001",
                "002",
                getListaDeIdsRadocs("001", "002", "003"),
                getListaDePontuacoes("Pontuacao001", "Valor001", "Pontuacao002", "Valor002"),
                "Fundamentacao",
                getListadeNotas()
        );

    }

    public Radoc getRadoc(String id) {
        List<Relato> listaDeRelatos = new ArrayList<Relato>();
        listaDeRelatos.add(getRelato("Relato001"));
        listaDeRelatos.add(getRelato("Relato002"));
        return new Radoc(id, 2016, listaDeRelatos);
    }

    public List<String> getListaDeIdsRadocs(String... ids) {
        List<String> listaDeIdsRadocs = new ArrayList<String>();

        for (String id : ids) {
            listaDeIdsRadocs.add(id);
        }
        return listaDeIdsRadocs;
    }

    public List<Pontuacao> getListaDePontuacoes(String... entradas) {
        if (entradas.length % 2 == 0) {
            List<Pontuacao> listaPontuacoes = new ArrayList<Pontuacao>();

            for (int i = 0; i < entradas.length - 1; i++) {
                listaPontuacoes.add(new Pontuacao(entradas[i], new Valor(entradas[i + 1])));
                i++;
            }
            return listaPontuacoes;
        } else {
            return null;
        }
    }

    public List<Nota> getListadeNotas() {
        List<Nota> listaDeNotas = new ArrayList<Nota>();
        listaDeNotas.add(getNota());
        return listaDeNotas;
    }

    public Nota getNota() {
        return new Nota(getRelato("RelatoTipo001"), getRelato("RelatoTipo002"), "Justificativa");
    }

    public Nota getNewNota() {
        return new Nota(getRelato("NovoRelatoTipo001"), getRelato("NovoRelatoTipo002"), "NovaJustificativa");
    }

    public Relato getRelato(String tipo) {
        Map<String, Valor> valores = new HashMap<String, Valor>();
        Valor valorTeste001 = new Valor("teste001");
        Valor valorTeste002 = new Valor("teste002");

        valores.put(tipo, valorTeste001);
        valores.put(tipo, valorTeste002);

        return new Relato(tipo, valores);

    }

}
