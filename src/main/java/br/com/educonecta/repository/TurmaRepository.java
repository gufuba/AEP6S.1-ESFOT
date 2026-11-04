package br.com.educonecta.repository;

import br.com.educonecta.model.Turma;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Acesso a colecao "turmas" do MongoDB. O voluntario responsavel e guardado
 * como referencia (ObjectId) para a colecao "voluntarios".
 */
@Repository
public class TurmaRepository extends MongoCrudRepository<Turma> {

    public static final String COLECAO = "turmas";

    public TurmaRepository(MongoDatabase database) {
        super(database, COLECAO);
    }

    public boolean existePorVoluntario(String voluntarioId) {
        return existePor(new Document("voluntarioId", toObjectId(voluntarioId)));
    }

    @Override
    protected Document toDocument(Turma turma) {
        return new Document("nome", turma.getNome())
                .append("disciplina", turma.getDisciplina())
                .append("turno", turma.getTurno())
                .append("vagas", turma.getVagas())
                .append("voluntarioId", toObjectId(turma.getVoluntarioId()))
                .append("ativa", turma.isAtiva());
    }

    @Override
    protected Turma toEntidade(Document document) {
        return new Turma(
                document.getObjectId("_id").toHexString(),
                document.getString("nome"),
                document.getString("disciplina"),
                document.getString("turno"),
                document.getInteger("vagas"),
                document.getObjectId("voluntarioId").toHexString(),
                Boolean.TRUE.equals(document.getBoolean("ativa")));
    }
}
