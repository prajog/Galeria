package beretta.prajo.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        //obtem da Activity a ActionBar padrao (setada acima)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //obtem o caminho enviado via Intent
        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");

        //carrega a foto em um Bitmap
        Bitmap bitmap = Util.getBitmap(photoPath);
        ImageView imPhoto = findViewById(R.id.imPhoto);
        //seta o Bitmap no ImageView
        imPhoto.setImageBitmap(bitmap);
    }

    //metodo que cria um inflador de menu, para crias as opcoes de menu definidas no arquivo de menu passado como parametro e as adiciona no menu da Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    //metodo chamado toda vez q um item da ToolBar for selecionado. Se o icone de compartilhar for clicado, vai excutar o codigo que compartilha a foto
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}