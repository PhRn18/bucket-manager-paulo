package com.project.bucketmanager.Models.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class EContentDispositionTest {
    @Test
    public void testGetByValue_WithValidValue() {
        String validValue = "attachment";
        EContentDisposition result = EContentDisposition.getByValue(validValue);
        assertThat(result).isEqualTo(EContentDisposition.ATTACHMENT);
    }

    @Test
    public void testGetByValue_WithNullValue() {
        String nullValue = null;
        EContentDisposition result = EContentDisposition.getByValue(nullValue);
        assertThat(result).isEqualTo(EContentDisposition.ATTACHMENT);
    }

    @Test
    public void testGetByValue_WithInvalidValue() {
        String invalidValue = "invalid";
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> EContentDisposition.getByValue(invalidValue))
                .withMessage("Invalid content disposition type: " + invalidValue);
    }

    @Test
    public void testGetValue_ReturnsCorrectValue() {
        EContentDisposition disposition = EContentDisposition.INLINE;
        String result = disposition.getValue();
        assertThat(result).isEqualTo("inline");
    }
}