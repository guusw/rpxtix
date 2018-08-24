package nl.tdrz.freerpx;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class TicketViewer extends AppCompatActivity {
    public static final String TAG = "freerpx";
    private RequestQueue requestQueue;
    private PdfiumCore pdfiumCore;

    private RenderedTicket renderedTicket;
    private Ticket ticket;

    private int computedTicketViewWidth;
    private int computedTicketViewHeight;
    private boolean ticketViewSizeComputed;

    private TextView holderTextView;

    private View flipContainer;
    private Fragment currentTicketSide;

    private void customizeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(R.string.ticket_viewer_title);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);


        //assign the view to the actionbar
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layoutParams);

        // Custom arrow color in the action bar
        //final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        //upArrow.setColorFilter(getResources().getColor(R.color.ns_blauw), PorterDuff.Mode.SRC_ATOP);
        //getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_viewer);

        customizeActionBar();

        holderTextView = findViewById(R.id.holderName);
        holderTextView.setText("");

        pdfiumCore = new PdfiumCore(getApplicationContext());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        window.setAttributes(params);

        final View imageFrameView = findViewById(R.id.imageFrame);

        ViewTreeObserver viewTreeObserver = imageFrameView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageFrameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    computedTicketViewWidth = imageFrameView.getWidth();
                    computedTicketViewHeight = imageFrameView.getHeight();
                    ticketViewSizeComputed = true;
                    // Start loading ticket
                    loadTicketAsync();
                }
            });
        }
    }

    private void loadTicketAsync() {
        AssetManager am = getResources().getAssets();
        try {
            InputStream stream = am.open("ticket.json");
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, Charset.defaultCharset());
            String jsonString = writer.toString();

            Gson gson = new Gson();
            TicketResponse ticketResponse = gson.fromJson(jsonString, TicketResponse.class);
            if (ticketResponse.tickets.isEmpty()) {
                throw new Exception("No tickets found in response");
            }

            ticket = ticketResponse.tickets.get(0);
            holderTextView.setText(ticket.initials + " " + ticket.lastName);

            requestQueue = Volley.newRequestQueue(getApplicationContext());
            downloadPDFAsync(ticket.pdfLinkNl);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to load ticket: ", ex);
        }
    }

    private void downloadPDFAsync(String url) {
        ByteRequest req = new ByteRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        Log.d(TAG, "onResponse: Got PDF of length: " + response.length);

                        try {
                            renderedTicket = renderPDF(response);
                            onTicketRendered();
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: Failed to render PDF", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: PDF Download error", error);
                    }
                });
        requestQueue.add(req);
    }

    private RenderedTicket renderPDF(byte[] pdfData) throws Exception {
        RenderedTicket result = new RenderedTicket();

        PdfDocument doc = pdfiumCore.newDocument(pdfData);
        try {
            int pageCount = pdfiumCore.getPageCount(doc);
            if (pageCount < 2) {
                throw new Exception("Invalid page count, expected at least 2, got " + pageCount);
            }

            pdfiumCore.openPage(doc, 0, 1);
            for (int pageID = 0; pageID < 2; pageID++) {
                int width = pdfiumCore.getPageWidthPoint(doc, pageID);
                int height = pdfiumCore.getPageHeightPoint(doc, pageID);
                float dyx = (float) height / (float) width;
                float viewDyx = (float) computedTicketViewHeight / (float) computedTicketViewWidth;
                if (dyx > viewDyx) {
                    // Bars on the left/right
                    height = computedTicketViewHeight;
                    width = (int) ((float) height / dyx);
                } else {
                    // Bars on the top/bottom
                    width = computedTicketViewWidth;
                    height = (int) ((float) height * dyx);
                }

                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = Bitmap.createBitmap(width, height, conf);

                pdfiumCore.renderPageBitmap(doc, bitmap, pageID, 0, 0, width, height);
                result.pages[pageID] = bitmap;
            }
        } catch (Exception ex) {
            pdfiumCore.closeDocument(doc);
            throw ex;
        }

        return result;
    }

    private void onTicketRendered() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.flipContainer, (Fragment) new CardFragment(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flip();
                    }
                },renderedTicket.pages[0]))
                .commit();
    }

    public static class CardFragment extends Fragment {
        private View.OnClickListener flipButtonHandler;
        private Bitmap bitmap;

        public CardFragment(View.OnClickListener flipButtonHandler, Bitmap bitmap) {
            this.flipButtonHandler = flipButtonHandler;
            this.bitmap = bitmap;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.ticket_side, container, false);

            View flipButton = view.findViewById(R.id.flipButton);
            flipButton.setOnClickListener(flipButtonHandler);

            ImageView image = view.findViewById(R.id.image);
            image.setImageBitmap(bitmap);
            return view;
        }
    }

    private boolean showingFrontSide = true;

    private void flip() {
        if (!showingFrontSide) {
            getFragmentManager().popBackStack();
            showingFrontSide = true;
            return;
        }

        // Flip to the back.
        showingFrontSide = false;

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.flip_right_in,
                        R.animator.flip_right_out,
                        R.animator.flip_left_in,
                        R.animator.flip_left_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(R.id.flipContainer, new CardFragment(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flip();
                    }
                }, renderedTicket.pages[1]))

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
