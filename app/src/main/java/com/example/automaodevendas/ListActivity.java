package com.example.automaodevendas;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.automaodevendas.MainActivity.Grupos;

import java.io.FileOutputStream;

public class ListActivity extends AppCompatActivity {

    ListView lista;
    TextView txt;

    String mensagem;

    ArrayAdapter adapter;
    final String ARQUIVO = "arquivo.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lista = (ListView) findViewById(R.id.listview1);
        txt = (TextView) findViewById(R.id.textView10);

        //TODO ROTINA PARA REMOVER LINK DOS GRUPOS QUE ESTEJAM ERRADOS
        for(int i=0; i < Grupos.size(); i++){

            if(Grupos.get(i).length() > 48){
                Grupos.remove(i);
            }
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Grupos);
        lista.setAdapter(adapter);

        atualizaDados();
    }

    //TODO PROCEDIMENTO PARA CRIAR MENU SUSPENSO
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    //TODO VERIFICA QUAL ITEM FOI CLICADO NO MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.salvar) {
            insereNomeArquivo();
        }

        return super.onOptionsItemSelected(item);
    }

    public void insereNomeArquivo(){
        DialogFragment modalNome = new Modal();
        modalNome.show(modalNome.getFragmentManager(), "modalNome");
    }

    //TODO METODO PARA SALVAR ARQUVIO TXT
    private void salvarArquivo() {
            try {

                FileOutputStream out = openFileOutput(ARQUIVO, MODE_APPEND);

                String texto = "Este texto será gravado";

                out.write(texto.getBytes());

                out.close();

            } catch (Exception e) {

                Log.e("ERRO", e.getMessage());
            }

        }


    public void atualizaDados() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView textView = (TextView) view;
                        mensagem = textView.getText().toString();

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(mensagem));
                        startActivity(i);

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}
