package com.gimnasio.util;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static boolean validarPassword(String password) {
        if (password == null) {
            return false;
        }

        /*
         * Reglas de validación:
         * - Entre 8 y 30 caracteres
         * - Al menos una letra minúscula
         * - Al menos una letra mayúscula
         * - Al menos un número
         * - Carácter especial elegido: !
         */

        String patron = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*!).{8,30}$";

        return password.matches(patron);
    }
}