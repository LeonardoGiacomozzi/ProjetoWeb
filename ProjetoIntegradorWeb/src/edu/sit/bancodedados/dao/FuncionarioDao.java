package edu.sit.bancodedados.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.sit.bancodedados.conexao.Conexao;
import edu.sit.bancodedados.conexao.ConexaoException;
import edu.sit.erros.dao.DaoException;
import edu.sit.erros.dao.EErrosDao;
import edu.sit.model.ECargo;
import edu.sit.model.Funcionario;

public class FuncionarioDao extends InstaladorDao implements IDao<Funcionario> {

	@Override
	public boolean criaTabela() throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			Statement st = conexao.createStatement();
			st.executeUpdate("CREATE TABLE IF NOT EXISTS Funcionario (" 
					+ "id INT NOT NULL AUTO_INCREMENT," 
					+ "Nome VARCHAR(45) NOT NULL," 
					+ "CPF VARCHAR(45) NOT NULL," 
					+ "Cargo VARCHAR(45) NOT NULL," 
					+ "Contato INT NOT NULL," 
					+ "PRIMARY KEY (id)," 
					+ "INDEX fk_Funcionario_Contato1_idx (Contato ASC))" 
					+ "ENGINE = InnoDB;");
			return true;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.CRIAR_TABELA, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}

	@Override
	public boolean excluiTabela() throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			Statement st = conexao.createStatement();
			st.execute("DROP TABLE Funcionario;");
			return true;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.EXCLUI_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}

	@Override
	public Funcionario consulta(Integer codigo) throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT * FROM Funcionario WHERE id = ?;");
			pst.setInt(1, codigo);
			ResultSet rs = pst.executeQuery();
			return rs.first() ? new Funcionario(rs.getInt("id"), rs.getString("Nome"), 
					rs.getString("CPF"), ECargo.values()[rs.getInt("Cargo")], rs.getInt("Contato")) : null;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.CONSULTA_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}
	
	public Funcionario consultaCompleta(Integer id) throws DaoException, ConexaoException {
		Funcionario funcionario = consulta(id);
		funcionario.setContato(new ContatoDao().consulta(funcionario.getContatoid()));
		return funcionario;
	}

	@Override
	public List<Funcionario> consultaTodos() throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		List<Funcionario> funcionarios = new ArrayList<Funcionario>();
		try {
			Statement st = conexao.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM Funcionario;");
			while (rs.next()) {
				funcionarios.add(new Funcionario(rs.getInt("id"), rs.getString("Nome"),
								rs.getString("CPF"), ECargo.values()[rs.getInt("Cargo")], rs.getInt("Contato")));
			}
			return funcionarios;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.CONSULTA_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}
	
	public List<Funcionario> consultaTodosCompleto() throws DaoException, ConexaoException {
		List<Funcionario> funcionariosCompleto = new ArrayList<>();
		List<Funcionario> funcionarios = consultaTodos();
		for (Funcionario funcionario : funcionarios) {
			funcionariosCompleto.add(consultaCompleta(funcionario.getId()));
		}
		return funcionariosCompleto;
	}

	@Override
	public List<Funcionario> consultaVariosPorID(Integer... codigos) throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		List<Funcionario> funcionario = new ArrayList<Funcionario>();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT * FROM Funcionario WHERE id = ?;");
			for (Integer codigo : codigos) {
				try {
					pst.setInt(1, codigo);
					ResultSet rs = pst.executeQuery();
					if (rs.first()) {
						funcionario.add(new Funcionario(rs.getInt("id"), rs.getString("Nome"),
								rs.getString("CPF"), ECargo.values()[rs.getInt("Cargo")], rs.getInt("Contato")));
					}
				} catch (Exception c) {
					new DaoException(EErrosDao.CONSULTA_DADO, c.getMessage(), this.getClass());
				}
			}
		} catch (Exception e) {
			throw new DaoException(EErrosDao.CONSULTA_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
		return funcionario;
	}
	
	public List<Funcionario> consultaFaixaCompleto(Integer... codigos) throws DaoException, ConexaoException {
		List<Funcionario> funcionariosCompleto = new ArrayList<>();
		List<Funcionario> funcionarios = consultaVariosPorID(codigos);
		for (Funcionario funcionario : funcionarios) {
			funcionariosCompleto.add(consultaCompleta(funcionario.getId()));
		}
		return funcionariosCompleto;
	}

	@Override
	public boolean insere(Funcionario objeto) throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement(
					"INSERT INTO Funcionario (Nome, CPF, Cargo, Contato) values (?, ?, ?, ?);");
			pst.setString(1, objeto.getNome());
			pst.setString(2, objeto.getCpf());
			pst.setInt(3, objeto.getCargo().ordinal());
			pst.setInt(4, objeto.getContatoid());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.INSERE_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}

	@Override
	public boolean altera(Funcionario objeto) throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement(
					"UPDATE Funcionario SET Nome = ?, CPF = ?, Cargo = ?  WHERE id = ?;");
			pst.setString(1, objeto.getNome());
			pst.setString(2, objeto.getCpf());
			pst.setInt(3, objeto.getCargo().ordinal());
			pst.setInt(4, objeto.getId());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.ALTERA_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}

	@Override
	public boolean exclui(Integer... codigos) throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("DELETE FROM Funcionario WHERE id = ?;");
			for (Integer novo : codigos) {
				pst.setInt(1, novo);
				pst.execute();
			}
		} catch (Exception e) {
			throw new DaoException(EErrosDao.EXCLUI_DADO, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
		return true;
	}

	public Integer pegaUltimoID() throws DaoException, ConexaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			Statement st = conexao.createStatement();
			ResultSet rs = st.executeQuery("SELECT MAX(id) FROM Funcionario;");
			return rs.first() ? rs.getInt(1) : 0;
		} catch (Exception e) {
			throw new DaoException(EErrosDao.PEGA_ID, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}

	public Integer consultaPorCpf(String cpf) throws ConexaoException, DaoException {
		Connection conexao = Conexao.abreConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT * FROM funcionario WHERE CPF like ?");
			pst.setString(1, cpf);
			ResultSet rs = pst.executeQuery();
			return rs.first() ? rs.getInt("id") : 0;
		}catch (Exception e) {
			throw new DaoException(EErrosDao.PEGA_ID, e.getMessage(), this.getClass());
		} finally {
			Conexao.fechaConexao();
		}
	}	
}
