package com.example.automaodevendas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    static List<String> Grupos, SitesEncontrados;

    EditText EditTipo, EditLocal;
    Button btGrupo;

    static String key, cx;
    int inicio = 1;
    static int QuantPaginasParaBusca = 1;
    static int termina0 = 10;

    static ProgressBar progressBar;

    TextView txtstatus;
    Button btBuscar;

    String fileName = "file.txt";
    String fileName2 = "file2.txt";

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

        //TODO LER O ARQUIVO, CASO NAO ENCONTRE CHAMA CONFIGURAÇÃOES
        key = LerArquivo(fileName);
        cx = LerArquivo(fileName2);


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

    //TODO PROCEDIMENTO PARA CRIAR MENU SUSPENSO
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO VERIFICA QUAL ITEM FOI CLICADO NO MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.config) {
            ChamaConfig();
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO ACTIVITY DE CONFIGURAÇÕES
    public void ChamaConfig() {
        Intent config = new Intent(this, Config.class);
        startActivity(config);
    }


    //TODO INICIO DO PROGRAMA
    //TODO BOTAO EFETUAR BUSCA

    public void BtBuscar(View view) {
        try {
            inicio = 1;
            SitesEncontrados.clear();
            Grupos.clear();

            while (inicio <= QuantPaginasParaBusca) {
                //linksBusca();
                GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                searchTask.execute();
                inicio++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro no Botão Buscar!");
        }
    }

    public void linksBusca(){
        try {
            Document doc = Jsoup.connect("https://www.google.com/search?q="+GetStringBusca()).get();

            System.out.println("O Titulo e: " + doc.title());

            Elements links = doc.getElementsByTag("a");

            for (Element link : links) {
                SitesEncontrados.add(link.attr("href"));
                System.out.println(link.attr("href"));
            }
        }
        catch (Exception e){
            System.out.println("Jsoup exception " + e);
        }

        System.out.println("SITES ENCONTRADOS: " + SitesEncontrados);
        System.out.println("Quantidade de Sites: " + Integer.toString(SitesEncontrados.size()));

        CapturaGrupos Captura = new CapturaGrupos();
        Captura.execute(termina0);

    }

    //TODO INICIA A CHAMADA DA BUSCA NO GOOGLE, RECEBE TODOS OS PARAMETROS DE BUSCA E CHAMA A THREAD DE BUSCA

    public void CarregaBuscaGoogle(String searchString, String startIndex, String key, String cx) {

        // TODO esconde o teclado
        //InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // TODO retira os espaços da string de busca e coloca sinal de +
        String searchStringNoSpaces = searchString.replace(" ", "+");

        // Your API key
        // TODO replace with your value
        //key = "AIzaSyCwHchTrrj4QQaROeQ5QnH7ba1Z3l_YkuU";


        // Your Search Engine ID
        // TODO replace with your value
        //cx = "008858830339203641993:2eoh2dq7qh8";

        String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json&start=" + startIndex;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            System.out.println("ERROR converting String to URL " + e.toString());
        }
        System.out.println("Url de Pesquisa = " + urlString);

        // TODO CHAMA A THREAD DE BUSCA start AsyncTask
        GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
        searchTask.execute(url);

    }

    //TODO METODO PARA PEGAR LINK DO RESULTADO DO GOOGLE
    public List<String> findHref(String page) { //Pega Links da pesquisa do google

        //String regex = "\\b" + "a href" + "\\b";

        String regex = "\"link\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(page);
        System.out.println(page);

        List<String> list = new ArrayList<String>();

        while (matcher.find() == true) {
            int end = matcher.end();
            //String hrefQuote = page.substring(end - 2, end - 1);
            //System.out.println(hrefQuote);
            int index = page.indexOf("\"", end + 7);

            if (page.substring(index - 1, index).equals("\""))
                index = index - 1;

            String word = page.substring(end + 3, index);
            list.add(word);
            SitesEncontrados.add(word);
            //System.out.println("RESULTADO FINDHREF: " + word);
        }

        System.out.println("SITES ENCONTRADOS: " + SitesEncontrados);
        System.out.println("Quantidade de Sites: " + Integer.toString(SitesEncontrados.size()));

        CapturaGrupos Captura = new CapturaGrupos();
        Captura.execute(termina0);

        return list;
    }

    //TODO LER UM ARQUIVO ARMAZENADO NA MEMORIA INTERNA DO CELULAR
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
            ChamaConfig();
            //Toast.makeText(getBaseContext(), "Acesse as configurações!", Toast.LENGTH_SHORT).show();
        }

        return text;
    }

    //TODO FORMA A STRING PARA A BUSCA NO GOOGLE
    public String GetStringBusca() {
        String strBusca = EditTipo.getText().toString() + " " + EditLocal.getText().toString() + " https://chat.whatsapp facebook";
        return strBusca;
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
            Integer tamanho0 = integers[0];


            //if (SitesEncontrados.size() == tamanho0) {
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
            //}
            return quantidadegrupo;
        }

        protected void onProgressUpdate(Integer... progress) {

            //System.out.println("executando captura grupo" + quantidadegrupo);
            //Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

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

    //TODO CLASS DE BUSCA NO GOOGLE
    public class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {

        String result = null;
        Integer responseCode = null;
        String responseMessage = "";

        //TODO PROCEDIMENTO EXECUTADO ANTES DA THREAD INICIAR, EXECUTA NA THREAD PRINCIPAL UI THREAD
        protected void onPreExecute() {
            System.out.println("AsyncTask - onPreExecute");
            txtstatus.setText("Iniciando Conexão...");
            progressBar.setVisibility(View.VISIBLE);
            btBuscar.setEnabled(false);

        }

        //TODO METODO PRINCIPAL DA THREAD, EXECUTADO EM SEGUNDO PLANO, NAO É POSSIVEL ACESSAR A UI THREAD
        @Override
        protected String doInBackground(URL... urls) {

            try {
                //Document doc = Jsoup.connect("https://www.google.com").get();

                Document doc = Jsoup.connect("https://www.google.com/search?q="+GetStringBusca()).get();

                System.out.println("O Titulo e: " + doc.title());

                Elements links = doc.getElementsByTag("a");

                for (Element link : links) {
                    SitesEncontrados.add(link.attr("href"));
                    System.out.println(link.attr("href"));
                }
            }
            catch (Exception e){
                System.out.println("Jsoup exception " + e);
            }

            System.out.println("SITES ENCONTRADOS: " + SitesEncontrados);
            System.out.println("Quantidade de Sites: " + Integer.toString(SitesEncontrados.size()));

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

            System.out.println("AsyncTask - onProgressUpdate, progress=" + progress);
            //Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        public void onPostExecute(String result) {
            CapturaGrupos Captura = new CapturaGrupos();
            Captura.execute(termina0);
            //findHref(result);
        }
    }
}
