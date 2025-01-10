package projet.controleur;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import net.sourceforge.plantuml.GeneratedImage;
import projet.Modele;


import net.sourceforge.plantuml.SourceFileReader;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ControlerImage {
    private Modele modele;

    public ControlerImage(Modele modele) {
        this.modele = modele;
    }

    /**
     * Capture l'image de la vue de classe
     */
    public void captureImage() {
        // Capture de l'image
        WritableImage image = new WritableImage((int)modele.getScene().getWidth()*75/100, (int) modele.getScene().getHeight()-(int)modele.getScene().getWindow().getHeight()+(int) modele.getScene().getHeight());
        modele.getVueClasse().snapshot(new SnapshotParameters(), image);

        File file = new File("capture.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        }
    }

    // Capture l'image de la vue UML
    public void captureImageUML(){
        try {
            // on lit le fichier Diag.puml
            File source = new File("Diag.puml");

            SourceFileReader reader = new SourceFileReader(source);

            List<GeneratedImage> list = reader.getGeneratedImages();

            if (!list.isEmpty()) {
                list.getFirst().getPngFile();
            } else {
                System.out.println("Aucune image n'a été générée.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
