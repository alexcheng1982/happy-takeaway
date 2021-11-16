local databases = ["restaurant", "order", "delivery"];
local config = {
  db: {
    username: "puser",
    password: "ppassword",
    database: "happy-takeaway"
  },
  pgadmin: {
    username: "admin@example.com",
    password: "pgadmin",
    port: 6555
  },
};

local kv = function(k, v) k + "=" + v;
local pgpass(host, database, username, password) =
  std.format("%s:5432:%s:%s:%s", [host, database, username, password]);

local templates = import "templates.libsonnet";

{
  "postgres-servers.json": std.manifestJsonEx({
      "Servers": {
        [std.toString(i + 1)]: {
          "Name": databases[i] + " service",
          "Group": "Happy-takeaway",
          "Host": databases[i] + "-postgres",
          "Port": 5432,
          "MaintenanceDB": config.db.database,
          "Username": config.db.username,
          "SSLMode": "prefer",
          "PassFile": "/%s-pgpassfile" % databases[i],
        } for i in std.range(0, std.length(databases) - 1)
      }
  }, "    "),
  ".env": std.lines([
    kv("POSTGRES_USERNAME", config.db.username),
    kv("POSTGRES_PASSWORD", config.db.password),
    kv("POSTGRES_DB", config.db.database),
    kv("PGADMIN_USER", config.pgadmin.username),
    kv("PGADMIN_PASSWORD", config.pgadmin.password),
    kv("PGADMIN_PORT", config.pgadmin.port)
  ]),
  "docker-compose.yaml": std.manifestYamlDoc({
    version: "3",
    services: {
      pgadmin: templates.DockerComposePgAdmin {
        databases: databases,
        adminUser: config.pgadmin.username,
      },
    } + {
      [databases[i] + "-postgres"]: templates.DockerComposePostgres {
        name: databases[i],
        index: i,
      } for i in std.range(0, std.length(databases) - 1)
    },
    volumes: {
      pgadmin: {
        driver: "local"
      },
    } + {
      [databases[i] + "-db"]: {
        driver: "local"
      }, for i in std.range(0, std.length(databases) - 1)
    },
  }, indent_array_in_object = true)
} + {
  [databases[i] + "-pgpassfile"]: pgpass(databases[i] + "-postgres", config.db.database, config.db.username, config.db.password) for i in std.range(0, std.length(databases) - 1)
}