package com.raffs.LawInsight.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DevProfileDataSourceTest {

  @Autowired
  private DataSource dataSource;

  @Test
  void shouldUseH2WithExplicitMemoryDatabaseUrl() throws SQLException {
    try (var connection = dataSource.getConnection()) {
      var metaData = connection.getMetaData();
      assertThat(metaData.getURL())
          .as("Dev profile should configure H2 with explicit URL jdbc:h2:mem:lawinsight")
          .contains("jdbc:h2:mem:lawinsight");
    }
  }
}
