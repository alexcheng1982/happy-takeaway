{
  DockerComposePostgres: {
    local db = self,

    name:: error "Must override name",
    index:: error "Must override index",
    
    image: "debezium/postgres:12-alpine",
    environment: {
      POSTGRES_USER: "${POSTGRES_USERNAME}",
      POSTGRES_PASSWORD: "${POSTGRES_PASSWORD}",
      POSTGRES_DB: "${POSTGRES_DB}"
    },
    volumes: [
      db.name + "-db:/data/postgres"
    ],
    ports: [
      (6432 + db.index) + ":5432"
    ],
  },
  DockerComposePgAdmin: {
    local pgAdmin = self,
    adminUser:: error "Must override adminUser",
    databases:: error "Must override databases",
    image: "dpage/pgadmin4",
    environment: {
      PGADMIN_DEFAULT_EMAIL: "${PGADMIN_USER}",
      PGADMIN_DEFAULT_PASSWORD: "${PGADMIN_PASSWORD}"
    },
    volumes: [
      "pgadmin:/root/.pgadmin",
      "./db/postgres-servers.json:/pgadmin4/servers.json"
    ],
    ports: [
      "${PGADMIN_PORT:-6555}:80"
    ],
    depends_on: [db + "-postgres" for db in pgAdmin.databases],
  },
}