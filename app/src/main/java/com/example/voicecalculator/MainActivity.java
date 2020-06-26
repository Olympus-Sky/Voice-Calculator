package com.example.voicecalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, AdapterView.OnItemClickListener {
    private Button btnListenJ;
    private TextView txtVoiceJ, txtOperationJ;
    private TextToSpeech txtSpeak;
    private ListView lvWordJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Widgets();
        Buttons();
    }

    private void Widgets() {
        txtSpeak = new TextToSpeech(MainActivity.this, MainActivity.this);
        txtVoiceJ = findViewById(R.id.txtVoice);
        txtOperationJ = findViewById(R.id.txtOperation);
        btnListenJ = findViewById(R.id.btnListen);
        lvWordJ = findViewById(R.id.lvWord);
    }

    private void Buttons() {
        btnListenJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

                if (activities.size() != 0) {
                    Intent intent = getRecognizerIntent();
                    startActivityForResult(intent, 0);
                }
            }
        });

        lvWordJ.setOnItemClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemTxt = parent.getItemAtPosition(position);
        String txt = itemTxt.toString();
        String[] txtSplit = txt.split(" ");
        double[] n = new double[txtSplit.length];
        double total = 0;
        String result = "";
        boolean division = false;

        for (int i = 0; i < txtSplit.length; i++) {
            boolean root = txtSplit[i].equals("√");
            boolean factorial = txtSplit[i].equals("fatorial");

            if (!root && !factorial)
                n[i] = i % 2 == 0 ? Double.parseDouble(txtSplit[i]) : 0;
        }

        for (int i = 0; i < txtSplit.length; i++) {
            switch (txtSplit[i]) {
                case "+":
                    if (i == 1) {
                        total = n[i - 1] + n[i + 1];
                    } else {
                        total += n[i + 1];
                    }
                    break;

                case "-":
                    if (i == 1) {
                        total = n[i - 1] - n[i + 1];
                    } else {
                        total -= n[i + 1];
                    }
                    break;

                case "x":
                case "*":
                    if (i == 1) {
                        total = n[i - 1] * n[i + 1];
                    } else {
                        total *= n[i + 1];
                    }
                    break;

                case "/":
                    if (i + 1 == txtSplit.length && n[i + 1] == 0) {
                        division = true;
                        result = "Impossível realizar divisão por 0!";
                        break;
                    } else {
                        if (i == 1) {
                            total = n[i - 1] / n[i + 1];
                        } else {
                            total /= n[i + 1];
                        }
                    }
                    break;

                case "^":
                    if (i == 1) {
                        total = Math.pow(n[i - 1], n[i + 1]);
                    }
                    break;

                default:
                    break;
            }

            division = false;
        }

        switch (txtSplit[0]) {
            case "√":
                total = Math.sqrt(Double.parseDouble(txtSplit[1]));
                break;

            case "fatorial":
                total = Double.parseDouble(String.valueOf(FatorialRecursivo(Integer.parseInt(txtSplit[1]))));
                break;

            default:
                break;
        }

        if (!division)
            txtVoiceJ.setText("O seu resultado é " + total);
        else
            txtVoiceJ.setText("O seu resultado é " + result);

        String text = txtVoiceJ.getText().toString();
        txtSpeak.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle.EMPTY, "1");

    }

    int FatorialRecursivo(int n) {
        int fatorial;

        if (n == 1) {
            fatorial = 1;
        }

        else {
            fatorial = n * FatorialRecursivo(n - 1);
        }

        return fatorial;
    }

    @Override
    public void onInit(int status) {
        Locale locale = new Locale("pt", "BR");
        txtSpeak.setLanguage(locale);
    }

    protected Intent getRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale aqui");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        return intent;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            lvWordJ.setAdapter(new ArrayAdapter<>(this, R.layout.custom_list_view, Objects.requireNonNull(words)));
            txtOperationJ.setText("Escolha a operação correta");
        }
    }
}