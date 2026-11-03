package br.com.fiap.bo;

import br.com.fiap.dao.AcaoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.to.AcaoTO;
import java.time.LocalDate;
import java.util.ArrayList;

public class AcaoBO {
    private AcaoDAO acaoDAO = new AcaoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public ArrayList<AcaoTO> findAll() { return acaoDAO.findAll(); }
    public AcaoTO findByCodigo(Long codigo) { return acaoDAO.findByCodigo(codigo); }

    public AcaoTO save(AcaoTO acao) {
        validarAcao(acao);
        // O registro e confirmado diretamente, sem etapa de validacao.
        acao.setDataRegistro(LocalDate.now());
        acao.setStatus("REGISTRADA");
        return acaoDAO.save(acao);
    }

    public AcaoTO update(AcaoTO acao) {
        validarAcao(acao);
        return acaoDAO.update(acao);
    }

    public boolean delete(Long codigo) { return acaoDAO.delete(codigo); }

    private void validarAcao(AcaoTO acao) {
        if (usuarioDAO.findByCodigo(acao.getUsuarioCodigo()) == null) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        String categoria = acao.getCategoria();
        if (!categoria.equalsIgnoreCase("Mobilidade") &&
            !categoria.equalsIgnoreCase("Reciclagem") &&
            !categoria.equalsIgnoreCase("Energia")) {
            throw new IllegalArgumentException("Categoria: Mobilidade, Reciclagem ou Energia");
        }
    }
}
