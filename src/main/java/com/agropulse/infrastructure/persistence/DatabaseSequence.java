package com.agropulse.infrastructure.persistence;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document to store sequence counters for auto-incrementing IDs.
 * This collection is used to generate sequential IDs for User, Farm, and SensorReading documents.
 */
@Document(collection = "database_sequences")
public class DatabaseSequence {

    private String id;
    private Long seq;

    public DatabaseSequence() {
    }

    public DatabaseSequence(String id, Long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
}
