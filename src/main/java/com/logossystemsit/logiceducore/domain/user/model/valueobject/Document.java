package com.logossystemsit.logiceducore.domain.user.model.valueobject;

import java.util.Objects;

public class Document {

    private final DocumentType type;
    private final DocumentNumber number;

    public Document(DocumentType type, DocumentNumber number) {
        this.type = Objects.requireNonNull(type, "Document type is required");
        this.number = Objects.requireNonNull(number, "Document number is required");

        validateFormat();
    }

    private void validateFormat() {
        type.validate(number.getValue());
    }

    /* ---------- GETTERS ---------- */

    public DocumentType getType() {
        return type;
    }

    public DocumentNumber getNumber() {
        return number;
    }

    /* ---------- HELPERS PARA PERSISTENCIA ---------- */

    public String numberValue() {
        return number.getValue();
    }

    public String typeValue() {
        return type.name();
    }

    /* ---------- ENUM ---------- */

    public enum DocumentType {

        CC {
            @Override
            public void validate(String value) {
                if (!value.matches("^\\d{6,10}$")) {
                    throw new IllegalArgumentException("Invalid CC format");
                }
            }
        },

        TI {
            @Override
            public void validate(String value) {
                if (!value.matches("^\\d{6,10}$")) {
                    throw new IllegalArgumentException("Invalid TI format");
                }
            }
        },

        CE {
            @Override
            public void validate(String value) {
                if (!value.matches("^[A-Za-z0-9]{6,12}$")) {
                    throw new IllegalArgumentException("Invalid CE format");
                }
            }
        },

        RC {
            @Override
            public void validate(String value) {
                if (!value.matches("^\\d{8,12}$")) {
                    throw new IllegalArgumentException("Invalid RC format");
                }
            }
        },

        PASSPORT {
            @Override
            public void validate(String value) {
                // opcional
            }
        };

        public abstract void validate(String value);
    }
}
