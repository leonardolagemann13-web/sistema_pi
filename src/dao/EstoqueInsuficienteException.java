package dao;

// Lançada quando uma encomenda não pode ser entregue por falta de produto no estoque
public class EstoqueInsuficienteException extends Exception {
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}