package com.saket.attendanceplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class settings_professor extends AppCompatActivity {
    akashDBhelper dBhelper;
    String textViewText = "Professors Enrolled: \n";
    List<Professor> professorList;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_professor);
        dBhelper = ((myCustomApplication)getApplication()).dBhelper;
        textView = (TextView)findViewById(R.id.prof_credentials);
        professorList = dBhelper.getProfessors();

        for(int i=0;i<professorList.size();i++) {
            textViewText += professorList.get(i).getName()+"\n";
        }
        textView.setText(textViewText);
    }

    public void add_professor(View view)  {
        EditText editText1 = (EditText) findViewById(R.id.prof_id);
        String prof_id = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.prof_name);
        String prof_name = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.prof_password);
        String prof_password = editText3.getText().toString();
        if(!prof_id.equals("") && !prof_name.equals("") && !prof_password.equals("")) {
            dBhelper.addProfessor(new Professor(prof_id, prof_name, prof_password));
            editText1.setText("");
            editText2.setText("");
            editText3.setText("");
        }
        else
            Toast.makeText(this,"Enter valid credentials!",Toast.LENGTH_LONG).show();
        professorList = dBhelper.getProfessors();
        textViewText = "Professors Enrolled: \n";
        for(int i=0;i<professorList.size();i++) {
            textViewText += professorList.get(i).getName()+"\n";
        }
        textView.setText(textViewText);
    }

    public void delete_professor(View view)   {
        EditText editText1 = (EditText) findViewById(R.id.prof_id);
        String prof_id = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.prof_name);
        String prof_name = editText2.getText().toString();
        EditText editText3 = (EditText) findViewById(R.id.prof_password);
        String prof_password = editText3.getText().toString();
        if(!prof_id.equals("") && !prof_name.equals("") && !prof_password.equals("")) {
            if(dBhelper.verifyProfessor(prof_id,prof_password)!=null) {
                dBhelper.deleteProfessor(new Professor(prof_id,prof_name,prof_password));
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
            }
            else
                Toast.makeText(this,"Invalid Professor credentials!",Toast.LENGTH_LONG).show();

        }
        else
            Toast.makeText(this,"Invalid Professor credentials!",Toast.LENGTH_LONG).show();
        professorList = dBhelper.getProfessors();
        textViewText = "Professors Enrolled: \n";
        for(int i=0;i<professorList.size();i++) {
            textViewText += professorList.get(i).getName()+"\n";
        }
        textView.setText(textViewText);

    }
}
