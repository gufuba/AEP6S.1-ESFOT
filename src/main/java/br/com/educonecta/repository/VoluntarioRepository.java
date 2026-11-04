package br.com.educonecta.repository;

import br.com.educonecta.model.Voluntario;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso a colecao "voluntarios" do MongoDB. As disciplinas sao guardadas
 * como um array de textos dentro do documento.
 */
@Repository
public class VoluntarioRepository extends MongoCrudRepository<Voluntario> {

    public static final String COLECAO = "voluntarios";

    public VoluntarioRepository(MongoDatabase database) {
        super(database, COLECAO);
    }

    public boolean existePorEmail(String email) {
        return existePor(new Document("email", email));
    }

    @Override
    protected Document toDocument(Voluntario voluntario) {
        return new Document("nome", voluntario.getNome())
                .append("email", voluntario.getEmail())
                .append("disciplinas", voluntario.getDisciplinas())
                .append("ativo", voluntario.isAtivo());
    }

    @Override
    protected Voluntario toEntidade(Document document) {
        return new Voluntario(
                document.getObjectId("_id").toHexString(),
                document.getString("nome"),
                document.getString("email"),
                document.getList("disciplinas", String.class, List.of()),
                Boolean.TRUE.equals(document.getBoolean("ativo")));
    }
}
