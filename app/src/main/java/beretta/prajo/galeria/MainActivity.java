package beretta.prajo.galeria;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;

    //guarda somente o local do arquivo de foto que esta sendo manipulado no momento
    String currentPhotoPath;

    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtem o elemento tbMain
        Toolbar toolbar = findViewById(R.id.tbMain);

        //indica que tbMain deve ser considerado como a ActionBar padrao da tela
        setSupportActionBar(toolbar);

        //acessa o diretorio Pictures
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //le a lista de fotos ja salvas
        File[] files = dir.listFiles();
        //adiciona na lista de fotos
        for (int i = 0; i<files.length; i++){
            photos.add(files[i].getAbsolutePath());
        }

        //cria MainAdapter
        mainAdapter = new MainAdapter(MainActivity.this, photos);
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        //seta o MainAdapter no RecycleView
        rvGallery.setAdapter(mainAdapter);

        //calcula quantas colunas de fotos cabem na tela do celular
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);

        //configura o RecycleView para exibir as fotos em GRID, respeitando o numero maximo de colunas calculado nas linhas acima
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions);

    }

    //metodo que cria um inflador de menu, para crias as opcoes de menu definidas no arquivo de menu passado como parametro e as adiciona no menu da Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    //metodo chamado toda vez q um item da ToolBar for selecionado. Se o icone da camera for clicado, vai excutar o codigo que dispara a camera do celular
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opCamera) {
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //recebe como parametro a foto que devera ser aberta por PhotoActivity
    public void startPhotoActivity(String photoPath){
        //passa o caminho para foto via Intent
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

    private void dispatchTakePictureIntent(){
        //cria um arquivo vazio dentro da pasta Pictures
        File f = null;
        //se o arquivo nao puder ser criado  sera exibida uma mensagem de erro para o usuario
        try {
            f = createImageFile();
        } catch (IOException e){
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        //salva o local do arquivo criado no atributo
        currentPhotoPath = f.getAbsolutePath();

        if(f != null) {
            //gera um endereco URI para o arquivo de foto
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "beretta.prajo.galeria.fileprovider", f);
            //cria uma intent para disparar a app de camera
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //o URI eh passado para a app de camera via intent
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            //inicia a app de camera, que fica esperando pelo resultado (a foto)
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
    }

    //metodo que cria o arquivo que vai guardar a imagem
    private File createImageFile() throws IOException {
        //pega a data e hora para criar um nome diferente para cada imagem
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //cria o nome da foto
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_TAKE_PICTURE){
            //se a foto tiver sido tirada
            if(resultCode == Activity.RESULT_OK){
                //o local da foto eh adicionado na lista de fotos
                photos.add(currentPhotoPath);
                //avisa o MainAdapter de que uma nova foto foi inserida na lista e que o recycleview deve ser atualizado tambem
                mainAdapter.notifyItemInserted(photos.size()-1);
            } else {
                //se a foto nao tiver sido tirada o arquivo criado sera excluido
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    //recebe como entrada uma lista de permissoes
    //metodo que exibe o dialog ao usuario pedindo confirmacao de uso do recurso
    private void checkForPermissions(List<String> permissions){
        List<String> permissionsNotGranted = new ArrayList<>();

        //verifica cada permissao
        for(String permission : permissions) {
            //se o usuario nao tiver confirmado ainda uma permissao ...
            if( !hasPermission(permission)){
                // ... a permissao eh posta em uma lista de permissoes nao confirmadas
                permissionsNotGranted.add(permission);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0){
                //requisita ao usuario as permissoes ainda nao concedidas quando esse metodo for chamado
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }

    //verifica se uma permissao foi concedida ou nao
    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //metodo chamado apos o usuario conceder ou nao as permissoes requisitadas
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        //verifica se a permissao foi concedida ou nao
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permission : permissions){
                if(!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        //verifica se alguma permissao essencial para o funcionamento da app, e exibe uma mensagem informando ao usuario que aquela permissao eh realmente necessaria
        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //verifica se a aplicacao ainda tem permissao para pedir permissao
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //cria uma caixa de dialogo
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //requisita novamente as permissoes
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();

                }
            }
        }
    }
}