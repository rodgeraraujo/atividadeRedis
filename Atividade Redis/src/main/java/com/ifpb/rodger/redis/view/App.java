
package com.ifpb.rodger.redis.view;

import com.ifpb.rodger.redis.dao.ProdutoDao;
import com.ifpb.rodger.redis.model.Produto;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String[] args) {

        Produto produto = new Produto(1, "Arroz Integral", 7.52f);
        Produto produto2 = new Produto(2, "Feijão Carioca", 7.13f);
        Produto produto3 = new Produto(3, "Picanha", 22.93f);

        try {
            ProdutoDao dao = new ProdutoDao();
            dao.salvar(produto);
            dao.salvar(produto2);
            dao.salvar(produto3);

            System.out.println("Resultado: " + dao.buscar(3).toString());

        } catch (SQLException ex) {
            System.out.println("Error: não foi possivel conectar ao banco!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch(NullPointerException e){
        }
    }
    
}
