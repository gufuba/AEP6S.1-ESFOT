package br.com.educonecta.repository;

import br.com.educonecta.model.Aluno;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de acesso a dados. Encapsula a colecao "alunos" no MongoDB
 * e converte entre {@link Aluno} e {@link Document}.
 */
@Repository
public class AlunoRepository {

    private static final String COLLECTION_NAME = "alunos";

    private final MongoCollection<Document> collection;

    public AlunoRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public Aluno inserir(Aluno aluno) {
        Document document = toDocument(aluno);
        collection.insertOne(document);
        aluno.setId(document.getObjectId("_id").toHexString());
        return aluno;
    }

    public Aluno buscarPorId(String id) {
        Document document = collection.find(new Document("_id", new ObjectId(id))).first();
        return document == null ? null : toAluno(document);
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = new ArrayList<>();
        for (Document document : collection.find()) {
            alunos.add(toAluno(document));
        }
        return alunos;
    }

    public boolean atualizar(Aluno aluno) {
        Document filtro = new Document("_id", new ObjectId(aluno.getId()));
        Document atualizacao = new Document("$set", toDocument(aluno));
        return collection.updateOne(filtro, atualizacao).getModifiedCount() > 0;
    }

    public boolean remover(String id) {
        DeleteResult resultado = collection.deleteOne(new Document("_id", new ObjectId(id)));
        return resultado.getDeletedCount() > 0;
    }

    private Document toDocument(Aluno aluno) {
        return new Document("nome", aluno.getNome())
                .append("idade", aluno.getIdade())
                .append("curso", aluno.getCurso())
                .append("turno", aluno.getTurno())
                .append("ativo", aluno.isAtivo());
    }

    private Aluno toAluno(Document document) {
        return new Aluno(
                document.getObjectId("_id").toHexString(),
                document.getString("nome"),
                document.getInteger("idade"),
                document.getString("curso"),
                document.getString("turno"),
                Boolean.TRUE.equals(document.getBoolean("ativo"))
        );
    }
}
