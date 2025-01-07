package projet.controleur;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import projet.classes.VueClasse;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ControlerImage {
    public static void captureImage(Scene scene, VueClasse scrollpane) {
        WritableImage image = new WritableImage((int)scene.getWidth()*75/100, (int) scene.getHeight());

        // Capturer le contenu du nœud dans l'image
        scrollpane.snapshot(new SnapshotParameters(), image);

        // Sauvegarder l'image dans un fichier
        File file = new File("snapshot.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Image enregistrée : " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        }
    }
}
