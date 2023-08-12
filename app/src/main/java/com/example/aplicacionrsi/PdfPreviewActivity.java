package com.example.aplicacionrsi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.print.PrintManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.shockwave.pdfium.PdfDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfPreviewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public String TAG = "PDF";
    private String currency_sign;
    Font normalWhite = new Font(FontFamily.HELVETICA, 12.0f, 0, BaseColor.WHITE);
    Integer pageNumber = Integer.valueOf(0);
    String pdfFileName;
    PDFView pdfView;
    BaseFont textBaseFont;
    Font textFont = new Font(FontFamily.TIMES_ROMAN, 14.0f);
    Font titleFont = new Font(FontFamily.TIMES_ROMAN, 18.0f, 1, BaseColor.BLACK);
    private Toolbar toolbar;
    private float widthParcentage = 90.0f;
    private static final Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 26, Font.BOLDITALIC);
    private static final Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
    private static final Font categoryFont = new Font(FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font subcategoryFont  = new Font(FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    String idFactura;
    String path;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_preview);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        Bundle param = this.getIntent().getExtras();
        if(param != null){
            path = param.getString("path");
            //Toast.makeText(this, "Path : "+path, Toast.LENGTH_LONG).show();
        }

        try {
            initLayout();
            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                //  createPDF();
                viewPdf();
                return;
            }
            requestPermission(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLayout() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setTitle((CharSequence) "PDF");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.pdfView = (PDFView) findViewById(R.id.pdfView);
    }

    private static void requestPermission(final Context context) {
        Activity activity = (Activity) context;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            new AlertDialog.Builder(context).setMessage(context.getResources().getString(R.string.permission_storage)).setPositiveButton((CharSequence) "ok", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
                }
            }).setNegativeButton((CharSequence) "no", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 101) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else if (iArr.length != 0 && iArr[0] == 0) {
         //   createPDF();
        }
    }

    private boolean checkString(String str) {
        return !TextUtils.isEmpty(str);
    }


    private void viewPdf() {
        this.pdfFileName = path;
        this.pdfView.fromFile(new File(this.pdfFileName)).defaultPage(this.pageNumber.intValue()).enableSwipe(true).swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).pageFitPolicy(FitPolicy.BOTH).spacing(10).load();
        this.pdfView.setMaxZoom(1.0f);
        this.pdfView.setBackgroundColor(ContextCompat.getColor(this, R.color.rng_gray_lite));
    }


    public void onPageChanged(int i, int i2) {
        this.pageNumber = Integer.valueOf(i);
        setTitle(String.format("%s %s / %s", new Object[]{this.pdfFileName, Integer.valueOf(i + 1), Integer.valueOf(i2)}));
    }

    public void loadComplete(int i) {
        printBookmarksTree(this.pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> list, String str) {
        for (PdfDocument.Bookmark bookmark : list) {
            if (bookmark.hasChildren()) {
                List children = bookmark.getChildren();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("-");
                printBookmarksTree(children, stringBuilder.toString());
            }
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
