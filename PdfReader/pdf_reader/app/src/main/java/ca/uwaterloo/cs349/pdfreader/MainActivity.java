package ca.uwaterloo.cs349.pdfreader;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so read this carefully.

public class MainActivity extends AppCompatActivity {

    private class StandardButton extends androidx.appcompat.widget.AppCompatRadioButton {

        public StandardButton(Context context) {
            super(context);
        }
        public StandardButton(Context context, String s) {
            super(context);
            setText(s);
            setWidth(200);
            setHeight(100);
        }
    }

    HashMap dicOfPaths = new HashMap();
    HashMap dicOfPaints = new HashMap();

    HashMap dicOfPathStack = new HashMap();
    HashMap dicOfPaintStack = new HashMap();

    HashMap dicOfRedoStack = new HashMap();
    HashMap dicOfUndoStack = new HashMap();

    final String LOGNAME = "pdf_viewer";
    final String FILENAME = "shannon1948.pdf";
    final int FILERESID = R.raw.shannon1948;

    static final int PEN = 2;
    static final int ERASER = 1;
    static final int ZOOM = 3;

    int curPage = 0;
    int totalPages = 3;

    TextView pg;
    TextView pdfName;

    // manage the pages of the PDF, see below
    PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer.Page currentPage;

    // custom ImageView class that captures strokes and draws them over the image
    PDFimage pageImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LinearLayout toolbarLayout = new LinearLayout(this);
        RadioGroup toolbarGroup = new RadioGroup(this);

        StandardButton pen = new StandardButton(this, "pen");
        StandardButton highlight = new StandardButton(this, "brush");
        StandardButton erase = new StandardButton(this, "eraser");
        StandardButton zp = new StandardButton(this, "zoom");

        final Button redo = new Button(this);
        redo.setText("redo");
        Button undo = new Button(this);
        undo.setText("undo");
        pdfName = new TextView(this);
        pdfName.setText(FILENAME);

        toolbarGroup.addView(pen);
        toolbarGroup.addView(highlight);
        toolbarGroup.addView(erase);
        toolbarGroup.addView(zp);

        toolbarLayout.addView(pdfName);
        toolbarLayout.addView(toolbarGroup);
        toolbarLayout.addView(redo);
        toolbarLayout.addView(undo);

        toolbarGroup.setOrientation(LinearLayout.HORIZONTAL);
        toolbarGroup.setGravity(Gravity.CENTER_HORIZONTAL);
        toolbarLayout.setOrientation(LinearLayout.HORIZONTAL);
        toolbarLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        final LinearLayout layout = findViewById(R.id.pdfLayout);
        pageImage = new PDFimage(this);

        Button previous = new Button(this);
        previous.setText("previous");
        previous.setWidth(340);

        Button next = new Button(this);
        next.setText("next");
        next.setWidth(340);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(previous); l.addView(next);
        l.setGravity(Gravity.CENTER_HORIZONTAL);

        pg = new TextView(this);

        pg.setText(Integer.toString(curPage + 1) + " / 3");
        pg.setGravity(Gravity.CENTER_HORIZONTAL);
        pg.setPadding(8,8,8,8);
        pageImage.setMinimumWidth(1000);
        pageImage.setMinimumHeight(2000);


        layout.addView(toolbarLayout);
        layout.addView(pageImage);
        layout.addView(pg);
        layout.addView(l);
        layout.setEnabled(true);

        final Paint penPaint = new Paint();
        penPaint.setColor(Color.BLUE);
        penPaint.setStyle(Paint.Style.STROKE);
        penPaint.setStrokeWidth(4);
        penPaint.setAntiAlias(true);

        final Paint highlightPaint = new Paint();
        highlightPaint.setColor(Color.YELLOW);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(40);
        highlightPaint.setAntiAlias(true);
        highlightPaint.setAlpha(90);

