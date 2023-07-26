/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.source.databricks.utils;

import static io.airbyte.integrations.source.databricks.utils.DatabricksConstants.DATABRICKS_CATALOG_JDBC_KEY;
import static io.airbyte.integrations.source.databricks.utils.DatabricksConstants.DATABRICKS_CATALOG_KEY;
import static io.airbyte.integrations.source.databricks.utils.DatabricksConstants.DATABRICKS_SCHEMA_JDBC_KEY;
import static io.airbyte.integrations.source.databricks.utils.DatabricksConstants.DATABRICKS_SCHEMA_KEY;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.json.Jsons;
import io.airbyte.db.jdbc.JdbcUtils;
import io.airbyte.integrations.source.databricks.DatabricksSourceConfig;

public abstract class DatabricksDatabaseUtil {

  protected static String buildConnectionProperty(JsonNode config, String key, String jdbcKey) {
    return config.has(key) ? jdbcKey + "=" + config.get(key).asText().toLowerCase() : "";
  }

  public static JsonNode buildJdbcConfig(final JsonNode config) {
    final DatabricksSourceConfig databricksConfig = DatabricksSourceConfig.get(config);

    final ImmutableMap.Builder<Object, Object> configBuilder = ImmutableMap.builder()
        .put(JdbcUtils.USERNAME_KEY, DatabricksConstants.DATABRICKS_USERNAME)
        .put(JdbcUtils.PASSWORD_KEY, databricksConfig.personalAccessToken())
        .put(JdbcUtils.JDBC_URL_KEY, databricksConfig.getDatabricksConnectionString());
//        .put(JdbcUtils.JDBC_URL_KEY, "jdbc:databricks://dbc-64a1ef0b-4c31.cloud.databricks.com:443/default;transportMode=http;ssl=1;AuthMech=3;httpPath=/sql/1.0/warehouses/2d051bfc43b59ef7;PWD=dapie1c02e3fc14b53a88143d3af547dc49f;ConnCatalog=main;ConnSchema=default;EnableArrow=0");

    StringBuilder connectionProperties = new StringBuilder();
    connectionProperties.append(buildConnectionProperty(config, DATABRICKS_CATALOG_KEY, DATABRICKS_CATALOG_JDBC_KEY));
    connectionProperties.append(buildConnectionProperty(config, DATABRICKS_SCHEMA_KEY, JdbcUtils.AMPERSAND + DATABRICKS_SCHEMA_JDBC_KEY));

    if (connectionProperties.length() > 0) {
      configBuilder.put(JdbcUtils.CONNECTION_PROPERTIES_KEY, connectionProperties.toString());
    }
    return Jsons.jsonNode(configBuilder.build());
  }
}
