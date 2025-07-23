package school.hei.patrimoine.modele.vente;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.Devise;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.Materiel;

public class VenteTest {
//  @Test
//  void
//      valeur_marche_doit_valider_les_parametres() { // NullPointerException if possession or valeur
//                                                    // or parameter is null
//    // IllegalArgumentException if value of them are not as exected
//    var possession =
//        new Materiel(
//            "Bâtiment", LocalDate.now(), LocalDate.now(), new Argent(200_000, Devise.EUR), 0.0);
//    assertThrows(
//        NullPointerException.class,
//        () -> new ValeurMarche(possession, LocalDate.now(), new Argent(100, Devise.EUR)));
//    assertThrows(
//        NullPointerException.class, () -> new ValeurMarche(possession, LocalDate.now(), null));
//  }

  @Test
  void valeur_marche_doit_stocker_correctement_les_valeurs() {
    var date = LocalDate.of(2025, 1, 1);
    var argent = new Argent(300_000, Devise.EUR);
    var vm = new ValeurMarche(null, date, argent);

    assertEquals(date, vm.t());
    assertEquals(argent, vm.valeur());
  }

  @Test
  void vente_doit_marquer_possession_comme_vendue() {
    var prixValeur = new Argent(20_000, Devise.EUR);
    var materiel = new Materiel("Voiture", LocalDate.now(), LocalDate.now(), prixValeur, 0.0);
    var compte = new Compte("Compte courant", LocalDate.now(), new Argent(0, Devise.EUR));

    materiel.vendre(LocalDate.now(), new Argent(25_000, Devise.EUR), compte);

    assertEquals(LocalDate.now(), materiel.getDateVente().get());
    assertEquals(new Argent(25_000, Devise.EUR), materiel.getPrixVente().get());
  }

  @Test
  void valeur_marche_historique_doit_etre_conservee() {
    var materiel =
        new Materiel(
            "Bâtiment", LocalDate.now(), LocalDate.now(), new Argent(200_000, Devise.EUR), 0.0);

    var date1 = LocalDate.of(2025, 1, 1);
    var date2 = LocalDate.of(2025, 6, 1);
    var possession = new Materiel("Bâtiment", date1, date2, new Argent(200_000, Devise.EUR), 0.0);

    materiel.ajouterValeurMarche(
        new ValeurMarche(possession, date1, new Argent(250_000, Devise.EUR)));
    materiel.ajouterValeurMarche(
        new ValeurMarche(possession, date2, new Argent(300_000, Devise.EUR)));

    assertEquals(new Argent(250_000, Devise.EUR), materiel.getValeurMarche(date1));
    assertEquals(new Argent(300_000, Devise.EUR), materiel.getValeurMarche(date2));
  }

  @Test
  void vendre_possession_deja_vendue_doit_echouer() { //
    var materiel =
        new Materiel(
            "Voiture", LocalDate.now(), LocalDate.now(), new Argent(20_000, Devise.EUR), 0.0);
    var compte = new Compte("Compte courant", LocalDate.now(), new Argent(0, Devise.EUR));

    materiel.vendre(LocalDate.now(), new Argent(25_000, Devise.EUR), compte);

    assertThrows(
        IllegalStateException.class,
        () -> materiel.vendre(LocalDate.now(), new Argent(30_000, Devise.EUR), compte));
  }

//  @Test
//  void vendre_possession_sans_compte_beneficiaire_doit_echouer() {
//    var materiel =
//        new Materiel(
//            "Voiture", LocalDate.now(), LocalDate.now(), new Argent(20_000, Devise.EUR), 0.0);
//
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> materiel.vendre(LocalDate.now(), new Argent(25_000, Devise.EUR), null));
//  }

  @Test
  void vendre_possession_sans_date_vente_doit_echouer() { // NullPointerException for test
    var materiel =
        new Materiel(
            "Voiture", LocalDate.now(), LocalDate.now(), new Argent(20_000, Devise.EUR), 0.0);
    var compte = new Compte("Compte courant", LocalDate.now(), new Argent(0, Devise.EUR));

    assertThrows(
        NullPointerException.class,
        () -> materiel.vendre(null, new Argent(25_000, Devise.EUR), compte));
  }

//  @Test
//  void vendre_possession_sans_prix_vente_doit_echouer() {
//    var materiel =
//        new Materiel(
//            "Voiture", LocalDate.now(), LocalDate.now(), new Argent(20_000, Devise.EUR), 0.0);
//    var compte = new Compte("Compte courant", LocalDate.now(), new Argent(0, Devise.EUR));
//
//    assertThrows(
//        IllegalArgumentException.class, () -> materiel.vendre(LocalDate.now(), null, compte));
//  }
}
