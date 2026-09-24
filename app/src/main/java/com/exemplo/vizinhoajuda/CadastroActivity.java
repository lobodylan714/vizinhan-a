package com.exemplo.vizinhoajuda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.Response;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);

        EditText nome = findViewById(R.id.nome);
        EditText email = findViewById(R.id.email);
        EditText senha = findViewById(R.id.senha);

        Button cadastrar = findViewById(R.id.cadastrar);

        cadastrar.setOnClickListener(v -> {

            String nomeTexto = nome.getText().toString().trim();
            String emailTexto = email.getText().toString().trim();
            String senhaTexto = senha.getText().toString().trim();

            if (nomeTexto.isEmpty() || emailTexto.isEmpty() || senhaTexto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            cadastrarNoSupabase(nomeTexto, emailTexto, senhaTexto);
        });
    }

    private void cadastrarNoSupabase(String nome, String email, String senha) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("nome", nome);
                body.put("email", email);
                body.put("senha", senha);

                Request request = SupabaseClient.requestBuilder("usuarios")
                        .post(SupabaseClient.jsonBody(body.toString()))
                        .build();

                try (Response response = SupabaseClient.HTTP_CLIENT.newCall(request).execute()) {
                    String mensagem = response.isSuccessful() ? "Cadastro realizado!" : "Erro ao cadastrar.";

                    runOnUiThread(() -> {
                        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
                        if (response.isSuccessful()) {
                            finish();
                        }
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro de conexão com Supabase: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}