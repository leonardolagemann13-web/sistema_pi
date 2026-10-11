package model;

public class Pagamentos {
    // Declaração dos atributos
    private int idPagamento;
    private Encomendas idEncomenda;
    private double valorTotal;
    private String formaPagamento;
    private String dataPagamento;

    // Getters e Setters

    // Getters

    public int getIdPagamento() {
        return idPagamento;
    }

    public Encomendas getIdEncomenda() {
        return idEncomenda;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    // Setters

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public void setIdEncomenda(Encomendas idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    // Para mostrar no ComboBox
    @Override
    public String toString(){
       if (idEncomenda != null){
           return idEncomenda.getIdCliente().getNome();
       }
       return String.valueOf(idPagamento);
    }
}