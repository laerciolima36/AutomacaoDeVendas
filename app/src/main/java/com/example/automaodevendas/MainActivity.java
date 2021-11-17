package com.example.automaodevendas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    static List<String> Grupos, SitesEncontrados;

    EditText EditTipo, EditLocal;

    static String key, cx;
    int inicio = 1;
    static int QuantPaginasParaBusca = 1;
    static int termina0 = 10;

    static ProgressBar progressBar;

    TextView txtstatus;
    Button btBuscar;

    List<String> DadosDoListView;
    ArrayAdapter<String> arrayAdapter;

    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //TODO INSTANCIA INICIAL DA CLASE, SETANDO A ACTIVITY DA CLASSE

        //TODO IDENTIFICA AS VIEW NA ACTIVITY PARA PODER SER ACESSADA PELA CLASSE (VIEW SAO OS BOTOES, TEXTO E ETC)
        EditTipo = (EditText) findViewById(R.id.EditTipo);
        EditLocal = (EditText) findViewById(R.id.EditLocal);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        sp = (Spinner) findViewById(R.id.spinner);
        txtstatus = (TextView) findViewById(R.id.txtStatus);

        Grupos = new ArrayList<String>();
        SitesEncontrados = new ArrayList<String>();

        btBuscar = (Button) findViewById(R.id.btBuscar);

        //TODO ADAPTADOR PARA O SPINNER (DROP DOWN LIST), ADAPTADOR SE CONECTA A FONTE DE DADOS (RESOUCES OU BANCO DE DADOS) E SETAMOS O ADAPTADOR PARA A VIEW
        ArrayAdapter<CharSequence> spadapter = ArrayAdapter.createFromResource(this, R.array.pages, android.R.layout.simple_spinner_dropdown_item);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(spadapter);

        //TODO FUNCAO PARA CLICAR NO SPINNER
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    QuantPaginasParaBusca = Integer.parseInt(sp.getItemAtPosition(position).toString());
                    System.out.println("Quantidade Selecionada de Paginas da Busca: " + QuantPaginasParaBusca);
                    String s = QuantPaginasParaBusca + "0";
                    termina0 = Integer.parseInt(s);
                    System.out.println("Valor da Variavel Termina0: " + termina0);
                } catch (NumberFormatException nfe) {
                    System.out.println("Erro ao pegar valor do spinner " + nfe);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //TODO INICIO DO PROGRAMA
    //TODO BOTAO EFETUAR BUSCA

    public void BtBuscar(View view) {
        try {
            inicio = 1;
            SitesEncontrados.clear();
            Grupos.clear();

            while (inicio <= QuantPaginasParaBusca) {
                GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                searchTask.execute();
                inicio++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro no Botão Buscar!");
        }
    }

    //TODO FORMA A STRING PARA A BUSCA NO GOOGLE
    public String GetStringBusca() {
        String strBusca = EditTipo.getText().toString() + " " + EditLocal.getText().toString() + " https://chat.whatsapp facebook";
        return strBusca;
    }

    //TODO CLASS DE BUSCA NO GOOGLE
    public class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {

        //TODO PROCEDIMENTO EXECUTADO ANTES DA THREAD INICIAR, EXECUTA NA THREAD PRINCIPAL UI THREAD
        protected void onPreExecute() {
            System.out.println("Iniciando a pesquisa no Google");
            txtstatus.setText("Iniciando Conexão...");
            progressBar.setVisibility(View.VISIBLE);
            btBuscar.setEnabled(false);

        }

        //TODO METODO PRINCIPAL DA THREAD, EXECUTADO EM SEGUNDO PLANO, NAO É POSSIVEL ACESSAR A UI THREAD

        @Override
        protected String doInBackground(URL... urls) {

            try {

                Document doc = Jsoup.connect("https://www.google.com/search?q=" + GetStringBusca()).get();

                System.out.println("O Titulo e: " + doc.title());

                Elements links = doc.getElementsByTag("a");

                for (Element link : links) {
                    SitesEncontrados.add(link.attr("href"));
                    System.out.println("Links encontrados na Busca do Google" + link.attr("href"));
                }
            } catch (Exception e) {
                System.out.println("Jsoup exception " + e);
            }

            System.out.println("SITES ENCONTRADOS: " + SitesEncontrados);
            System.out.println("Quantidade de Sites: " + SitesEncontrados.size());

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            System.out.println("AsyncTask - onProgressUpdate, progress=" + progress);
        }

        public void onPostExecute(String result) {
            CapturaGrupos Captura = new CapturaGrupos();
            Captura.execute(termina0);
        }
    }

    public class CapturaGrupos extends AsyncTask<Integer, Integer, Integer> {

        private Integer numero;
        int quantSites = 10;
        int comeca = 0;
        int quantidadegrupo = 0;


        //TODO PROCEDIMENTO EXECUTADO ANTES DA THREAD INICIAR, EXECUTA NA THREAD PRINCIPAL UI THREAD
        protected void onPreExecute() {
            txtstatus.setText("Iniciando a captura de Grupos...");
            System.out.println("Executou captura grupo");
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            quantSites = SitesEncontrados.size();
            comeca = 0;

            while (comeca < quantSites) {
                SearchMain searchMain = new SearchMain();
                searchMain.init(SitesEncontrados.get(comeca));
                quantidadegrupo = Grupos.size();
                comeca++;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtstatus.setText("Quantidade de Grupos Encontrados: " + Grupos.size());
                    }
                });
            }
            return quantidadegrupo;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void onPostExecute(Integer result) {
            if (comeca >= quantSites) {
                txtstatus.setText("");
                progressBar.setVisibility(View.GONE);
                btBuscar.setEnabled(true);
                Intent gp = new Intent(MainActivity.this, ListActivity.class);
                startActivity(gp);
            }
        }
    }
}