package mensajeria.controller;

import static mensajeria.util.Respuesta.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioController {
    private final Map<String, String> usuarios;
    private final Map<String, List<String>> buzones;

    public UsuarioController() {
        usuarios = new HashMap<>();
        buzones = new HashMap<>();
    }

    public String registrarUsuario(String username, String publicKey) {
        if (usuarios.containsKey(username)) {
            return String.format(USUARIO_YA_REGISTRADO, username);
        }
        usuarios.put(username, publicKey);
        buzones.put(username, new ArrayList<>());
        return String.format(REGISTRAR_OK, username);
    }

    public String obtenerLlavePublica(String username) {
        if (!usuarios.containsKey(username)) {
            return String.format(USUARIO_NO_REGISTRADO, username);
        }
        String publicKey = usuarios.get(username);
        return String.format(LLAVE_OK, username, publicKey);
    }

    public String enviarMensaje(String username, String message) {
        if (!usuarios.containsKey(username)) {
            return String.format(USUARIO_NO_REGISTRADO, username);
        }
        buzones.get(username).add(message);
        return String.format(MENSAJE_GUARDADO, username);
    }

    public String leerMensajes(String username) {
        if (!usuarios.containsKey(username)) {
            return String.format(USUARIO_NO_REGISTRADO, username);
        }
        return String.format(CANTIDAD_MENSAJES, username, buzones.get(username).size());
    }

    public List<String> obtenerBuzon(String username) {
        return buzones.get(username);
    }
}
