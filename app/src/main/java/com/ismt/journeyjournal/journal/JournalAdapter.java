package com.ismt.journeyjournal.journal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ismt.journeyjournal.Dashboard;
import com.ismt.journeyjournal.Journal;
import com.ismt.journeyjournal.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private final Context context;
    private List<JournalEntities> entities;
    private JournalListeners journalListeners;
    AlertDialog dialogDelete;
    private Timer timer;
    private List<JournalEntities> source;
    String title, textDsc;
                                           

    public JournalAdapter(List<JournalEntities> entities, JournalListeners journalListeners, Context context) {
        this.entities = entities;
        this.journalListeners = journalListeners;
        source = entities;
        this.context = context;

    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JournalViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.journal_data_listing,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setJournal(entities.get(position));
        holder.journalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journalListeners.onJournalClicked(entities.get(position), position);
            }
        });

        holder.menuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete:
                                showDeleteDialog(position);
                                break;

                            case R.id.menu_share:
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                Drawable drawable = holder.imageJournal.getDrawable();
                                Bitmap bitmap = drawableToBitmap(drawable);//put here your image id
                                String path = entities.get(position).getImageJournal();
                                OutputStream out = null;
                                File file = new File(path);
                                try {
                                    out = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.flush();
                                    out.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                path = file.getPath();
                                Uri bmpUri = Uri.parse("file://" + path);
                                Intent shareIntent = new Intent();
                                shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
                                shareIntent.putExtra(
                                        Intent.EXTRA_TEXT, entities.get(position).title  + "\n" +  entities.get(position).subTitle

                                );
                                shareIntent.setType("image/png");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(Intent.createChooser(shareIntent, "Share with"));
                        }
                        return false;
                    }
                });
            }
        });

    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    void showDeleteDialog(@SuppressLint("RecyclerView") final int position){
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations
                = android.R.style.Animation_Dialog;
        dialog.setContentView(R.layout.popup_delete);

        TextView textCancel = dialog.findViewById(R.id.textCancel);
        TextView textDelete = dialog.findViewById(R.id.textDelete);

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
                Toast.makeText(view.getContext(), "Journal deleted successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteItem(int position) {
        journalListeners.onDelete(entities.get(position));
        entities.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,entities.size());
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public int getItemViewType(int position) {
        return position;
    }


    static class JournalViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout journalLayout;
        TextView title, subtitle, dateTime;
        RoundedImageView imageJournal;
        ImageView menuPopup;

        JournalViewHolder(@Nullable View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.textDsc);
            dateTime = itemView.findViewById(R.id.dateTime);
            imageJournal = itemView.findViewById(R.id.imageJournal);
            journalLayout = itemView.findViewById(R.id.journalLayout);
            menuPopup = itemView.findViewById(R.id.more);
        }

        void setJournal(JournalEntities entities) {
            title.setText(entities.getTitle());
            if (entities.getSubTitle().trim().isEmpty()) {
                subtitle.setVisibility(View.GONE);
            } else {
                subtitle.setText(entities.getSubTitle());
            }
            dateTime.setText(entities.getDateTime());

            if (entities.getImageJournal() != null) {
                imageJournal.setImageBitmap(BitmapFactory.decodeFile(entities.getImageJournal()));
                imageJournal.setVisibility(View.VISIBLE);
            } else {
                imageJournal.setVisibility(View.GONE);
            }
        }
    }

    public void searchJournals(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    entities = source;
                } else {
                    ArrayList<JournalEntities> temp = new ArrayList<>();
                    for (JournalEntities entities : source) {
                        if (entities.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || entities.getSubTitle().toLowerCase().contains(searchKeyword.toLowerCase())) {

                            temp.add(entities);
                        }
                    }
                    entities = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
