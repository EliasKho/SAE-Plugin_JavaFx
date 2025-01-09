//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import projet.classes.Attribut;
//import projet.classes.Methode;
//
//import java.lang.reflect.Parameter;
//import java.lang.reflect.Type;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TestMethode {
//    Type typeRetour;
//    Parameter typeParam1, typeParam2;
//    @BeforeEach
//    public void setUp() {
//        Class<?> c = Attribut.class;
//        typeRetour = int.class;
//        typeParam1 = c.getConstructors()[0].getParameters()[0];
//        typeParam2 = c.getConstructors()[0].getParameters()[2];
//    }
//
//    @Test
//    public void TestMethode0Param(){
//        Methode m = new Methode("nom", typeRetour, null, 1);
//        assertEquals("+nom():int", m.getUMLString());
//    }
//
//    @Test
//    public void TestMethode1Param(){
//        Methode m = new Methode("nom", typeRetour, List.of(typeParam1), 1);
//        assertEquals("+nom(String):int", m.getUMLString());
//    }
//
//    @Test
//    public void TestMethode2Param(){
//        Methode m = new Methode("nom", typeRetour, List.of(typeParam1, typeParam2), 1);
//        assertEquals("+nom(String,int):int", m.getUMLString());
//    }
//
//    @Test
//    public void TestConstructeur0Param(){
//        Methode m = new Methode("nom", null, 1);
//        assertEquals("+nom()", m.getUMLString());
//    }
//
//    @Test
//    public void TestConstructeur1Param(){
//        Methode m = new Methode("nom", List.of(typeParam1), 1);
//        assertEquals("+nom(String)", m.getUMLString());
//    }
//
//    @Test
//    public void TestConstructeur2Param(){
//        Methode m = new Methode("nom", List.of(typeParam1, typeParam2), 1);
//        assertEquals("+nom(String,int)", m.getUMLString());
//    }
//}
