package org.lemanoman.contas.dto;

public class Mes {
    private Integer codigo;
    private String nome;
    private boolean selected;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Mes() {
    }

    public Mes(Integer codigo, String nome, boolean selected) {
        this.codigo = codigo;
        this.nome = nome;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
