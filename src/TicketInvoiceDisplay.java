import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.IOException;

public class TicketInvoiceDisplay {
    public Stage stage;
    @FXML
    TilePane invoicePane;
    @FXML
    TilePane ticketPane;
    @FXML
    public void initialize() throws IOException {
        File invoiceFile = new File("invoice.pdf");
        PDDocument document = PDDocument.load(invoiceFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for(int i = 0; i < document.getNumberOfPages(); i++) {
            Image invoice1 = renderPDFPageToImage(pdfRenderer, i);
            ImageView invoice = new ImageView(invoice1);
            invoice.setFitWidth(485);
            invoice.setPreserveRatio(true);
            invoicePane.getChildren().add(invoice);
        }


        File ticketFile = new File("ticket.pdf");
        PDDocument ticketDocument = PDDocument.load(ticketFile);
        PDFRenderer pdfRenderer1 = new PDFRenderer(ticketDocument);
        for(int i = 0; i < ticketDocument.getNumberOfPages(); i++) {
            Image ticket1 = renderPDFPageToImage(pdfRenderer1, i);
            ImageView ticket = new ImageView(ticket1);
            ticket.setFitWidth(485);
            ticket.setPreserveRatio(true);
            ticketPane.getChildren().add(ticket);
        }

    }

    private Image renderPDFPageToImage(PDFRenderer renderer, int pageIndex) throws IOException {
        java.awt.image.BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, 300);
        javafx.scene.image.Image javafxImage = convertToFXImage(bufferedImage);
        return javafxImage;
    }

    private javafx.scene.image.Image convertToFXImage(java.awt.image.BufferedImage bufferedImage) {
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(
                bufferedImage.getWidth(), bufferedImage.getHeight());
        javafx.scene.image.PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return writableImage;
    }
}
