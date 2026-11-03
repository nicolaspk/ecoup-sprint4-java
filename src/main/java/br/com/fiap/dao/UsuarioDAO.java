package br.com.fiap.dao;

import br.com.fiap.to.UsuarioTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {
    public ArrayList<UsuarioTO> findAll() {
        ArrayList<UsuarioTO> usuarios = new ArrayList<>();
        String sql = "SELECT CODIGO, NOME, EMAIL FROM DDD_USUARIO ORDER BY CODIGO";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar usuarios", e);
        }
        return usuarios;
    }

    public UsuarioTO findByCodigo(Long codigo) {
        String sql = "SELECT CODIGO, NOME, EMAIL FROM DDD_USUARIO WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montarUsuario(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuario", e);
        }
        return null;
    }

    public UsuarioTO save(UsuarioTO usuario) {
        String sql = "INSERT INTO DDD_USUARIO (NOME, EMAIL) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"CODIGO"})) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) usuario.setCodigo(rs.getLong(1));
                }
                return usuario;
            }
        } catch (SQLException e) {
            // ORA-00001: e-mail ja cadastrado (restricao UNIQUE).
            if (e.getErrorCode() == 1) {
                throw new IllegalArgumentException("Email ja cadastrado");
            }
            throw new IllegalStateException("Erro ao cadastrar usuario", e);
        }
        return null;
    }

    public UsuarioTO update(UsuarioTO usuario) {
        String sql = "UPDATE DDD_USUARIO SET NOME = ?, EMAIL = ? WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setLong(3, usuario.getCodigo());
            if (ps.executeUpdate() > 0) return usuario;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                throw new IllegalArgumentException("Email ja cadastrado");
            }
            throw new IllegalStateException("Erro ao atualizar usuario", e);
        }
        return null;
    }

    public boolean delete(Long codigo) {
        String sql = "DELETE FROM DDD_USUARIO WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // ORA-02292: existem acoes relacionadas a este usuario.
            if (e.getErrorCode() == 2292) {
                throw new IllegalArgumentException("Usuario possui acoes cadastradas");
            }
            throw new IllegalStateException("Erro ao excluir usuario", e);
        }
    }

    private UsuarioTO montarUsuario(ResultSet rs) throws SQLException {
        return new UsuarioTO(rs.getLong("CODIGO"), rs.getString("NOME"), rs.getString("EMAIL"));
    }
}