        pen.setSelected(true);
        pen.setChecked(true);


        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (curPage <= 0) {
                    curPage = 0;
                } else {
                    curPage --;
                }
                pg.setText(Integer.toString(curPage + 1) + " / " + totalPages);
                pageImage.setPaths((ArrayList<Path>) dicOfPaths.get(curPage));
                pageImage.setColors((ArrayList<Paint>) dicOfPaints.get(curPage));

                pageImage.setPathStack((Stack<Path>) dicOfPathStack.get(curPage));
                pageImage.setPaintStack((Stack<Paint>) dicOfPaintStack.get(curPage));

                pageImage.setRedoStack((Stack<Job>) dicOfRedoStack.get(curPage));
                pageImage.setUndoStack((Stack<Job>) dicOfUndoStack.get(curPage));
                showPage(curPage);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (curPage >= totalPages - 1) {
                    curPage = totalPages - 1;
                } else {
                    curPage ++;
                }
                pg.setText(Integer.toString(curPage + 1) + " / " + totalPages);
                pageImage.setPaths((ArrayList<Path>) dicOfPaths.get(curPage));
                pageImage.setColors((ArrayList<Paint>) dicOfPaints.get(curPage));

                pageImage.setPathStack((Stack<Path>) dicOfPathStack.get(curPage));
                pageImage.setPaintStack((Stack<Paint>) dicOfPaintStack.get(curPage));

