//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import projet.classes.*;
//
//import java.lang.reflect.Type;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TestAttribut {
//    Type type;
//    @BeforeEach
//    public void setUp() {
//        type = int.class;
//    }
//
//    @Test
//    public void testPublic() {
//        Attribut a = new Attribut("nom", type, 1);
//        assertEquals("+nom:int", a.getUMLString());
//    }
//
//    @Test
//    public void testProtected() {
//        Attribut a = new Attribut("nom", type, 4);
//        assertEquals("#nom:int", a.getUMLString());
//    }
//
//    @Test
//    public void testPrivate() {
//        Attribut a = new Attribut("nom", type, 2);
//        assertEquals("-nom:int", a.getUMLString());
//    }
//
//    @Test
//    public void testPublicStatic() {
//        Attribut a = new Attribut("nom", type, 9);
//        assertEquals("+{static} nom:int", a.getUMLString());
//    }
//
//    @Test
//    public void testPublicAbstract() {
//        Attribut a = new Attribut("nom", type, 1025);
//        assertEquals("+{abstract} nom:int", a.getUMLString());
//    }
//}
