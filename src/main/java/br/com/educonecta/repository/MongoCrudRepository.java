package br.com.educonecta.repository;

import br.com.educonecta.model.Entidade;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio base: implementa o CRUD sobre uma colecao MongoDB. Cada subclasse
 * informa apenas o nome da colecao e como converter entidade e documento.
 */
public abstract class MongoCrudRepository<T extends Entidade> {

    private final MongoCollection<Document> collection;

    protected MongoCrudRepository(MongoDatabase database, String nomeColecao) {
        this.collection = database.getCollection(nomeColecao);
    }

    protected abstract Document toDocument(T entidade);

    protected abstract T toEntidade(Document document);

    public T inserir(T entidade) {
        Document document = toDocument(entidade);
        collection.insertOne(document);
        entidade.setId(document.getObjectId("_id").toHexString());
        return entidade;
    }

    public T buscarPorId(String id) {
        Document document = collection.find(new Document("_id", toObjectId(id))).first();
        return document == null ? null : toEntidade(document);
    }

    public List<T> listarTodos() {
        return converter(collection.find());
    }

    public boolean atualizar(T entidade) {
        Document filtro = new Document("_id", toObjectId(entidade.getId()));
        return collection.replaceOne(filtro, toDocument(entidade)).getMatchedCount() > 0;
    }

    public boolean remover(String id) {
        return collection.deleteOne(new Document("_id", toObjectId(id))).getDeletedCount() > 0;
    }

    protected List<T> buscarPor(Bson filtro) {
        return converter(collection.find(filtro));
    }

    protected boolean existePor(Bson filtro) {
        return collection.find(filtro).first() != null;
    }

    protected long contarPor(Bson filtro) {
        return collection.countDocuments(filtro);
    }

    protected static ObjectId toObjectId(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            throw new IllegalArgumentException(
                    "Id invalido: '" + id + "'. Informe um identificador de 24 caracteres hexadecimais.");
        }
        return new ObjectId(id);
    }

    private List<T> converter(Iterable<Document> documentos) {
        List<T> entidades = new ArrayList<>();
        for (Document document : documentos) {
            entidades.add(toEntidade(document));
        }
        return entidades;
    }
}