                pageImage.setRedoStack((Stack<Job>) dicOfRedoStack.get(curPage));
                pageImage.setUndoStack((Stack<Job>) dicOfUndoStack.get(curPage));
                showPage(curPage);

            }
        });

        pen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageImage.setBrush(penPaint);
                pageImage.setMode(PEN);
            }
        });

        highlight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageImage.setBrush(highlightPaint);
                pageImage.setMode(PEN);
            }
        });
        erase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageImage.setMode(ERASER);
            }
        });
        zp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageImage.setMode(ZOOM);
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Stack<Job> redoTemp = (Stack<Job>) dicOfRedoStack.get(curPage);
                Stack<Job> undoTemp = (Stack<Job>) dicOfUndoStack.get(curPage);
                if (!redoTemp.empty()) {
                    Job theJob = (Job) redoTemp.pop();
                    doJob(theJob, true, curPage);
                    undoTemp.push(theJob);
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Stack<Job> redoTemp = (Stack<Job>) dicOfRedoStack.get(curPage);
                Stack<Job> undoTemp = (Stack<Job>) dicOfUndoStack.get(curPage);
                if (!undoTemp.empty()) {
                    Job theJob = (Job) undoTemp.pop();
                    doJob(theJob, false, curPage);
                    redoTemp.push(theJob);
                }
            }
        });

        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
            openRenderer(this);
            pageImage.setBrush(penPaint);
            for (int i = 0; i < totalPages; i++) {
                dicOfPaths.put(i, new ArrayList<Path>());
                dicOfPaints.put(i, new ArrayList<Paint>());

                dicOfPathStack.put(i, new Stack<Path>());
                dicOfPaintStack.put(i, new Stack<Paint>());

                dicOfRedoStack.put(i, new Stack<Job>());
                dicOfUndoStack.put(i, new Stack<Job>());
            }
            pageImage.setPaths((ArrayList<Path>) dicOfPaths.get(0));
            pageImage.setColors((ArrayList<Paint>) dicOfPaints.get(0));

            pageImage.setPathStack((Stack<Path>) dicOfPathStack.get(0));
            pageImage.setPaintStack((Stack<Paint>) dicOfPaintStack.get(0));

            pageImage.setRedoStack((Stack<Job>) dicOfRedoStack.get(0));
            pageImage.setUndoStack((Stack<Job>) dicOfUndoStack.get(0));
            showPage(curPage);

        } catch (IOException exception) {
            Log.d(LOGNAME, "Error opening PDF");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();
        try {
            closeRenderer();
        } catch (IOException ex) {
            Log.d(LOGNAME, "Unable to close PDF renderer");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            InputStream asset = this.getResources().openRawResource(FILERESID);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            totalPages = pdfRenderer.getPageCount();
        }

    }

    // do this before you quit!
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    private void doJob(Job job, boolean isRedo, int cPage) {
        if (isRedo) {
            if (job.isEraser()) {
                // redo eraser, move from pathlist to pathstack
                ArrayList<Path> pathListTemp = (ArrayList<Path>) dicOfPaths.get(cPage);
                ArrayList<Paint> paintListTemp = (ArrayList<Paint>) dicOfPaints.get(cPage);
                Stack<Path> pathStackTemp = (Stack<Path>) dicOfPathStack.get(cPage);
                Stack<Paint> paintStackTemp = (Stack<Paint>) dicOfPaintStack.get(cPage);
                for (int i = 0; i < job.getNumber(); i++) {
                    Path pathTemp = pathListTemp.get(pathListTemp.size() - 1);
                    Paint paintTemp = paintListTemp.get(paintListTemp.size() - 1);
                    pathListTemp.remove(pathListTemp.size() - 1);
                    paintListTemp.remove(paintListTemp.size() - 1);
                    pathStackTemp.push(pathTemp);
                    paintStackTemp.push(paintTemp);
                }
            } else {
                // redraw , move from pathstack to pathlist
                Stack<Path> pathStackTemp = (Stack<Path>) dicOfPathStack.get(cPage);
                Stack<Paint> paintStackTemp = (Stack<Paint>) dicOfPaintStack.get(cPage);
                ArrayList<Path> pathListTemp = (ArrayList<Path>) dicOfPaths.get(cPage);
                ArrayList<Paint> paintListTemp = (ArrayList<Paint>) dicOfPaints.get(cPage);
                Path pathTemp = pathStackTemp.pop();
                Paint paintTemp = paintStackTemp.pop();
                pathListTemp.add(pathTemp);
                paintListTemp.add(paintTemp);
            }
        } else {
            if (job.isEraser()) {
                // undo eraser, move from stack to list
                ArrayList<Path> pathListTemp = (ArrayList<Path>) dicOfPaths.get(cPage);
                ArrayList<Paint> paintListTemp = (ArrayList<Paint>) dicOfPaints.get(cPage);
                Stack<Path> pathStackTemp = (Stack<Path>) dicOfPathStack.get(cPage);
                Stack<Paint> paintStackTemp = (Stack<Paint>) dicOfPaintStack.get(cPage);
                for (int i = 0; i < job.getNumber(); i++) {
                    Path pathTemp = pathStackTemp.pop();
                    Paint paintTemp = paintStackTemp.pop();
                    pathListTemp.add(pathTemp);
                    paintListTemp.add(paintTemp);

                }
            } else {
                // undraw, move from list to stack
                ArrayList<Path> pathListTemp = (ArrayList<Path>) dicOfPaths.get(cPage);
                ArrayList<Paint> paintListTemp = (ArrayList<Paint>) dicOfPaints.get(cPage);
                Stack<Path> pathStackTemp = (Stack<Path>) dicOfPathStack.get(cPage);
                Stack<Paint> paintStackTemp = (Stack<Paint>) dicOfPaintStack.get(cPage);
                Path pathTemp = pathListTemp.get(pathListTemp.size() - 1);
                Paint paintTemp = paintListTemp.get(paintListTemp.size() - 1);
                pathListTemp.remove(pathListTemp.size() - 1);
                paintListTemp.remove(paintListTemp.size() - 1);
                pathStackTemp.push(pathTemp);
                paintStackTemp.push(paintTemp);
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        System.out.println("111111111111111");
        // Close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        System.out.println("222222222222222222");
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        System.out.println("333333333333333333");
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        System.out.println("444444444444444444");
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        System.out.println("555555555555555555555");
        // Display the page
        pageImage.setImage(bitmap);
        pg.setText(Integer.toString(curPage + 1) + " / " + totalPages);
    }
}
