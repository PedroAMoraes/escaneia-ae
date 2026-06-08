package br.edu.fatecguarulhos.escaneiaai.telas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.edu.fatecguarulhos.escaneiaai.R;
import br.edu.fatecguarulhos.escaneiaai.dao.EventoDao;
import br.edu.fatecguarulhos.escaneiaai.forms.FormularioEvento;
import br.edu.fatecguarulhos.escaneiaai.models.Evento;

public class TelaEditarEvento extends AppCompatActivity {
    private EditText edtTitulo, edtLocal, edtDescricao, edtDataInicio, edtDataFim;
    private Button btnVoltar, btnEditar, btnExcluir;
    private FormularioEvento formEvento;
    private Evento evento;
    private EventoDao eventoDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_editar_evento);
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
        Intent it = getIntent();
        String jsonEvento = it.getStringExtra("jsonEvento");
        evento =  new Gson().fromJson(jsonEvento, Evento.class);
        eventoDAO = new EventoDao();
    }

    private void inicializarComponentes(){
        edtTitulo = findViewById(R.id.edtTituloEvento_editarEvento);
        edtDescricao = findViewById(R.id.edtDescricao_editarEvento);
        edtLocal = findViewById(R.id.edtLocal_editarEvento);
        edtDataInicio = findViewById(R.id.edtDataInicio_editarEvento);
        edtDataFim = findViewById(R.id.edtDataFim_editarEvento);
        btnEditar = findViewById(R.id.btnEditarEvento_editarEvento);
        btnExcluir = findViewById(R.id.btnExcluirEvento_editarEvento);
        btnVoltar = findViewById(R.id.btnVoltar_editarEvento);
        formEvento = new FormularioEvento(this, edtTitulo, edtDataInicio, edtDataFim, edtLocal, edtDescricao);
    }
    private void configurarComponentes(){
        edtTitulo.setText(evento.getTitulo());
        edtLocal.setText(evento.getLocal());
        edtDescricao.setText(evento.getDescricao());
        edtDataInicio.setText(evento.getDataInicio());
        edtDataFim.setText(evento.getDataFim());
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
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarEvento();
            }
        });
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirEvento();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void excluirEvento(){
        new AlertDialog.Builder(this)
                .setTitle("Excluindo evento")
                .setMessage("Deseja realmente excluir " + evento.getTitulo() +"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        try{
                            eventoDAO.deleteEvento(evento.getId());
                            Toast.makeText(TelaEditarEvento.this, "Evento excluido!", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent();
                            setResult(AppCompatActivity.RESULT_OK, it);
                            finish();
                        } catch (RuntimeException re){
                            Toast.makeText(TelaEditarEvento.this, re.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    public void editarEvento(){
        try{
            if(formEvento.validarDados()){
                evento.setTitulo(edtTitulo.getText().toString().trim());
                String dataInicio = edtDataInicio.getText().toString();
                evento.setDataInicio(dataInicio);
                evento.setMomentoInicio(converterDataMomentoInicio(dataInicio));
                evento.setDataFim(edtDataFim.getText().toString());
                evento.setLocal(edtLocal.getText().toString().trim());
                evento.setDescricao(edtDescricao.getText().toString().trim());
                eventoDAO.updateEvento(evento);
                Toast.makeText(this, "Evento atualizado!", Toast.LENGTH_SHORT).show();
            }
        } catch (RuntimeException re){
            Toast.makeText(TelaEditarEvento.this, re.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(TelaEditarEvento.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String converterDataMomentoInicio(String dataInicio){
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
}