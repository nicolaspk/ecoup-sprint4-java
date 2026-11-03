package br.com.fiap.dao;

import br.com.fiap.to.AcaoTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AcaoDAO {
    public ArrayList<AcaoTO> findAll() {
        ArrayList<AcaoTO> acoes = new ArrayList<>();
        String sql = "SELECT * FROM DDD_ACAO_SUSTENTAVEL ORDER BY CODIGO";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) acoes.add(montarAcao(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar acoes", e);
        }
        return acoes;
    }

    public AcaoTO findByCodigo(Long codigo) {
        String sql = "SELECT * FROM DDD_ACAO_SUSTENTAVEL WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montarAcao(rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar acao", e);
        }
        return null;
    }

    public AcaoTO save(AcaoTO acao) {
        String sql = "INSERT INTO DDD_ACAO_SUSTENTAVEL " +
                "(USUARIO_CODIGO, TITULO, CATEGORIA, LINK_EVIDENCIA, DATA_REGISTRO, STATUS) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"CODIGO"})) {
            preencher(ps, acao);
            ps.setDate(5, Date.valueOf(acao.getDataRegistro()));
            ps.setString(6, acao.getStatus());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) acao.setCodigo(rs.getLong(1));
                }
                return acao;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao cadastrar acao", e);
        }
        return null;
    }

    public AcaoTO update(AcaoTO acao) {
        String sql = "UPDATE DDD_ACAO_SUSTENTAVEL SET USUARIO_CODIGO = ?, " +
                "TITULO = ?, CATEGORIA = ?, LINK_EVIDENCIA = ? WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, acao);
            ps.setLong(5, acao.getCodigo());
            if (ps.executeUpdate() > 0) return findByCodigo(acao.getCodigo());
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar acao", e);
        }
        return null;
    }

    public boolean delete(Long codigo) {
        String sql = "DELETE FROM DDD_ACAO_SUSTENTAVEL WHERE CODIGO = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir acao", e);
        }
    }

    private void preencher(PreparedStatement ps, AcaoTO acao) throws SQLException {
        ps.setLong(1, acao.getUsuarioCodigo());
        ps.setString(2, acao.getTitulo());
        ps.setString(3, acao.getCategoria());
        ps.setString(4, acao.getLinkEvidencia());
    }

    private AcaoTO montarAcao(ResultSet rs) throws SQLException {
        AcaoTO acao = new AcaoTO();
        acao.setCodigo(rs.getLong("CODIGO"));
        acao.setUsuarioCodigo(rs.getLong("USUARIO_CODIGO"));
        acao.setTitulo(rs.getString("TITULO"));
        acao.setCategoria(rs.getString("CATEGORIA"));
        acao.setLinkEvidencia(rs.getString("LINK_EVIDENCIA"));
        acao.setDataRegistro(rs.getDate("DATA_REGISTRO").toLocalDate());
        acao.setStatus(rs.getString("STATUS"));
        return acao;
    }
}
