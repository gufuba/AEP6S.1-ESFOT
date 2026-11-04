package br.com.educonecta.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoConfigTest {

    @Test
    void deveCriarClienteEBancoComOsValoresConfigurados() {
        MongoConfig config = new MongoConfig();
        ReflectionTestUtils.setField(config, "connectionString", "mongodb://localhost:1/?serverSelectionTimeoutMS=100");
        ReflectionTestUtils.setField(config, "databaseName", "banco_de_teste");

        try (MongoClient client = config.mongoClient()) {
            assertNotNull(client);
            MongoDatabase database = config.mongoDatabase(client);
            assertEquals("banco_de_teste", database.getName());
        }
    }
}
