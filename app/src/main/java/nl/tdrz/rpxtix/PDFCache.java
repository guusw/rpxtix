package nl.tdrz.rpxtix;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.charset.Charset;

public class PDFCache {
    private static final String CACHE_FOLDER_NAME = "ticket_pdf_cache";
    private static final String TAG = "rpxtix";

    private static PDFCache instance;

    private Context context;
    private File ticketCacheFolder;

    public static PDFCache getOrCreateInstance(Context context) {
        if(instance == null) {
            instance = new PDFCache(context);
        }
        return instance;
    }

    public PDFCache(Context context) {
        this.context = context;
        ticketCacheFolder = new File(context.getCacheDir(), CACHE_FOLDER_NAME);
        if(!ticketCacheFolder.exists()) {
            ticketCacheFolder.mkdirs();
        }
    }

    public String uniquePDFFileName(Ticket ticket) {
        return String.format("%s_%s.pdf", ticket.orderId, ticket.travelerId);
    }

    public byte[] get(Ticket ticket) {
        String fileName = uniquePDFFileName(ticket);
        File pdfFile = new File(ticketCacheFolder, fileName);
        try {
            FileInputStream reader = new FileInputStream(pdfFile);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while(true) {
                int read = reader.read(buffer);
                bytes.write(buffer, 0, read);
                if(read == 0 || reader.available() == 0) {
                    break;
                }
            }
            return bytes.toByteArray();
        }catch(Exception e) {
            Log.i(TAG, "get: Failed to find cached pdf with name " + fileName + ": " + e);
        }
        return null;
    }

    public void setOrUpdate(Ticket ticket, byte[] pdfData) {

        String fileName = uniquePDFFileName(ticket);
        File pdfFile = new File(ticketCacheFolder, fileName);
        try {
            FileOutputStream writer = new FileOutputStream(pdfFile);
            writer.write(pdfData);
        }catch(Exception e) {
            Log.i(TAG, "get: Failed to store pdf in cache with name " + fileName + ": " + e);
        }
    }

    public void delete(Ticket ticket) {
        String fileName = uniquePDFFileName(ticket);
        File pdfFile = new File(ticketCacheFolder, fileName);
        pdfFile.delete();
    }
}
