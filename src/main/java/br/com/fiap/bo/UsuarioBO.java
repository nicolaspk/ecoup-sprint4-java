package br.com.fiap.bo;

import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.to.UsuarioTO;
import java.util.ArrayList;

public class UsuarioBO {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public ArrayList<UsuarioTO> findAll() { return usuarioDAO.findAll(); }
    public UsuarioTO findByCodigo(Long codigo) { return usuarioDAO.findByCodigo(codigo); }
    public UsuarioTO save(UsuarioTO usuario) { return usuarioDAO.save(usuario); }
    public UsuarioTO update(UsuarioTO usuario) { return usuarioDAO.update(usuario); }
    public boolean delete(Long codigo) { return usuarioDAO.delete(codigo); }
}
