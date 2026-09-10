package br.com.educonecta.repository;

import br.com.educonecta.model.Aluno;
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

    @Test
    void deveInserirAlunoEDefinirIdGerado() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");

        doAnswer(invocation -> {
            Document document = invocation.getArgument(0);
            document.put("_id", new ObjectId());
            return null;
        }).when(collection).insertOne(any(Document.class));

        Aluno resultado = repository.inserir(aluno);

        assertNotNull(resultado.getId());
        verify(collection).insertOne(any(Document.class));
    }

    @Test
    void deveBuscarAlunoPorIdQuandoExiste() {
        ObjectId objectId = new ObjectId();
        Document document = new Document("_id", objectId)
                .append("nome", "Maria")
                .append("idade", 10)
                .append("curso", "Matematica")
                .append("turno", "Tarde")
                .append("ativo", true);

        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(document);

        Aluno resultado = repository.buscarPorId(objectId.toHexString());

        assertEquals(objectId.toHexString(), resultado.getId());
        assertEquals("Maria", resultado.getNome());
    }

    @Test
    void deveRetornarNuloQuandoAlunoNaoExiste() {
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        Aluno resultado = repository.buscarPorId(new ObjectId().toHexString());

        assertNull(resultado);
    }

    @SuppressWarnings("unchecked")
    @Test
    void deveListarTodosOsAlunos() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Document doc1 = new Document("_id", id1)
                .append("nome", "Maria").append("idade", 10)
                .append("curso", "Matematica").append("turno", "Tarde").append("ativo", true);
        Document doc2 = new Document("_id", id2)
                .append("nome", "Joao").append("idade", 12)
                .append("curso", "Portugues").append("turno", "Manha").append("ativo", true);

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
    void deveAtualizarAlunoComSucesso() {
        Aluno aluno = new Aluno(new ObjectId().toHexString(), "Maria", 11, "Matematica", "Tarde", true);
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(1L);
        when(collection.updateOne(any(Document.class), any(Document.class))).thenReturn(updateResult);

        boolean atualizado = repository.atualizar(aluno);

        assertTrue(atualizado);
    }

    @Test
    void deveRetornarFalsoQuandoAtualizacaoNaoAlteraNada() {
        Aluno aluno = new Aluno(new ObjectId().toHexString(), "Maria", 11, "Matematica", "Tarde", true);
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(0L);
        when(collection.updateOne(any(Document.class), any(Document.class))).thenReturn(updateResult);

        boolean atualizado = repository.atualizar(aluno);

        assertFalse(atualizado);
    }

    @Test
    void deveRemoverAlunoComSucesso() {
        DeleteResult deleteResult = mock(DeleteResult.class);
        when(deleteResult.getDeletedCount()).thenReturn(1L);
        when(collection.deleteOne(any(Document.class))).thenReturn(deleteResult);

        boolean removido = repository.remover(new ObjectId().toHexString());

        assertTrue(removido);
    }

    @Test
    void deveRetornarFalsoQuandoNaoRemoveNenhumDocumento() {
        DeleteResult deleteResult = mock(DeleteResult.class);
        when(deleteResult.getDeletedCount()).thenReturn(0L);
        when(collection.deleteOne(any(Document.class))).thenReturn(deleteResult);

        boolean removido = repository.remover(new ObjectId().toHexString());

        assertFalse(removido);
    }
}
