package br.com.educonecta.config;

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
 * Cria os indices do MongoDB na inicializacao. Uma falha (ex.: banco fora do ar ou
 * dados antigos duplicados) apenas gera um aviso e nao impede a API de subir.
 */
@Configuration
public class MongoIndicesConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MongoIndicesConfig.class);

    @Bean
    public ApplicationRunner criarIndices(MongoDatabase database) {
        return args -> criarIndice(database, VoluntarioRepository.COLECAO,
                Indexes.ascending("email"), new IndexOptions().unique(true));
    }

    private void criarIndice(MongoDatabase database, String colecao, Bson chaves, IndexOptions opcoes) {
        try {
            database.getCollection(colecao).createIndex(chaves, opcoes);
        } catch (MongoException e) {
            LOG.warn("Nao foi possivel criar o indice {} na colecao {}: {}", chaves, colecao, e.getMessage());
        }
    }
}
