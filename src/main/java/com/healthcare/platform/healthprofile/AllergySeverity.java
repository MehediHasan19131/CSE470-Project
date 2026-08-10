package com.healthcare.platform.healthprofile;

/**
 * How serious a reaction the allergy causes. Stored on `allergies.severity`
 * as its plain {@code name()} (VARCHAR, same convention {@code UserRole} uses
 * on `users.role`) - not a MySQL ENUM type, just a CHECK constraint restricting
 * the column to these three values (see health-schema.sql).
 */
public enum AllergySeverity {
    MILD,
    MODERATE,
    SEVERE
}
