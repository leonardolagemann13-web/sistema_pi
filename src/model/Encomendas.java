package model;

public class Encomendas {
    // Declaração dos atributos da classe
    private int idEncomenda;
    private Clientes cliente;
    private String dataPedido;
    private String dataPrevisao;
    private String dataPrevisaoPagamento;
    private String tipoEntrega;
    private String statusEntrega;
    private String statusPagamento;

    // Getters e Setters

    // Getters

    public int getIdEncomenda() {
        return idEncomenda;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public String getDataPrevisaoPagamento() {
        return dataPrevisaoPagamento;
    }

    public String getDataPrevisao() {
        return dataPrevisao;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public String getStatusEntrega() {
        return statusEntrega;
    }

    // Setters

    public void setIdEncomenda(int idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public void setDataPrevisao(String dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public void setDataPrevisaoPagamento(String dataPrevisaoPagamento) {
        this.dataPrevisaoPagamento = dataPrevisaoPagamento;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public void setStatusEntrega(String statusEntrega) {
        this.statusEntrega = statusEntrega;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    // Para aparecer no ComboBox
    @Override
    public String toString(){
        if (cliente != null){
            return cliente.getNome();
        }
        return String.valueOf(idEncomenda);
    }
}