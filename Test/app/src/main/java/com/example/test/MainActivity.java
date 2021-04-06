package com.example.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner=(Spinner) findViewById(R.id.spinner);
        ArrayList<String> arrayData=new ArrayList<>();
        arrayData.add("Plus");
        arrayData.add("Minus");
        arrayData.add("Inmultire");
        arrayData.add("Impartire");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter(this,R.layout.adapter_item,R.id.textViewAdapter,arrayData);
        spinner.setAdapter(arrayAdapter);

    }


    public void dialogAlertBuilder(String s)
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rezultat");
        builder.setMessage(s);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void Calculate(View view) {
        EditText number1=(EditText)findViewById(R.id.number1);
        EditText number2=(EditText)findViewById(R.id.number2);
        Spinner spinner=(Spinner)findViewById(R.id.spinner);
        String result;
        if((number1.getText().toString()!=null )&&(number2.getText().toString()!=null)) {
            float n1 = Integer.valueOf(number1.getText().toString());
            float n2 = Integer.valueOf(number2.getText().toString());
            if(n2!=0){
            if (spinner.getSelectedItem().toString().contains("Plus")) {
                result = number1.getText().toString() + " + " + number2.getText().toString() + " = " + String.valueOf(n1 + n2);
                dialogAlertBuilder(result);
            } else if (spinner.getSelectedItem().toString().contains("Minus")) {
                result = number1.getText().toString() + " - " + number2.getText().toString() + " = " + String.valueOf(n1 - n2);
                dialogAlertBuilder(result);
            } else if (spinner.getSelectedItem().toString().contains("Inmultire")) {
                result = number1.getText().toString() + " * " + number2.getText().toString() + " = " + String.valueOf(n1 * n2);
                dialogAlertBuilder(result);
            } else {
                result = number1.getText().toString() + " / " + number2.getText().toString() + " = " + String.valueOf(n1 / n2);
                dialogAlertBuilder(result);
            }}
            else
            {
                Toast.makeText(getApplicationContext(),"Null division",Toast.LENGTH_LONG).show();
            }
        }

    }
}