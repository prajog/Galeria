package beretta.prajo.galeria;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos){
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    // preenche o ImageView com a foto correspondente
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        //obtem as dimensoes que a imagem vai ter na lista
        int w = (int)mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int)mainActivity.getResources().getDimension(R.dimen.itemHeight);
        //carrega a imagem em um Bitmap ao mesmo tempo em que a foto é escalada para casar com os tamanhos definidos
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);
        //seta o Bitmap no imageView
        imPhoto.setImageBitmap(bitmap);
        //define o que acontece ao clicar em cima de uma imagem
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //app navega para PhotoActivity, que exibe a foto e tamanho ampliado
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
