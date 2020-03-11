package org.lemanoman.contas.dto;

public class Ano {
    private Integer ano;
    private boolean selected;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Ano(Integer ano, boolean selected) {
        this.ano = ano;
        this.selected = selected;
    }

    public Ano() {
    }
}
