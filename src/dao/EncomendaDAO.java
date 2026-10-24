package dao;
import connection.ConnectionFactory;
import model.Clientes;
import model.Encomendas;
import model.Estoque;
import model.Produtos;

import java.security.KeyStore;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class EncomendaDAO {
    // Metodo CREATE encomenda
    public void salvar(Encomendas encomenda){
        String sql = "INSERT INTO encomendas(idCliente,dataPrevisao,dataPrevisaoPagamento,tipoEntrega,statusEntrega,statusPagamento,frete" +
                "VALUES (?,?,?,?,?,?,?)";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,encomenda.getIdCliente().getIdCliente());
            ps.setDate(2, Date.valueOf(encomenda.getDataPrevisao()));
            ps.setDate(3, Date.valueOf(encomenda.getDataPrevisaoPagamento()));
            ps.setString(4,encomenda.getTipoEntrega());
            ps.setString(5,"A Entregar");
            ps.setString(6,"A pagar");
            ps.setDouble(7,encomenda.getFrete());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo READ encomenda
    public List<Encomendas> pesquisar(String nomeCompleto){
        List<Encomendas> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEncomenda, c.nome, c.sobrenome, e.dataPedido, e.dataPrevisao, e.dataPrevisaoPagamento, e.tipoEntrega, e.statusEntrega, e.statusPagamento, e.frete" +
                "FROM encomendas e " +
                "INNER JOIN clientes c ON c.idCliente = e.idCliente" +
                "WHERE CONCAT(c.nome, ' ', c.sobrenome) LIKE ? ";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setString(1,nomeCompleto + "%");
            ps.setString(2,nomeCompleto + "%");
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Encomendas encomenda = new Encomendas();
                    Clientes cliente = new Clientes();
                    cliente.setNome(rs.getString("nome"));
                    cliente.setSobrenome(rs.getString("sobrenome"));
                    encomenda.setCliente(cliente);
                    encomenda.setDataPedido(rs.getObject("dataPedido", java.time.LocalDate.class));
                    encomenda.setDataPrevisao(rs.getObject("dataPrevisao", java.time.LocalDate.class));
                    encomenda.setDataPrevisaoPagamento(rs.getObject("dataPrevisaoPagamento", java.time.LocalDate.class));
                    encomenda.setTipoEntrega(rs.getString("tipoEntrega"));
                    encomenda.setStatusEntrega(rs.getString("statusEntrega"));
                    encomenda.setStatusPagamento(rs.getString("statusPagamento"));
                    encomenda.setFrete(rs.getDouble("frete"));
                    lista.add(encomenda);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
    // Metodo UPDATE encomenda
    public void atualizar(Encomendas encomenda){
        String sql = "UPDATE encomendas SET idCliente=?,dataPrevisao=?,dataPrevisaoPagamento=?,tipoEntrega=?,statusEntrega=?,frete=? WHERE idEncomenda=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,encomenda.getIdCliente().getIdCliente());
            ps.setDate(2,Date.valueOf(encomenda.getDataPrevisao()));
            ps.setDate(3,Date.valueOf(encomenda.getDataPrevisaoPagamento()));
            ps.setString(4,encomenda.getTipoEntrega());
            ps.setString(5,encomenda.getStatusEntrega());
            ps.setDouble(6,encomenda.getFrete());
            ps.setInt(7,encomenda.getIdEncomenda());
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo DELETE encomenda
    public void excluir(int id){
        String sql = "DELETE FROM encomendas WHERE idEncomenda=?";
        try(
            Connection conexao = ConnectionFactory.getConnection();
            PreparedStatement ps = conexao.prepareStatement(sql)
        ){
            ps.setInt(1,id);
            ps.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Metodo listar encomendas a entregar
    public List<Encomendas> listar(){
        List<Encomendas> lista = new ArrayList<>();
        String sql =
                "SELECT e.idEncomenda, c.nome, c.sobrenome, e.dataPedido, e.dataPrevisao, e.dataPrevisaoPagamento, e.tipoEntrega, e.statusEntrega, e.statusPagamento, e.frete" +
                        "FROM encomendas e " +
                        "INNER JOIN clientes c ON c.idCliente = e.idCliente" +
                        "ORDER BY c.nome OR c.sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Clientes cliente = new Clientes();
                Encomendas encomenda = new Encomendas();
                encomenda.setIdEncomenda(rs.getInt("idEncomenda"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                encomenda.setDataPrevisao(rs.getObject("dataPrevisao",java.time.LocalDate.class));
                encomenda.setDataPrevisaoPagamento(rs.getObject("dataPrevisaoPagamento", java.time.LocalDate.class));
                encomenda.setTipoEntrega(rs.getString("tipoEntrega"));
                encomenda.setStatusEntrega(rs.getString("statusEntrega"));
                encomenda.setStatusPagamento(rs.getString("statusPagamento"));
                encomenda.setFrete(rs.getDouble("frete"));
                encomenda.setCliente(cliente);
                lista.add(encomenda);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo listar encomendas a entregar
    public List<Encomendas> listar2(){
        List<Encomendas> lista = new ArrayList<>();
        String sql =
                    "SELECT c.nome, c.sobrenome, e.statusEntrega, e.statusPagamento" +
                    "FROM encomendas e " +
                    "INNER JOIN clientes c ON c.idCliente = e.idCliente" +
                    "ORDER BY c.nome OR c.sobrenome";

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ){
            while (rs.next()){
                Clientes cliente = new Clientes();
                Encomendas encomenda = new Encomendas();
                cliente.setNome(rs.getString("nome"));
                cliente.setSobrenome(rs.getString("sobrenome"));
                encomenda.setStatusEntrega(rs.getString("statusEntrega"));
                encomenda.setStatusPagamento(rs.getString("statusPagamento"));
                encomenda.setCliente(cliente);
                lista.add(encomenda);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }
}