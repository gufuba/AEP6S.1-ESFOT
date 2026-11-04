package br.com.educonecta.config;

import br.com.educonecta.repository.AlunoRepository;
import br.com.educonecta.repository.TurmaRepository;
import br.com.educonecta.repository.VoluntarioRepository;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cria os indices do MongoDB na inicializacao: e-mail unico em voluntarios e indices
 * nos campos de referencia usados nas consultas de relacionamento. Uma falha (ex.: banco
 * fora do ar ou dados antigos duplicados) apenas gera um aviso e nao impede a API de subir.
 */
@Configuration
public class MongoIndicesConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MongoIndicesConfig.class);

    @Bean
    public ApplicationRunner criarIndices(MongoDatabase database) {
        return args -> {
            criarIndice(database, VoluntarioRepository.COLECAO, Indexes.ascending("email"),
                    new IndexOptions().unique(true));
            criarIndice(database, TurmaRepository.COLECAO, Indexes.ascending("voluntarioId"), new IndexOptions());
            criarIndice(database, AlunoRepository.COLECAO, Indexes.ascending("turmaId"), new IndexOptions());
        };
    }

    private void criarIndice(MongoDatabase database, String colecao, Bson chaves, IndexOptions opcoes) {
        try {
            database.getCollection(colecao).createIndex(chaves, opcoes);
        } catch (MongoException e) {
            LOG.warn("Nao foi possivel criar o indice {} na colecao {}: {}", chaves, colecao, e.getMessage());
        }
    }
}
