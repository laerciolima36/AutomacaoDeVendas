package com.example.automaodevendas;

//import android.app.Person;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.automaodevendas.MainActivity.Grupos;

public class ListActivity extends AppCompatActivity {

    ListView lista;
    TextView txt;

    String mensagem;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lista = (ListView) findViewById(R.id.listview1);
        txt = (TextView) findViewById(R.id.textView10);


        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Grupos);
        lista.setAdapter(adapter);

        atualizaDados();
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
