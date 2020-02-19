package org.lemanoman.contas;


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

    public void resetDatabase(boolean forceReset){
        File db = new File(dataBaseLocation);
        if(!db.exists()|| forceReset || forceRestore){
            Database database = new Database(dataBaseLocation);
            database.dropAndCreate();
            database.close();
        }
    }


    public UserModel getUsuario(String usuario){
        UserModel userModel = new UserModel();
        Database database = new Database(dataBaseLocation);
        List<Map<String,Object>> list = database.doSelect("select usuario,nome,senha,dataCriado from usuarios where usuario = '"+usuario+"'");
        if(list!=null && list.size()>0){
            Map<String,Object> map = list.get(0);
            userModel =  new UserModel(
                    (String) map.get("usuario"),
                    (String) map.get("senha"),
                    (String) map.get("nome"),
                    "ADMIN"
            );
        }
        database.close();
        return  userModel;
    }

    public List<Conta> getListContasMesAtual(String usuario){
        List<Conta> listConta = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        Database database = new Database(dataBaseLocation);
        List<Map<String,Object>> list = database.doSelect(
                "select * from conta \n" +
                        " where " +
                        " usuario = '"+usuario+"' \n" +
                        " and mes = "+(calendar.get(Calendar.MONTH)+1)+" and ano = "+calendar.get(Calendar.YEAR));
        if(list!=null && list.size()>0){
            for(Map<String,Object> map: list){
                listConta.add(new Conta(map));
            }

        }
        database.close();
        return  listConta;
    }

    public boolean createUser(String usuario,String nome,String senha){
        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into usuarios(usuario,nome,senha,dataCriado) values ('"+usuario+"','"+nome+"','"+senha+"',DATETIME('now'))");
            database.close();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            database.close();
            return false;
        }
    }

    public boolean addConta(String lancamento,String descricao,String usuario,Double total, Date date, Boolean pago){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into conta (usuario, lancamento, descricao, ano, mes, dia, total, pago)\n" +
                    "values ('"+usuario+"','"+lancamento+"','"+descricao+"',"+calendar.get(Calendar.YEAR)+","+(calendar.get(Calendar.MONTH)+1)+","+calendar.get(Calendar.DAY_OF_MONTH)+","+total+","+pago+");");
            database.close();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            database.close();
            return false;
        }
    }

}
