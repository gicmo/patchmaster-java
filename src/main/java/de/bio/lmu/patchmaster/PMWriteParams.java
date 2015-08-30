package de.bio.lmu.patchmaster;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PMWriteParams {

    public static Record loadPuseRecords(String path) throws IOException {

        Bundle b = new Bundle(path);
        b.readHeader();
        Record root = b.loadTree(".pul");
        return root;
    }

    public static void main(String[] args) {
        PDDocument doc = null;
        PDPage page = null;

        try{

            Record root = loadPuseRecords("/Users/gicmo/Coding/G-Node/patchmaster/2012_12_03c.dat");
            Record group = root.getChildren()[0];

            doc = new PDDocument();
            page = new PDPage();

            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA;
            float fontsize = 8;
            float fheight = font.getFontDescriptor().getFontBoundingBox().getHeight()/1000;
            fheight = fheight * 1.05f * fontsize;

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.beginText();
            content.setFont(font, fontsize);


            Record[] children = group.getChildren();

            float y = 780;
            content.moveTextPositionByAmount (50, y);

            content.setFont(PDType1Font.HELVETICA_BOLD, fontsize);
            content.drawString("Label ");
            content.moveTextPositionByAmount (100, 0);
            content.drawString("R value");
            content.moveTextPositionByAmount (50, 0);
            content.drawString(String.format("R fraction"));
            content.moveTextPositionByAmount (50, 0);
            content.drawString(String.format("R Delwen"));
            content.moveTextPositionByAmount (50, 0);
            content.drawString(String.format("C-Slow"));
            content.moveTextPositionByAmount (-250, -fheight);

            content.setFont(font, fontsize);

            for (int i = 0; i < children.length; i++) {
                Record child = children[i];
                String label = child.decodeString("Label");
                double dtstamp = child.decodeDouble("Time");
                long tstamp = ((long) dtstamp*1000) - 631195200000L;

                Date d = new Date(tstamp);
                SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy,HH:mm:ss");
                String s = f.format(d);
                System.out.println("Time: " + dtstamp + " " + tstamp + " " + s);

                content.moveTextPositionByAmount(0, -fheight);
                content.drawString(label);

                Record amp = child.decodeRecord("AmplifierState");
                double rsval = amp.decodeDouble("RsValue");

                content.moveTextPositionByAmount (100, 0);
                content.drawString(String.format("%2.3e", rsval));

                double val = amp.decodeDouble("RsFraction");
                content.moveTextPositionByAmount (50, 0);
                content.drawString(String.format("%2.3e", val));

                content.moveTextPositionByAmount (50, 0);
                content.drawString(String.format("%2.3e", (1/val)*rsval));

                val = amp.decodeDouble("CSlow");
                content.moveTextPositionByAmount (50, 0);
                content.drawString(String.format("%2.3e", val));

                content.moveTextPositionByAmount (-250, 0);

            }

            content.endText();

            content.close();
            doc.save("PDFWithText.pdf");
            doc.close();

        } catch (Exception e){
            System.out.println(e);
        }

    }

}
