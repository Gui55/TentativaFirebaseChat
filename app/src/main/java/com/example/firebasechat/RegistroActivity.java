package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegistroActivity extends AppCompatActivity {

    private EditText cadNome, cadEmail, cadSenha;

    private Button btnCadastro, btnFoto;

    private Uri uri;

    private ImageView circIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Associação dos componentes

        cadNome = findViewById(R.id.cadastroNome);
        cadEmail = findViewById(R.id.cadastroEmail);
        cadSenha = findViewById(R.id.cadastroPass);

        btnFoto = findViewById(R.id.btnFoto);
        btnCadastro = findViewById(R.id.btnCadastro);

        circIm = findViewById(R.id.circImageView);


        //Ação do botão de foto

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Chamar o método para selecionar foto

                selecionarFoto();

            }
        });

        //Ação do botão de cadastro

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //Chamar o método para criar o usuário

                criarUsuario();

            }
        });

    }

    private void selecionarFoto() {

        //Nesse intent ele vai abrir a galeria do celular do próprio usuário ao invés de uma outra
        //activity do aplicativo

        Intent intent = new Intent(Intent.ACTION_PICK);


        //O método abaixo define o tipo de arquivo à ser buscado

        intent.setType("image/*");

        startActivityForResult(intent, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0){
            uri = data.getData();

            Bitmap im = null;

            try {

                /*

                Ao invés de

                im = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                use:

                 */

                im = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                circIm.setImageBitmap(im);
                btnFoto.setAlpha(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void criarUsuario() {

        //Pegar o que o usuário digitou nos EditTexts da activity de cadastro

        String nome = cadNome.getText().toString();
        String email = cadEmail.getText().toString();
        String senha = cadSenha.getText().toString();

        //Se o e-mail e a senha forem nulos ou vazios, exiba um toast alertando o usuário à preenchê-los

        if(nome==null ||
        nome.isEmpty() ||
        email==null ||
        email.isEmpty() ||
        senha == null ||
        senha.isEmpty()){

            Toast.makeText(this, "Faltam dados à serem preenchidos", Toast.LENGTH_SHORT).show();

        }

        //Método do firebaseAuth para cadastrar um usuário com um e-mail e uma senha
        //(nesse caso feito automaticamente pelo firebase em si).

        //Nele, é necessário implementar um escutador de sucesso e um escutador de erro

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Se o cadastro tiver sucesso, imprima no Log o uid de usuário

                if(task.isSuccessful()){
                    Log.i("test", task.getResult().getUser().getUid());

                    salvarUsuarioNoFirebase();
                }


            }
        })
        .addOnFailureListener(new OnFailureListener() { /*Se o cadastro der errado, o escutador de erros
            fará um Log mostrando o erro*/

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("test", e.getMessage());
            }
        });

    }

    private void salvarUsuarioNoFirebase() {

        //Gera uma hash aleatoria para um nome de arquivo
        String nomeArquivo = UUID.randomUUID().toString();

        final StorageReference ref = FirebaseStorage.getInstance()
                .getReference("/images/" + nomeArquivo);


        //Para subir a foto
        ref.putFile(uri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { // Escutador de sucesso
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                //Escutador de sucesso na url de download
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri ur) {
                        Log.i("Teste", ur.toString());
                    }
                });


            }
        })
        .addOnFailureListener(new OnFailureListener() { // Escutador de falha
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("tstT", e.getMessage(), e);
            }
        });


    }
}
