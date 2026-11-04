package br.com.educonecta.repository;

import br.com.educonecta.model.Voluntario;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoluntarioRepositoryTest {

    @Mock
    private MongoDatabase database;

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private FindIterable<Document> findIterable;

    private VoluntarioRepository repository;

    @BeforeEach
    void setUp() {
        when(database.getCollection("voluntarios")).thenReturn(collection);
        repository = new VoluntarioRepository(database);
    }

    @Test
    void deveGravarDisciplinasComoArrayNoDocumento() {
        Voluntario voluntario = new Voluntario("Carlos", "carlos@email.com", List.of("Matematica", "Fisica"));
        ObjectId idGerado = new ObjectId();
        doAnswer(invocation -> {
            ((Document) invocation.getArgument(0)).put("_id", idGerado);
            return null;
        }).when(collection).insertOne(any(Document.class));

        Voluntario salvo = repository.inserir(voluntario);

        assertEquals(idGerado.toHexString(), salvo.getId());
        verify(collection).insertOne(argThat(doc ->
                "Carlos".equals(doc.getString("nome"))
                        && "carlos@email.com".equals(doc.getString("email"))
                        && List.of("Matematica", "Fisica").equals(doc.getList("disciplinas", String.class))
                        && Boolean.TRUE.equals(doc.getBoolean("ativo"))));
    }

    @Test
    void deveLerVoluntarioDoDocumento() {
        ObjectId id = new ObjectId();
        Document document = new Document("_id", id)
                .append("nome", "Carlos").append("email", "carlos@email.com")
                .append("disciplinas", List.of("Matematica")).append("ativo", false);
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(document);

        Voluntario voluntario = repository.buscarPorId(id.toHexString());

        assertEquals(id.toHexString(), voluntario.getId());
        assertEquals(List.of("Matematica"), voluntario.getDisciplinas());
        assertFalse(voluntario.isAtivo());
    }

    @Test
    void deveConsultarExistenciaPorEmail() {
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(new Document("email", "carlos@email.com")).thenReturn(null);

        assertTrue(repository.existePorEmail("carlos@email.com"));
        assertFalse(repository.existePorEmail("outro@email.com"));
        verify(collection, times(2)).find(argThat((Document filtro) -> filtro.containsKey("email")));
    }
}
