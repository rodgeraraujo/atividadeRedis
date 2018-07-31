package com.ifpb.rodger.redis.dao;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ifpb.rodger.redis.conection.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ifpb.rodger.redis.model.Produto;
import redis.clients.jedis.Jedis;

public class ProdutoDao {

    private Config postgre;
    private Connection con;
    private Jedis jedis;
    private Gson gson;

    public ProdutoDao() throws SQLException, ClassNotFoundException {
        postgre = new Config();

        con = postgre.getConnection();

        jedis = new Jedis("localhost", 6379);

        gson = new Gson();
    }

    public boolean salvar(Produto p) throws SQLException {

        PreparedStatement stmt = con.prepareStatement("INSERT INTO produto (codigo, descricao, preco) values (?,?,?)");
        stmt.setInt(1, p.getCodigo());
        stmt.setString(2, p.getDescricao());
        stmt.setFloat(3, p.getPreco());

        stmt.execute();

        String json = gson.toJson(p);
        jedis.setex("" + p.getCodigo(), 1800, json);
        jedis.close();

        return true;
    }

    public Produto buscar(int codigo) throws SQLException {
        PreparedStatement statementmt = con.prepareStatement("SELECT * FROM produto WHERE codigo= ? ");
        statementmt.setInt(1, codigo);
        ResultSet validate = statementmt.executeQuery();

        if (validate.next()){
            if (jedis.get("" + codigo) == null) {

                System.out.println("O produto não está salvo no Redis. \nBusca realizada no PostgreSQL.\n");

                PreparedStatement stmt = con.prepareStatement("SELECT * FROM produto WHERE codigo= ? ");
                stmt.setInt(1, codigo);
                ResultSet result = stmt.executeQuery();

                if (result.next()){
//                System.out.println("Código: " + result.getString("codigo") +
//                        "\nDescrição: " + result.getString("descricao") +
//                        "\nPreço: " + result.getString("preco"));

                    int codigoInt = Integer.parseInt(result.getString("codigo"));
                    String decricaoString = result.getString("descricao");
                    float precoFloat = Float.valueOf(result.getString("preco").trim()).floatValue();

                    Produto produto = new Produto(codigoInt, decricaoString, precoFloat);
                    return produto;
                }
            } else {
                System.out.println("O produto encontrado no Redis.");

                String result = jedis.get("" + codigo);
                Produto p = gson.fromJson(result, Produto.class);

//            System.out.println("Código: " + p.getCodigo() +
//                    "\nDescrição: " + p.getDescricao() +
//                    "\nPreço: " + p.getPreco());

                Produto produto = new Produto(p.getCodigo(), p.getDescricao(), p.getPreco());
                return produto;
            }
        }

        System.out.printf("O codigo não corresponde a nenhum produto.");
        return null;
    }
}
