package br.com.educonecta.repository;

import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Endereco;
import br.com.educonecta.model.Responsavel;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoRepositoryTest {

    @Mock
    private MongoDatabase database;

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private FindIterable<Document> findIterable;

    private AlunoRepository repository;

    @BeforeEach
    void setUp() {
        when(database.getCollection("alunos")).thenReturn(collection);
        repository = new AlunoRepository(database);
    }

    private Document documentoDeAluno(ObjectId id, String nome, int idade, String curso, String turno) {
        return new Document("_id", id)
                .append("nome", nome).append("idade", idade)
                .append("curso", curso).append("turno", turno).append("ativo", true);
    }

    @Test
    void deveInserirAlunoEDefinirIdGerado() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        ObjectId idGerado = new ObjectId();

        doAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.put("_id", idGerado);
            return null;
        }).when(collection).insertOne(any(Document.class));

        Aluno resultado = repository.inserir(aluno);

        assertEquals(idGerado.toHexString(), resultado.getId());
        verify(collection).insertOne(argThat(doc ->
                "Maria".equals(doc.getString("nome"))
                        && Integer.valueOf(10).equals(doc.getInteger("idade"))
                        && "Matematica".equals(doc.getString("curso"))
                        && "Tarde".equals(doc.getString("turno"))
                        && Boolean.TRUE.equals(doc.getBoolean("ativo"))));
    }

    @Test
    void deveGravarEnderecoComoSubdocumentoEResponsaveisComoListaDeSubdocumentos() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        aluno.setEndereco(new Endereco("Rua A", "Centro", "Maringa", "PR"));
        aluno.setResponsaveis(List.of(
                new Responsavel("Ana", "Mae", "44999991234"),
                new Responsavel("Jose", "Pai", "44988887777")));
        doAnswer(invocation -> {
            ((Document) invocation.getArgument(0)).put("_id", new ObjectId());
            return null;
        }).when(collection).insertOne(any(Document.class));

        repository.inserir(aluno);

        verify(collection).insertOne(argThat(doc -> {
            Document endereco = doc.get("endereco", Document.class);
            List<Document> responsaveis = doc.getList("responsaveis", Document.class);
            return endereco != null
                    && "Maringa".equals(endereco.getString("cidade"))
                    && "PR".equals(endereco.getString("estado"))
                    && responsaveis.size() == 2
                    && "Ana".equals(responsaveis.get(0).getString("nome"))
                    && "44988887777".equals(responsaveis.get(1).getString("telefone"));
        }));
    }

    @Test
    void naoDeveGravarCampoEnderecoQuandoAlunoNaoTemEndereco() {
        Aluno aluno = new Aluno("Maria", 20, "Matematica", "Tarde");
        doAnswer(invocation -> {
            ((Document) invocation.getArgument(0)).put("_id", new ObjectId());
            return null;
        }).when(collection).insertOne(any(Document.class));

        repository.inserir(aluno);

        verify(collection).insertOne(argThat(doc ->
                !doc.containsKey("endereco") && doc.getList("responsaveis", Document.class).isEmpty()));
    }

    @Test
    void deveLerSubdocumentosAoBuscarAluno() {
        ObjectId objectId = new ObjectId();
        Document document = documentoDeAluno(objectId, "Maria", 10, "Matematica", "Tarde")
                .append("endereco", new Document("rua", "Rua A").append("bairro", "Centro")
                        .append("cidade", "Maringa").append("estado", "PR"))
                .append("responsaveis", List.of(
                        new Document("nome", "Ana").append("parentesco", "Mae").append("telefone", "44999991234")));
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(document);

        Aluno resultado = repository.buscarPorId(objectId.toHexString());

        assertEquals("Maringa", resultado.getEndereco().cidade());
        assertEquals(1, resultado.getResponsaveis().size());
        assertEquals("Mae", resultado.getResponsaveis().get(0).parentesco());
    }

    @Test
    void deveLerDocumentoAntigoSemCamposAninhados() {
        ObjectId objectId = new ObjectId();
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(documentoDeAluno(objectId, "Maria", 10, "Matematica", "Tarde"));

        Aluno resultado = repository.buscarPorId(objectId.toHexString());

        assertNull(resultado.getEndereco());
        assertTrue(resultado.getResponsaveis().isEmpty());
    }

    @Test
    void deveBuscarAlunoPorIdQuandoExiste() {
        ObjectId objectId = new ObjectId();
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(documentoDeAluno(objectId, "Maria", 10, "Matematica", "Tarde"));

        Aluno resultado = repository.buscarPorId(objectId.toHexString());

        assertEquals(objectId.toHexString(), resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals(10, resultado.getIdade());
        assertTrue(resultado.isAtivo());
    }

    @Test
    void deveRetornarNuloQuandoAlunoNaoExiste() {
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        assertNull(repository.buscarPorId(new ObjectId().toHexString()));
    }

    @Test
    void deveRejeitarIdQueNaoEUmObjectIdValido() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> repository.buscarPorId("id-invalido"));

        assertTrue(erro.getMessage().contains("id-invalido"));
        verifyNoInteractions(findIterable);
    }

    @Test
    void deveRejeitarIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> repository.remover(null));
    }

    @SuppressWarnings("unchecked")
    @Test
    void deveListarTodosOsAlunos() {
        Document doc1 = documentoDeAluno(new ObjectId(), "Maria", 10, "Matematica", "Tarde");
        Document doc2 = documentoDeAluno(new ObjectId(), "Joao", 12, "Portugues", "Manha");

        MongoCursor<Document> cursor = mock(MongoCursor.class);
        Iterator<Document> iterator = List.of(doc1, doc2).iterator();
        when(cursor.hasNext()).thenAnswer(inv -> iterator.hasNext());
        when(cursor.next()).thenAnswer(inv -> iterator.next());
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(cursor);

        List<Aluno> alunos = repository.listarTodos();

        assertEquals(2, alunos.size());
        assertEquals("Maria", alunos.get(0).getNome());
        assertEquals("Joao", alunos.get(1).getNome());
    }

    @Test
    void deveAtualizarAlunoSubstituindoODocumento() {
        Aluno aluno = new Aluno(new ObjectId().toHexString(), "Maria", 11, "Matematica", "Tarde", true);
        UpdateResult resultado = mock(UpdateResult.class);
        when(resultado.getMatchedCount()).thenReturn(1L);
        when(collection.replaceOne(any(Document.class), any(Document.class))).thenReturn(resultado);

        assertTrue(repository.atualizar(aluno));
    }

    @Test
    void deveRetornarFalsoQuandoNenhumDocumentoCorresponde() {
        Aluno aluno = new Aluno(new ObjectId().toHexString(), "Maria", 11, "Matematica", "Tarde", true);
        UpdateResult resultado = mock(UpdateResult.class);
        when(resultado.getMatchedCount()).thenReturn(0L);
        when(collection.replaceOne(any(Document.class), any(Document.class))).thenReturn(resultado);

        assertFalse(repository.atualizar(aluno));
    }

    @Test
    void deveRemoverAlunoComSucesso() {
        DeleteResult resultado = mock(DeleteResult.class);
        when(resultado.getDeletedCount()).thenReturn(1L);
        when(collection.deleteOne(any(Document.class))).thenReturn(resultado);

        assertTrue(repository.remover(new ObjectId().toHexString()));
    }

    @Test
    void deveRetornarFalsoQuandoNaoRemoveNenhumDocumento() {
        DeleteResult resultado = mock(DeleteResult.class);
        when(resultado.getDeletedCount()).thenReturn(0L);
        when(collection.deleteOne(any(Document.class))).thenReturn(resultado);

        assertFalse(repository.remover(new ObjectId().toHexString()));
    }
}
