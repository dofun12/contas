package org.lemanoman.contas;

import java.util.Map;

public class Conta {
    private String usuario;
    private String lancamento;
    private Integer mes;
    private Integer ano;
    private Integer dia;
    private Double total;
    private Boolean pago;
    private String descricao;

    public Conta() {
    }

    public Conta(Map<String,Object> map) {
        this.usuario = (String) map.get("usuario");
        this.lancamento = (String)map.get("lancamento");
        this.mes = (Integer) map.get("mes");
        this.ano = (Integer) map.get("ano");
        this.dia = (Integer) map.get("dia");
        this.total = (Double) map.get("total");
        this.pago = (map.get("pago")!=null && ((Integer) map.get("pago")>0));
        this.descricao = (String)map.get("descricao");
    }

    public Conta(String usuario, String lancamento, Integer mes, Integer ano, Integer dia, Double total, Boolean pago) {
        this.usuario = usuario;
        this.lancamento = lancamento;
        this.mes = mes;
        this.ano = ano;
        this.dia = dia;
        this.total = total;
        this.pago = pago;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLancamento() {
        return lancamento;
    }

    public void setLancamento(String lancamento) {
        this.lancamento = lancamento;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
