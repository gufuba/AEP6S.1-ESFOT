package br.com.educonecta.repository;

import br.com.educonecta.model.Aluno;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Acesso a colecao "alunos" do MongoDB.
 */
@Repository
public class AlunoRepository extends MongoCrudRepository<Aluno> {

    public static final String COLECAO = "alunos";

    public AlunoRepository(MongoDatabase database) {
        super(database, COLECAO);
    }

    @Override
    protected Document toDocument(Aluno aluno) {
        return new Document("nome", aluno.getNome())
                .append("idade", aluno.getIdade())
                .append("curso", aluno.getCurso())
                .append("turno", aluno.getTurno())
                .append("ativo", aluno.isAtivo());
    }

    @Override
    protected Aluno toEntidade(Document document) {
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
