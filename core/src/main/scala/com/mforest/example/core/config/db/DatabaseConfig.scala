package com.mforest.example.core.config.db

final case class DatabaseConfig(postgres: PostgresConfig, redis: RedisConfig, migration: MigrationConfig)
