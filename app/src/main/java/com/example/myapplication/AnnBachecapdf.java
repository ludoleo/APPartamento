package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnnBachecapdf extends AppCompatActivity {
    EditText nomepdf,indirizzopdf,prezzopdf,nomeprop;
    Button invio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ann_bachecapdf);
        nomepdf= findViewById(R.id.editTextNomepdf);
        indirizzopdf=findViewById(R.id.editTextViapdf);
        prezzopdf=findViewById(R.id.editTextPrezzopdf);
        invio= findViewById(R.id.Buttonpdf);
        nomeprop= findViewById(R.id.editTextProppdf);
        invio.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String nome= nomepdf.getText().toString();
                String indirizzo= indirizzopdf.getText().toString();
                String prezzo= prezzopdf.getText().toString();
                String nomeProp = nomeprop.getText().toString();
                try {
                    createPdf(nome,nomeProp,indirizzo,prezzo);
                }
                catch (FileNotFoundException e ){
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createPdf(String nome,String nomeProp,String indirizzo, String prezzo) throws FileNotFoundException{
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath,"myPDF. pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0,0,0,0);
        Drawable d = getDrawable(R.drawable.sfondo_logo);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        Paragraph annuncio = new Paragraph("Annuncio completo visibile sull'app Uniaffitto").setBold().setFontSize(22).setTextAlignment(TextAlignment.CENTER);
        Paragraph nuovoann = new Paragraph("Annuncio").setFontSize(14).setTextAlignment(TextAlignment.CENTER);
        Paragraph annuncios = new Paragraph("Affitto camera").setBold().setFontSize(22).setTextAlignment(TextAlignment.CENTER);
        // creo tabella
        float [] width = {100f,100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell().add(new Paragraph("Nome Annuncio")));
        table.addCell(new Cell().add(new Paragraph(nome)));

        table.addCell(new Cell().add(new Paragraph("Nome Propietario")));
        table.addCell(new Cell().add(new Paragraph(nomeProp)));

        table.addCell(new Cell().add(new Paragraph("Indirizzo")));
        table.addCell(new Cell().add(new Paragraph(indirizzo)));

        table.addCell(new Cell().add(new Paragraph("Prezzo")));
        table.addCell(new Cell().add(new Paragraph(prezzo+"€")));
        // QR code
        BarcodeQRCode qrCode = new BarcodeQRCode(nome+"\n"+nomeProp+"\n"+indirizzo+"\n"+prezzo+"\n");
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
         Image qrCodeImage  = new Image(qrCodeObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);
         // tutti gli Object nel document
        document.add(image);
        document.add(annuncio);
        document.add(nuovoann);
        document.add(annuncios);
        document.add(table);
        document.add(qrCodeImage);

        document.close();
        Toast.makeText(AnnBachecapdf.this,"PDF Creato",Toast.LENGTH_SHORT).show();



    }
}