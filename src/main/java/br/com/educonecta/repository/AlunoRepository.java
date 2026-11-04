package br.com.educonecta.repository;

import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Endereco;
import br.com.educonecta.model.Responsavel;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso a colecao "alunos" do MongoDB. O endereco e guardado como subdocumento,
 * os responsaveis como uma lista de subdocumentos e a turma como referencia (ObjectId).
 */
@Repository
public class AlunoRepository extends MongoCrudRepository<Aluno> {

    public static final String COLECAO = "alunos";

    public AlunoRepository(MongoDatabase database) {
        super(database, COLECAO);
    }

    @Override
    protected Document toDocument(Aluno aluno) {
        Document document = new Document("nome", aluno.getNome())
                .append("idade", aluno.getIdade())
                .append("curso", aluno.getCurso())
                .append("turno", aluno.getTurno())
                .append("ativo", aluno.isAtivo());
        if (aluno.getEndereco() != null) {
            document.append("endereco", enderecoParaDocument(aluno.getEndereco()));
        }
        document.append("responsaveis", aluno.getResponsaveis().stream().map(this::responsavelParaDocument).toList());
        if (aluno.getTurmaId() != null) {
            document.append("turmaId", toObjectId(aluno.getTurmaId()));
        }
        return document;
    }

    public List<Aluno> listarPorTurma(String turmaId) {
        return buscarPor(new Document("turmaId", toObjectId(turmaId)));
    }

    public long contarPorTurma(String turmaId) {
        return contarPor(new Document("turmaId", toObjectId(turmaId)));
    }

    @Override
    protected Aluno toEntidade(Document document) {
        Aluno aluno = new Aluno(
                document.getObjectId("_id").toHexString(),
                document.getString("nome"),
                document.getInteger("idade"),
                document.getString("curso"),
                document.getString("turno"),
                Boolean.TRUE.equals(document.getBoolean("ativo"))
        );
        Document endereco = document.get("endereco", Document.class);
        if (endereco != null) {
            aluno.setEndereco(documentParaEndereco(endereco));
        }
        List<Document> responsaveis = document.getList("responsaveis", Document.class, List.of());
        aluno.setResponsaveis(responsaveis.stream().map(this::documentParaResponsavel).toList());
        ObjectId turmaId = document.getObjectId("turmaId");
        if (turmaId != null) {
            aluno.setTurmaId(turmaId.toHexString());
        }
        return aluno;
    }

    private Document enderecoParaDocument(Endereco endereco) {
        return new Document("rua", endereco.rua())
                .append("bairro", endereco.bairro())
                .append("cidade", endereco.cidade())
                .append("estado", endereco.estado());
    }

    private Endereco documentParaEndereco(Document document) {
        return new Endereco(
                document.getString("rua"),
                document.getString("bairro"),
                document.getString("cidade"),
                document.getString("estado"));
    }

    private Document responsavelParaDocument(Responsavel responsavel) {
        return new Document("nome", responsavel.nome())
                .append("parentesco", responsavel.parentesco())
                .append("telefone", responsavel.telefone());
    }

    private Responsavel documentParaResponsavel(Document document) {
        return new Responsavel(
                document.getString("nome"),
                document.getString("parentesco"),
                document.getString("telefone"));
    }
}
