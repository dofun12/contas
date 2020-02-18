package org.lemanoman.contas;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class DatabaseService {
    @Value("${database.location}")
    private String dataBaseLocation;

    public void resetDatabase(){
        Database database = new Database(dataBaseLocation);
        database.dropAndCreate();
        database.close();
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

    public boolean createUser(String usuario,String nome,String senha){
        Database database = new Database(dataBaseLocation);
        try {
            database.doUpdate("insert into usuarios(usuario,nome,senha,dataCriado) values ('"+usuario+"','"+nome+"','"+senha+"',DATETIME('now'))");
            database.close();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            database.close();
            return true;
        }
    }

}
