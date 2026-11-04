package br.com.educonecta.repository;

import br.com.educonecta.model.Turma;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurmaRepositoryTest {

    @Mock
    private MongoDatabase database;

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private FindIterable<Document> findIterable;

    private TurmaRepository repository;

    @BeforeEach
    void setUp() {
        when(database.getCollection("turmas")).thenReturn(collection);
        repository = new TurmaRepository(database);
    }

    @Test
    void deveGravarVoluntarioComoReferenciaObjectId() {
        ObjectId voluntarioId = new ObjectId();
        Turma turma = new Turma("Turma A", "Matematica", "Tarde", 15, voluntarioId.toHexString());
        doAnswer(invocation -> {
            ((Document) invocation.getArgument(0)).put("_id", new ObjectId());
            return null;
        }).when(collection).insertOne(any(Document.class));

        repository.inserir(turma);

        verify(collection).insertOne(argThat(doc ->
                voluntarioId.equals(doc.get("voluntarioId"))
                        && "Matematica".equals(doc.getString("disciplina"))
                        && Integer.valueOf(15).equals(doc.getInteger("vagas"))
                        && Boolean.TRUE.equals(doc.getBoolean("ativa"))));
    }

    @Test
    void deveLerTurmaDoDocumento() {
        ObjectId id = new ObjectId();
        ObjectId voluntarioId = new ObjectId();
        Document document = new Document("_id", id)
                .append("nome", "Turma A").append("disciplina", "Matematica").append("turno", "Tarde")
                .append("vagas", 15).append("voluntarioId", voluntarioId).append("ativa", false);
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(document);

        Turma turma = repository.buscarPorId(id.toHexString());

        assertEquals(id.toHexString(), turma.getId());
        assertEquals(voluntarioId.toHexString(), turma.getVoluntarioId());
        assertEquals(15, turma.getVagas());
        assertFalse(turma.isAtiva());
    }

    @Test
    void deveConsultarSeVoluntarioPossuiTurmas() {
        ObjectId voluntarioId = new ObjectId();
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(new Document()).thenReturn(null);

        assertTrue(repository.existePorVoluntario(voluntarioId.toHexString()));
        assertFalse(repository.existePorVoluntario(voluntarioId.toHexString()));
        verify(collection, times(2)).find(argThat((Document filtro) -> voluntarioId.equals(filtro.get("voluntarioId"))));
    }

    @Test
    void deveRejeitarIdDeVoluntarioInvalidoNaConsulta() {
        assertThrows(IllegalArgumentException.class, () -> repository.existePorVoluntario("invalido"));
    }
}
