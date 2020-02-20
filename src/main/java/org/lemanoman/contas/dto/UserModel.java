package org.lemanoman.contas.dto;

public class UserModel {
  private String username;
  private String password;
  private String[] roles;
  private String nome;

  public UserModel(String username, String password, String nome, String... roles) {
    this.username = username;
    this.password = password;
    this.roles = roles;
    this.nome = nome;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public UserModel(){}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }
}
