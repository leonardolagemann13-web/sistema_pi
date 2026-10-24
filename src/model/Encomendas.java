package model;

import java.time.LocalDate;

public class Encomendas {
    // Declaração dos atributos da classe
    private int idEncomenda;
    private Clientes cliente;
    private LocalDate dataPedido;
    private LocalDate dataPrevisao;
    private LocalDate dataPrevisaoPagamento;
    private String tipoEntrega;
    private String statusEntrega;
    private String statusPagamento;
    private double frete;

    // Getters e Setters

    // Getters

    public int getIdEncomenda() {
        return idEncomenda;
    }

    public Clientes getIdCliente() {
        return cliente;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public LocalDate getDataPrevisaoPagamento() {
        return dataPrevisaoPagamento;
    }

    public LocalDate getDataPrevisao() {
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

    public double getFrete() {
        return frete;
    }
    // Setters

    public void setIdEncomenda(int idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public void setDataPrevisao(LocalDate dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public void setDataPrevisaoPagamento(LocalDate dataPrevisaoPagamento) {
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

    public void setFrete(double frete) {
        this.frete = frete;
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