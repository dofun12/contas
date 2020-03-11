package org.lemanoman.contas.service;


import org.lemanoman.contas.dto.TimePeriod;
import org.lemanoman.contas.dto.UserModel;
import org.lemanoman.contas.dto.Conta;
import org.lemanoman.contas.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;


@Service
public class DatabaseService {
    @Value("${database.location}")
    private String dataBaseLocation;

    @Value("${database.forceRestore}")
    private Boolean forceRestore;

    public void resetDatabase(boolean forceReset) {
        File db = new File(dataBaseLocation);
        if (!db.exists() || forceReset || forceRestore) {
            Database database = new Database(dataBaseLocation);
            database.dropAndCreate();
            database.close();
        }
    }

    public List<Integer> getListAnos() {
        UserModel userModel = new UserModel();
        Database database = new Database(dataBaseLocation);
        List<Map<String, Object>> list = database.doSelect("select ano as anos from conta group by ano");
        List<Integer> listAnos = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for(Map<String,Object> map: list){
                listAnos.add((Integer) map.get("anos"));
            }
        }
        database.close();
        return listAnos;
    }


    public UserModel getUsuario(String usuario) {
        UserModel userModel = new UserModel();
        Database database = new Database(dataBaseLocation);
        List<Map<String, Object>> list = database.doSelect("select usuario,nome,senha,dataCriado from usuarios where usuario = '" + usuario + "'");
        if (list != null && list.size() > 0) {
            Map<String, Object> map = list.get(0);
            userModel = new UserModel(
                    (String) map.get("usuario"),
                    (String) map.get("senha"),
                    (String) map.get("nome"),
                    "ADMIN"
            );
        }
        database.close();
        return userModel;
    }

    public List<Conta> getListContasMesAtual(String usuario) {
        List<Conta> listConta = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        Database database = new Database(dataBaseLocation);
        List<Map<String, Object>> list = database.doSelect(
                "select * from conta \n" +
                        " where " +
                        " usuario = '" + usuario + "' \n" +
                        " and mes = " + (calendar.get(Calendar.MONTH) + 1) + " and ano = " + calendar.get(Calendar.YEAR)+ " order by dia");
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                listConta.add(new Conta(map));
            }

        }
        database.close();
        return listConta;
    }

    public List<Conta> getListContas(String usuario, Integer ano, Integer mes) {
        List<Conta> listConta = new ArrayList<>();
        Database database = new Database(dataBaseLocation);
        List<Map<String, Object>> list = database.doSelect(
                "select * from conta \n" +
                        " where " +
                        " usuario = '" + usuario + "' \n" +
                        " and mes = " + mes + " and ano = " + ano+  " order by dia");
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                listConta.add(new Conta(map));
            }

        }
        database.close();
        return listConta;
    }

    public Conta getConta(String usuario, String lancamento, Integer ano, Integer mes) {
        List<Conta> listConta = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        Database database = new Database(dataBaseLocation);
        List<Map<String, Object>> list = database.doSelect(
                "select * from conta \n" +
                        " where " +
                        " usuario = '" + usuario + "' \n" +
                        " and ano = " + ano + " \n" +
                        " and mes = " + mes + " \n" +
                        " and lancamento = '" + lancamento + "'");
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                listConta.add(new Conta(map));
            }
        }
        database.close();
        if (listConta.size() > 0) {
            return listConta.get(0);
        } else {
            return null;
        }
    }

    public boolean createUser(String usuario, String nome, String senha) {
        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into usuarios(usuario,nome,senha,dataCriado) values ('" + usuario + "','" + nome + "','" + senha + "',DATETIME('now'))");
            database.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            database.close();
            return false;
        }
    }

    public boolean copyFromTo(String fromUsuario,Integer fromMes, Integer fromAno, String toUsuario, Integer toMes, Integer toAno) {
        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into conta (usuario, lancamento, mes, ano, dia, total, pago)" +
                    " select '"+toUsuario+"' as usuario, lancamento, "+toMes+" as mes, "+toAno+" as ano, dia, total, 0 as pago from conta where ano = "+fromAno+" and mes = "+fromMes+" and usuario = '"+fromUsuario+"';");
            database.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            database.close();
            return false;
        }
    }

    public boolean addConta(String lancamento, String descricao, String usuario, Double total, Date date, Boolean pago) {
        TimePeriod tp = TimeUtils.toTimePeriod(date);


        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into conta (usuario, lancamento, descricao, ano, mes, dia, total, pago)\n" +
                    "values ('" + usuario + "','" + lancamento + "','" + descricao + "'," + tp.getAno() + "," + tp.getMes() + "," + tp.getDia() + "," + total + "," + pago + ");");
            database.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            database.close();
            return false;
        }
    }

    public boolean deleteConta(String lancamento, String usuario, int ano, int mes,int dia) {
        Database database = new Database(dataBaseLocation);
        try {
            database.doPreparedUpdate("delete from conta where ano = ? and mes =? and dia=? and lancamento = ? and usuario =?",
                    ano,
                    mes,
                    dia,
                    lancamento,
                    usuario
            );
            database.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            database.close();

        }
        return false;
    }

    public boolean editarConta(String lancamento, String descricao, String usuario, Double total, Date date, Boolean pago) {
        TimePeriod tp = TimeUtils.toTimePeriod(date);
        if (tp != null) {
            Database database = new Database(dataBaseLocation);
            try {
                database.doPreparedUpdate("update conta set total=?, dia= ?, descricao=?, pago=? where ano = ? and mes =? and lancamento = ? and usuario =?",
                        total,
                        tp.getDia(),
                        descricao,
                        pago == null ? false : pago,
                        tp.getAno(),
                        tp.getMes(),
                        lancamento,
                        usuario
                );
                database.close();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                database.close();

            }
        }
        return false;
    }

}
