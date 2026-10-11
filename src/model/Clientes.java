package model;

public class Clientes {
    // Declaração dos atributos da classe
    private int idCliente;
    private String nome;
    private String sobrenome;
    private String contato;
    private String endereco;

    // Getters e Setter

    // Getters
    public int getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getContato() {
        return contato;
    }

    public String getEndereco() {
        return endereco;
    }

    // Setters
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    // Exibe o nome no JComboBOX
    @Override
    public String toString(){
        return nome + " " + sobrenome;
    }
}