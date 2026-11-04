package br.com.educonecta.config;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoIndicesConfigTest {

    @Mock
    private MongoDatabase database;

    @Mock
    private MongoCollection<Document> voluntarios;

    @Mock
    private MongoCollection<Document> turmas;

    @Mock
    private MongoCollection<Document> alunos;

    @BeforeEach
    void setUp() {
        when(database.getCollection("voluntarios")).thenReturn(voluntarios);
        when(database.getCollection("turmas")).thenReturn(turmas);
        when(database.getCollection("alunos")).thenReturn(alunos);
    }

    private static Bson chave(String campo) {
        return new BsonDocument(campo, new BsonInt32(1));
    }

    private static boolean mesmaChave(Bson indice, String campo) {
        return chave(campo).equals(indice.toBsonDocument());
    }

    @Test
    void deveCriarIndiceUnicoDeEmailEIndicesNosCamposDeReferencia() throws Exception {
        new MongoIndicesConfig().criarIndices(database).run(null);

        verify(voluntarios).createIndex(argThat((Bson k) -> mesmaChave(k, "email")),
                argThat((IndexOptions o) -> o.isUnique()));
        verify(turmas).createIndex(argThat((Bson k) -> mesmaChave(k, "voluntarioId")),
                argThat((IndexOptions o) -> !o.isUnique()));
        verify(alunos).createIndex(argThat((Bson k) -> mesmaChave(k, "turmaId")),
                argThat((IndexOptions o) -> !o.isUnique()));
    }

    @Test
    void deveApenasAvisarEContinuarQuandoUmIndiceFalhar() {
        when(voluntarios.createIndex(any(Bson.class), any(IndexOptions.class)))
                .thenThrow(new MongoException("falha simulada"));

        assertDoesNotThrow(() -> new MongoIndicesConfig().criarIndices(database).run(null));

        verify(turmas).createIndex(any(Bson.class), any(IndexOptions.class));
        verify(alunos).createIndex(any(Bson.class), any(IndexOptions.class));
    }
}
