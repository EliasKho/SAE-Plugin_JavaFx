package projet.controleur;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import net.sourceforge.plantuml.GeneratedImage;
import projet.Modele;
import projet.classes.VueClasse;

import net.sourceforge.plantuml.SourceFileReader;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControlerImage {
    private Modele modele;

    public ControlerImage(Modele modele) {
        this.modele = modele;
    }

    public void captureImage() {
        WritableImage image = new WritableImage((int)modele.getScene().getWidth()*75/100, (int) modele.getScene().getHeight());
        // Capturer le contenu du nœud dans l'image
        modele.getVueClasse().snapshot(new SnapshotParameters(), image);

        // Sauvegarder l'image dans un fichier
        File file = new File("capture.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
//            System.out.println("Image enregistrée : " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        }
    }

    public void captureImageUML(){
        try {
            // Chemin vers votre fichier PlantUML
            File source = new File("Diag.puml");

            // Création du lecteur de fichier source
            SourceFileReader reader = new SourceFileReader(source);

            List<GeneratedImage> list = reader.getGeneratedImages();
//            System.out.println(list);

            if (!list.isEmpty()) {
                File png = list.get(0).getPngFile();
//                System.out.println("Image générée : " + png.getAbsolutePath());
            } else {
                System.out.println("Aucune image n'a été générée.");
            }
//            System.out.println("Image enregistrée : " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
