package br.edu.fatecguarulhos.escaneiaai.forms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormularioEvento {
    private Context context;
    private EditText edtTitulo, edtDataInicio, edtDataFim, edtLocal, edtDescricao;

    private Calendar calendario = Calendar.getInstance();

    public FormularioEvento(Context context, EditText edtTitulo, EditText edtDataInicio, EditText edtDataFim, EditText edtLocal, EditText edtDescricao) {
        this.context = context;
        this.edtTitulo = edtTitulo;
        this.edtDataInicio = edtDataInicio;
        this.edtDataFim = edtDataFim;
        this.edtLocal = edtLocal;
        this.edtDescricao = edtDescricao;
    }
    public void mostrarEscolhaDateTime(EditText edtData){
        new DatePickerDialog(context, (view, ano, mes, dia) -> {
            calendario.set(Calendar.YEAR, ano);
            calendario.set(Calendar.MONTH, mes);
            calendario.set(Calendar.DAY_OF_MONTH, dia);

            new TimePickerDialog(context, (timeView, hora, minuto) -> {
                calendario.set(Calendar.HOUR_OF_DAY, hora);
                calendario.set(Calendar.MINUTE, minuto);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                edtData.setText(sdf.format(calendario.getTime()));

            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), false).show();

        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
    }
    public boolean validarDados(){
        if(!validarTitulo(edtTitulo)){
            Toast.makeText(context, "Título obrigatório", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!validarDatas(edtDataInicio, edtDataFim)){
            Toast.makeText(context, "Data inicio/fim inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!validarLocal(edtLocal)){
            Toast.makeText(context, "Local obrigatório", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    private boolean validarTitulo(EditText campoNome){
        String input = campoNome.getText().toString().trim();
        return !input.isEmpty();
    }
    private boolean validarLocal(EditText campoLocal){
        String input = campoLocal.getText().toString().trim();
        return !input.isEmpty();
    }
    private boolean validarDatas(EditText dataInicio1, EditText dataFim1){
        if(dataFim1.getText().toString().equals("") || dataInicio1.getText().toString().equals(""))
            return false;
        String strInicio = dataInicio1.getText().toString();
        String strFim = dataFim1.getText().toString();
        Calendar d1 = stringToCalendar(strInicio);
        Calendar d2 = stringToCalendar(strFim);
        if(d1.equals(d2))
            return true;
        return(d1.before(d2));
    }

    public Calendar stringToCalendar(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
