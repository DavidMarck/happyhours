package com.example.david.happyhours;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by David on 23/05/2016.
 */
public class BarAdapter extends ArrayAdapter<BarRow> {

    public List<BarRow> barsListe;
    public ArrayList<BarRow> resultRecherche;

    /**
     * Constructeur
     *
     * @param context
     * @param bars liste contenant toutes les informations relatives aux bars à afficher
     */
    public BarAdapter(Context context, List<BarRow> bars) {
        super(context,0,bars);
        this.barsListe = bars;
        resultRecherche = new ArrayList<BarRow>();
        resultRecherche.addAll(barsListe);
    }

    /**
     * Instancie les vues (items de listViewBars) ou les ré-utilise
     *
     * @param position
     * @param convertView vue recyclée
     * @param parent
     * @return vue recyclée
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Android fournit un convertView null lorsqu'il nous demande de la créer
        // dans le cas contraire, cela veux dire qu'il nous fournit une vue recyclée
        if(convertView == null){
            // On récupère row_bar via un LayoutInflater,
            // qui va charger un layout xml dans un objet View
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bar,parent, false);
        }

        BarViewHolder viewHolder = (BarViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BarViewHolder();
            viewHolder.bar_nom = (TextView) convertView.findViewById(R.id.bar_nom);
            viewHolder.bar_hh = (TextView) convertView.findViewById(R.id.bar_hh);
            viewHolder.bar_adresse = (TextView) convertView.findViewById(R.id.bar_adresse);
            viewHolder.bar_horaires = (TextView) convertView.findViewById(R.id.bar_horaires);
            viewHolder.btn_favori = (ImageButton) convertView.findViewById(R.id.btn_favori);
            viewHolder.btn_barathon = (ImageButton) convertView.findViewById(R.id.btn_barathon);
            viewHolder.btn_geoloc = (ImageButton) convertView.findViewById(R.id.btn_geoloc);
            convertView.setTag(viewHolder);
        }

        // getItem(position) récupère l'item [position] de la List<BarRow> barsRows
        // déclarations final à fin d'utilisation dans OnClickListener
        final BarRow barRow = getItem(position);
        final BarDAO barDAO = new BarDAO(getContext());
        final ImageButton imgBtn = viewHolder.btn_favori;

        // Remplissage de la vue
        viewHolder.bar_nom.setText(barRow.getNom());
        viewHolder.bar_hh.setText(barRow.getHappy_hours());
        viewHolder.bar_adresse.setText(barRow.getAdresse());
        viewHolder.bar_horaires.setText(barRow.getHoraires());
        viewHolder.bar_adresse.setText(barRow.getAdresse());

        // On récupère la bar au nom correspondant dans la base de données
        Cursor cursor = barDAO.getDatabase().query(barDAO.TABLE_BAR, barDAO.allColumnBar, barDAO.COLUMN_BAR_NOM + " = ? ", new String[] {barRow.getNom()}, null, null, null);
        cursor.moveToFirst();
        Bar bar = barDAO.cursorToBar(cursor);
        cursor.close();
        // On définit l'image du bouton favori en fonction de l'attribut estFavori
        if(bar.getEstFavori() == 1) {
            viewHolder.btn_favori.setImageResource(R.drawable.ic_favorite_enabled);
            imgBtn.setTag("R.drawable.ic_favorite_enabled");
        } else {
            viewHolder.btn_favori.setImageResource(R.drawable.ic_favorite_disabled);
            imgBtn.setTag("R.drawable.ic_favorite_disabled");
        }

        // Ajout des Listener aux boutons
        viewHolder.btn_favori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ContentValues contentValues = new ContentValues();
                if(imgBtn.getTag().equals("R.drawable.ic_favorite_disabled")) {
                    imgBtn.setImageResource(R.drawable.ic_favorite_enabled);
                    imgBtn.setTag("R.drawable.ic_favorite_enabled");
                    contentValues.put(barDAO.COLUMN_BAR_FAV,1);
                    barDAO.updateBar(contentValues,barDAO.COLUMN_BAR_NOM + " = ?",new String[] {barRow.getNom()});
                    genererToast("Bar \"" + barRow.getNom() + "\" ajouté aux favoris",500);
                } else {
                    // On demande confirmation pour enlever le bar des favoris
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Retrait des favoris")
                            .setMessage("Êtes-vous sûr de vouloir retirer le bar \"" + barRow.getNom() + "\" des favoris?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    imgBtn.setImageResource(R.drawable.ic_favorite_disabled);
                                    imgBtn.setTag("R.drawable.ic_favorite_disabled");
                                    contentValues.put(barDAO.COLUMN_BAR_FAV,0);
                                    barDAO.updateBar(contentValues,barDAO.COLUMN_BAR_NOM + " = ?",new String[] {barRow.getNom()});
                                    if(getContext().getClass().getSimpleName().equals("favoris")) {
                                        for(BarRow unBarRow : barsListe) {
                                            if(unBarRow.getNom().equals(barRow.getNom())){
                                                barsListe.remove(unBarRow);
                                                break;
                                            }
                                        }
                                        notifyDataSetChanged();
                                    } else {
                                        genererToast("Bar \"" + barRow.getNom() + "\" retiré des favoris",500);
                                    }
                                }
                            })
                            .setNegativeButton("Non", null)
                            .show();
                }
            }
        });
        viewHolder.btn_barathon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genererToast("btn_barathon is clicked!",500);
            }
        });
        viewHolder.btn_geoloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genererToast("btn_geoloc is clicked!",500);
            }
        });

        return convertView;
    }

    private class BarViewHolder {
        public TextView bar_nom,bar_hh,bar_adresse,bar_horaires;
        public ImageButton btn_favori,btn_barathon,btn_geoloc;
    }

    /**
     * Génère un Toast, l'affiche et l'annule après une duree
     *
     * @param text
     * @param duree
     */
    public void genererToast(String text, int duree) {
        final Toast toast = Toast.makeText(getContext(),
                text, Toast.LENGTH_SHORT);
        toast.show();
        // N'afficher le Toast que 500ms et l'annuler
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duree);
    }

    /**
     * Gestion du filtrage de la recharche (SearchView)
     *
     * @param charText séquence à filtrer
     */
    public void filter (String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        barsListe.clear();
        if(charText.length() == 0) {
            barsListe.addAll(resultRecherche);
        } else {
            for(BarRow aBarRow : resultRecherche) {
                if (charText.length() != 0 && aBarRow.getNom().toLowerCase(Locale.getDefault()).contains(charText)) {
                    barsListe.add(aBarRow);
                } else if (charText.length() != 0 && aBarRow.getAdresse().toLowerCase(Locale.getDefault()).contains(charText)) {
                    barsListe.add(aBarRow);
                }
            }
        }
        notifyDataSetChanged();
    }
}
