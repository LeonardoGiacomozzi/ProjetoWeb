package edu.sit.installer.populador;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.sit.DataObject.ProdutoQuantidade;
import edu.sit.bancodedados.conexao.ConexaoException;
import edu.sit.bancodedados.dao.CategoriaDao;
import edu.sit.bancodedados.dao.ClienteDao;
import edu.sit.bancodedados.dao.ContatoDao;
import edu.sit.bancodedados.dao.FornecedorDao;
import edu.sit.bancodedados.dao.FuncionarioDao;
import edu.sit.bancodedados.dao.ProdutoDao;
import edu.sit.bancodedados.dao.UsuarioDao;
import edu.sit.bancodedados.dao.VendaDao;
import edu.sit.erro.instalacao.EErroInstalacao;
import edu.sit.erro.instalacao.InstalacaoException;
import edu.sit.erros.dao.DaoException;
import edu.sit.model.Categoria;
import edu.sit.model.Cliente;
import edu.sit.model.Contato;
import edu.sit.model.ECargo;
import edu.sit.model.Fornecedor;
import edu.sit.model.Funcionario;
import edu.sit.model.Produto;
import edu.sit.model.Usuario;
import edu.sit.model.Venda;
import edu.sit.uteis.Arquivo;

public class Populador {

	public static boolean cliente() throws InstalacaoException {

		try {
			List<String> clientesTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/clientes.txt");
			for (String cliente : clientesTxt) {
				String[] dados = cliente.split(";");
				new ContatoDao().insere(new Contato(dados[4], dados[5]));
				new ClienteDao().insere(new Cliente(dados[0],
						LocalDate.parse(dados[1], DateTimeFormatter.ofPattern("dd/MM/yyyy")), dados[2], dados[3],
						new ContatoDao().pegaUltimoID()));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_CLIENTES);
		}

	}

	public static boolean funcionario() throws InstalacaoException {

		try {
			List<String> funcionarioTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/funcionario.txt");
			for (String funcionario : funcionarioTxt) {
				String[] dados = funcionario.split(";");
				new ContatoDao().insere(new Contato(dados[3], dados[4]));
				new FuncionarioDao().insere(new Funcionario(dados[0], dados[1],
						ECargo.values()[Integer.parseInt(dados[2])], new ContatoDao().pegaUltimoID()));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_FUNCIONARIO);
		}

	}

	public static boolean fornecedor() throws InstalacaoException {

		try {
			List<String> fornededorTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/fornecedor.txt");
			for (String fornecedor : fornededorTxt) {
				String[] dados = fornecedor.split(";");
				new ContatoDao().insere(new Contato(dados[3], dados[4]));
				new FornecedorDao().insere(
						new Fornecedor(dados[0], dados[1], dados[2], new ContatoDao().pegaUltimoID()));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_FORNECEDOR);
		}

	}

	public static boolean categoria() throws InstalacaoException {

		try {
			List<String> categoriaTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/categoria.txt");
			for (String categoria : categoriaTxt) {
				String[] dados = categoria.split(";");
				new CategoriaDao().insere(new Categoria(dados[0]));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_CATEGORIA);
		}
	}

	public static boolean produto() throws InstalacaoException {

		try {
			List<String> produtoTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/produto.txt");
			for (String produto : produtoTxt) {
				String[] dados = produto.split(";");
				new ProdutoDao().insere(new Produto(dados[0], Integer.parseInt(dados[1]),
						Integer.parseInt(dados[2]), Integer.parseInt(dados[3]), Double.parseDouble(dados[4])));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_CATEGORIA);
		}

	}

	public static boolean venda() throws InstalacaoException {

		try {
			List<String> vendaTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/venda.txt");
			for (String venda : vendaTxt ) {
				String[] dados = venda.split(";");
				ArrayList<ProdutoQuantidade> produtosVenda = new ArrayList<ProdutoQuantidade>();
						for (String pQ : dados[2].split("#")) {
							String[] produtoQ = pQ.split("&");
							produtosVenda.add(new ProdutoQuantidade(new ProdutoDao().consulta(Integer.parseInt(produtoQ[0]))
									,Integer.parseInt(produtoQ[1])));
							
						}
				new VendaDao().insere(new Venda(Integer.parseInt(dados[0]),Integer.parseInt(dados[1]),produtosVenda,
						Double.parseDouble(dados[3]),LocalDate.parse(dados[4],DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_VENDA);
		}

	}

	public static boolean usuario() throws InstalacaoException {
		try {
			List<String> usuarioTxt = (List<String>) Arquivo
					.leArquivo(System.getProperty("user.dir") + "/populador/usuario.txt");
			for (String usuario : usuarioTxt) {
				String[] dados = usuario.split(";");
				new UsuarioDao().insere(new Usuario(dados[0],dados[1]));
			}
			return true;
		} catch (DaoException | ConexaoException | IOException e) {
			System.out.println(e.getMessage());
			throw new InstalacaoException(EErroInstalacao.ERRO_POPULAR_USUARIO);
		}
		
	}
	
}
