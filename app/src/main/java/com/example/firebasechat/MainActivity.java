package com.example.firebasechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText enterEmail, enterPass;

    private Button btnEntrar;

    private TextView criarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Associar os componentes

        enterEmail=findViewById(R.id.enterEmail);
        enterPass=findViewById(R.id.enterPass);
        btnEntrar=findViewById(R.id.btnEntrar);
        criarConta=findViewById(R.id.criarContaClick);


        //Ação ao apertar o texto de criar conta

        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Ir para a tela de registro

                startActivity(new Intent(getApplicationContext(), RegistroActivity.class));

            }
        });


    }
}
