package com.example.automaodevendas;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.example.automaodevendas.MainActivity.cx;
import static com.example.automaodevendas.MainActivity.key;

public class Config extends AppCompatActivity {

    //configuracoes github
    //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
    Button btsalvar;
    EditText editKey;
    EditText editCX;

    String fileName = "file.txt";
    String fileName2 = "file2.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        editKey = (EditText) findViewById(R.id.editkey);
        editCX = (EditText) findViewById(R.id.editCX);

        btsalvar = (Button) findViewById(R.id.btsalvar);

        editKey.setText(LerArquivo(fileName));
        key = LerArquivo(fileName);
        editCX.setText(LerArquivo(fileName2));
        cx = LerArquivo(fileName2);

        getSupportActionBar().hide();

        btsalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarArquivo(fileName, editKey.getText().toString());
                salvarArquivo(fileName2, editCX.getText().toString());
                key = LerArquivo(fileName);
                cx = LerArquivo(fileName2);

                Intent Principal = new Intent(Config.this,MainActivity.class);
                startActivity(Principal);
            }
        });
        }

    public void salvarArquivo(String file, String text) {
        try {
            FileOutputStream outputStream = openFileOutput(file, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
            //Toast.makeText(getBaseContext(), "Salvo!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Erro ao Salvar o Arquivo!", Toast.LENGTH_SHORT).show();
        }

    }

    public String LerArquivo(String File) {
        String text = "";

        try {
            FileInputStream fileInputStream = openFileInput(File);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            text = new String(buffer);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Erro ao Ler o Arquivo!", Toast.LENGTH_SHORT).show();
        }

        return text;
    }
}
