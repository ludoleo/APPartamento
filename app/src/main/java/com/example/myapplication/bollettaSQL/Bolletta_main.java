package com.example.myapplication.bollettaSQL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Bolletta_main extends Activity {
    DatabaseHelper databaseHelper;
    public ListView listView;
    ArrayList<Bolletta> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolletta_main);
        listView = findViewById(R.id.list_boll);
     databaseHelper = new DatabaseHelper(this);
     populateView();
     CustomAdapter adapter = new CustomAdapter(this,R.layout.riga_bollette_list, listData);
     listView.setAdapter(adapter);


    }

    private void populateView() {
        Cursor data = databaseHelper.getdata();
        while(data.moveToNext())
        {
            String name = data.getString(1);
            byte []image =data.getBlob(2);
            listData.add(new Bolletta(name,image));

        }
    }
    public class CustomAdapter extends BaseAdapter
    {
        private Context context;
        private int layout;
        ArrayList<Bolletta> textList;
        public CustomAdapter (Context context, int layout,ArrayList<Bolletta>textList){

            this.context = context;
            this.layout = layout;
            this.textList = textList;
        }


        @Override
        public int getCount() {
            return textList.size();
        }

        @Override
        public Object getItem(int position) {
            return textList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        private class ViewHolder {
            ImageView Imageview1;
            TextView textName;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View row = view;
            ViewHolder holder;
            if (row == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layout,null);
                holder = new ViewHolder();
                holder.textName = (TextView) row.findViewById(R.id.bulet);
                holder.Imageview1= (ImageView) row.findViewById(R.id.imageViewbulet);
                row.setTag(holder);
            }
            else {
                holder =(ViewHolder) row.getTag();
            }
            final Bolletta bolletta = textList.get(position);
            holder.textName.setText(bolletta.getName());
            byte [] bollettaImage = bolletta.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bollettaImage,0,bollettaImage.length);
            holder.Imageview1.setImageBitmap(bitmap);
            /*
            row.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor data = databaseHelper.getItemId(bolletta.getName());
                    int itemId = -1;
                    String BollettaName = "";
                    byte [] BollettaImg = null;
                    while (data.moveToNext()){
                        itemId = data.getInt(0);
                        BollettaName = data.getString(1);
                        BollettaImg = data.getBlob(2);
                        Intent Editintent = new Intent(Bolletta_main.this,EditBolletta.class);
                        Editintent.putExtra("id",itemId);
                        Editintent.putExtra("name",bolletta.getName());
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bs);
                        Editintent.putExtra("byteArray",bs.toByteArray());
                        startActivity(Editintent);
                    }
                    if (itemId > -1){
                        Toast.makeText(Bolletta_main.this,"On Item Click: the id is: "+ itemId+" "+BollettaName+" "+ BollettaImg,Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Bolletta_main.this,"No Data",Toast.LENGTH_LONG).show();
                    }

                }
            });*/
            return row;
        }
    }
}