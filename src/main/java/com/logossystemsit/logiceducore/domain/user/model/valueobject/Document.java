package com.logossystemsit.logiceducore.domain.user.model.valueobject;

public class Document {

    private final DocumentType type;
    private final DocumentNumber number;

    public Document(DocumentType type, DocumentNumber number) {
        if (type == null) {
            throw new IllegalArgumentException("Document type is required");
        }
        if (number == null) {
            throw new IllegalArgumentException("Document number is required");
        }

        validateByType(type, number);

        this.type = type;
        this.number = number;
    }

    private void validateByType(DocumentType type, DocumentNumber number) {

        String value = number.getValue();

        switch (type) {
            case CC, TI -> {
                if (!value.matches("^\\d{6,10}$")) {
                    throw new IllegalArgumentException("Invalid CC/TI format");
                }
            }
            case CE -> {
                if (!value.matches("^[A-Za-z0-9]{6,12}$")) {
                    throw new IllegalArgumentException("Invalid CE format");
                }
            }
            case RC -> {
                if (!value.matches("^\\d{8,12}$")) {
                    throw new IllegalArgumentException("Invalid RC format");
                }
            }
            default -> {
                // fallback
            }
        }
    }

    public void validateAgainstAge(int age) {
        if (type == DocumentType.TI && age >= 18) {
            throw new IllegalArgumentException("TI is only for minors");
        }

        if (type == DocumentType.CC && age < 18) {
            throw new IllegalArgumentException("CC requires legal age");
        }
    }

    public enum DocumentType {
        CC, // Cédula de ciudadanía
        CE, // Cédula de extranjería
        TI, // Tarjeta de identidad
        RC, // Registro Civil
        PASSPORT // opcional
    }

    public DocumentType getType() {
        return type;
    }

    public DocumentNumber getNumber() {
        return number;
    }
}
