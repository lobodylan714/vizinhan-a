package com.exemplo.vizinhoajuda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);

        Button entrar = findViewById(R.id.entrar);
        Button cadastro = findViewById(R.id.cadastro);

        cadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        entrar.setOnClickListener(v -> {
            String emailTexto = email.getText().toString().trim();
            String senhaTexto = senha.getText().toString().trim();

            if (emailTexto.isEmpty() || senhaTexto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            autenticarNoSupabase(emailTexto, senhaTexto);
        });
    }

    private void autenticarNoSupabase(String emailTexto, String senhaTexto) {
        new Thread(() -> {
            try {
                String emailEncoded = URLEncoder.encode(emailTexto, StandardCharsets.UTF_8.toString());
                String url = "usuarios?email=eq." + emailEncoded;

                Request request = SupabaseClient.requestBuilder(url)
                        .get()
                        .build();

                try (Response response = SupabaseClient.HTTP_CLIENT.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(this, "Erro ao consultar usuário", Toast.LENGTH_LONG).show());
                        return;
                    }

                    String responseBody = response.body() != null ? response.body().string() : "[]";
                    JSONArray usuarios = new JSONArray(responseBody);

                    if (usuarios.length() == 0) {
                        runOnUiThread(() -> Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_LONG).show());
                        return;
                    }

                    JSONObject usuario = usuarios.getJSONObject(0);
                    String senhaSalva = usuario.optString("senha", "");

                    if (senhaSalva.equals(senhaTexto)) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Senha incorreta", Toast.LENGTH_LONG).show());
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro de conexão com Supabase: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}