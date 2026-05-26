package com.taylors.csc61204;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AppTest {

    @Test
    void main_runsWithoutThrowing() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
