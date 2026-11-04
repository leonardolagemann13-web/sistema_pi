package model;

import java.time.LocalDate;

public class Relatorio {

    private int idRelatorio;
    private String periodo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorArrecadado;
    private double despesas;
    private double valorTotalArrecadado;

    public Relatorio() {
    }

    public int getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(int idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public double getValorArrecadado() {
        return valorArrecadado;
    }

    public void setValorArrecadado(double valorArrecadado) {
        this.valorArrecadado = valorArrecadado;
    }

    public double getDespesas() {
        return despesas;
    }

    public void setDespesas(double despesas) {
        this.despesas = despesas;
    }

    public double getValorTotalArrecadado() {
        return valorTotalArrecadado;
    }

    public void setValorTotalArrecadado(double valorTotalArrecadado) {
        this.valorTotalArrecadado = valorTotalArrecadado;
    }
}