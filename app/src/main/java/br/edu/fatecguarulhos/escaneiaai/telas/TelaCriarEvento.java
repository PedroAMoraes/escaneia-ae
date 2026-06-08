package br.edu.fatecguarulhos.escaneiaai.telas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.edu.fatecguarulhos.escaneiaai.R;
import br.edu.fatecguarulhos.escaneiaai.dao.EventoDao;
import br.edu.fatecguarulhos.escaneiaai.forms.FormularioEvento;
import br.edu.fatecguarulhos.escaneiaai.models.Evento;

public class TelaCriarEvento extends AppCompatActivity {
    private EditText edtTitulo, edtDataInicio, edtDataFim, edtDescricao, edtLocal;
    private Button btnCriar, btnVoltar;
    private EventoDao eventoDao;
    private FormularioEvento formEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_evento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarValores();
        inicializarComponentes();
        configurarComponentes();
    }

    private void inicializarValores() {
        eventoDao = new EventoDao();
    }

    private void inicializarComponentes(){
        edtTitulo = findViewById(R.id.edtTituloEvento_criarEvento);
        edtDataInicio = findViewById(R.id.edtDataInicio_criarEvento);
        edtDataFim = findViewById(R.id.edtDataFim_criarEvento);
        edtLocal = findViewById(R.id.edtLocal_criarEvento);
        edtDescricao = findViewById(R.id.edtDescricao_criarEvento);
        btnCriar = findViewById(R.id.btnCriarEvento_criarEvento);
        btnVoltar = findViewById(R.id.btnVoltar_criarEvento);
        formEvento = new FormularioEvento(this, edtTitulo, edtDataInicio, edtDataFim, edtLocal, edtDescricao);
    }
    private void configurarComponentes(){
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formEvento.validarDados()){
                    Evento e = criarEvento();
                    registrarEvento(e);
                    mostrarSenha(e.getSenha());
                    limparCampos();
                }
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEvento.mostrarEscolhaDateTime(edtDataFim);
            }
        });
        edtDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEvento.mostrarEscolhaDateTime(edtDataInicio);
            }
        });
    }

    private Evento criarEvento(){
            Evento e = new Evento();
            e.setTitulo(edtTitulo.getText().toString().trim());
            String dataInicio = edtDataInicio.getText().toString();
            e.setDataInicio(dataInicio);
            e.setDataFim(edtDataFim.getText().toString());
            e.setMomentoInicio(formatarDataMomentoInicio(dataInicio));
            String idHash = String.valueOf(getIdCelular().hashCode());
            e.setIdCriador(getIdCelular());
            e.setSenha(idHash.substring(2,6));
            e.setLocal(edtLocal.getText().toString().trim());
            e.setDescricao(edtDescricao.getText().toString().trim());
        return e;
    }
    private void registrarEvento(Evento e){
        if(e != null){
            eventoDao.adicionarEvento(e);
            Toast.makeText(this, "Evento criado com sucesso",Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarSenha(String senha){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Senha: " + senha);
        builder.setMessage("Use esta senha para alterar seus eventos a partir de outro dispositivo.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // formatar a data de dia/mes/ano para ano/mes/dia, permitindo  organizar a ordem de inicio/fim
    private String formatarDataMomentoInicio(String dataInicio){
        String[] dataHora = dataInicio.split(" - ");
        String[] dataFatia = dataHora[0].split("/");
        String[] horaFatia = dataHora[1].split(":");
        String momentoInicio = dataFatia[2];
        momentoInicio = momentoInicio + dataFatia[1];
        momentoInicio = momentoInicio + dataFatia[0];
        momentoInicio = momentoInicio + horaFatia[0];
        momentoInicio = momentoInicio + horaFatia[1];
        return momentoInicio;
    }

    // para definir o criador do evento
    private String getIdCelular(){
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void limparCampos(){
        edtTitulo.setText("");
        edtDataInicio.setText("");
        edtDataFim.setText("");
        edtLocal.setText("");
        edtDescricao.setText("");
    }


}


