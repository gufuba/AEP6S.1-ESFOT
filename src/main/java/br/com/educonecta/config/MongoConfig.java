package br.com.educonecta.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centraliza a criacao da conexao com o MongoDB local (Community Edition).
 * Compativel com a instancia padrao usada em aula (localhost:27017),
 * visualizavel via MongoDB Compass. A string de conexao e o nome do banco
 * podem ser sobrescritos via application.properties.
 */
@Configuration
public class MongoConfig {

    @Value("${educonecta.mongodb.uri:mongodb://localhost:27017}")
    private String connectionString;

    @Value("${educonecta.mongodb.database:educonecta}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }
}
