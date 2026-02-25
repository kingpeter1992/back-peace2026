package com.king.peace.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DataSourceFallbackConfig {

  @Bean
  public DataSource dataSource(Environment env) {

    // PRIMARY: lire les props Spring standard (tes props Render)
    String pUrl = env.getProperty("spring.datasource.url");
    String pUser = env.getProperty("spring.datasource.username");
    String pPass = env.getProperty("spring.datasource.password", "");
    String pDriver = env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");

    // FALLBACK: H2
    String fUrl = env.getProperty("app.datasource.fallback.url",
        "jdbc:h2:mem:peace;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    String fUser = env.getProperty("app.datasource.fallback.username", "sa");
    String fPass = env.getProperty("app.datasource.fallback.password", "");
    String fDriver = env.getProperty("app.datasource.fallback.driver-class-name", "org.h2.Driver");

    // Si config primaire incomplète => H2
    if (isBlank(pUrl) || isBlank(pUser)) {
      System.out.println("⚠️ Config PostgreSQL manquante. Fallback H2.");
      return buildDs(fUrl, fUser, fPass, fDriver, "FALLBACK-H2");
    }

    // Essayer Postgres
    HikariDataSource primary = buildDs(pUrl, pUser, pPass, pDriver, "PRIMARY-POSTGRES");
    if (canConnect(primary)) {
      System.out.println("✅ DB primaire OK (PostgreSQL)");
      return primary;
    }

    // Sinon fallback H2
    try { primary.close(); } catch (Exception ignored) {}
    System.out.println("⚠️ DB primaire indisponible. Fallback H2.");
    return buildDs(fUrl, fUser, fPass, fDriver, "FALLBACK-H2");
  }

  private HikariDataSource buildDs(String url, String user, String pass, String driver, String poolName) {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(url);
    cfg.setUsername(user);
    cfg.setPassword(pass);
    cfg.setDriverClassName(driver);

    cfg.setConnectionTimeout(3000);
    cfg.setValidationTimeout(2000);
    cfg.setInitializationFailTimeout(0);

    cfg.setMaximumPoolSize(10);
    cfg.setPoolName(poolName);
    return new HikariDataSource(cfg);
  }

  private boolean canConnect(DataSource ds) {
    try (Connection c = ds.getConnection()) {
      return c.isValid(2);
    } catch (Exception e) {
      System.out.println("❌ Connexion primaire KO: " + e.getMessage());
      return false;
    }
  }

  private boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}