package school.hei.patrimoine.patrilang.unit.visitors.possession;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.CompteContext;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.VenteContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.*;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Vente;
import school.hei.patrimoine.patrilang.antlr.PatriLangParser;
import school.hei.patrimoine.patrilang.modele.variable.VariableScope;
import school.hei.patrimoine.patrilang.utils.UnitTestVisitor;
import school.hei.patrimoine.patrilang.visitors.possession.CompteVisitor;
import school.hei.patrimoine.patrilang.visitors.possession.VenteVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

class CompteVisitorTest {
  private static final LocalDate AJD = LocalDate.of(2025, 6, 23);
  private static final Materiel ordinateur =
      new Materiel("ordinateur", AJD, AJD, ariary(1_000_000), 10.0);
  private static final Materiel tablette =
      new Materiel("tablette", AJD, AJD, ariary(2_000_000), 11.0);
  private final VariableVisitor variableVisitor = new VariableVisitor();

  CompteVisitor subject = new CompteVisitor(variableVisitor);

  VenteVisitor venteVisitor = new VenteVisitor(variableVisitor);

  UnitTestVisitor visitor =
      new UnitTestVisitor() {
        @Override
        public Compte visitCompte(CompteContext ctx) {
          return subject.apply(ctx);
        }
      };

  UnitTestVisitor venteTestVisitor =
      new UnitTestVisitor() {
        @Override
        public Vente visitVente(VenteContext ctx) {
          return venteVisitor.apply(ctx);
        }
      };

  @BeforeEach
  void setUp() {
    variableVisitor.addToScope("ajd", DATE, AJD);
    variableVisitor.addToScope("ordinateur", MATERIEL, ordinateur);
    variableVisitor.addToScope("tablette", MATERIEL, tablette);
    subject = new CompteVisitor(variableVisitor);
  }

  @Test
  void parse_normal_compte() {
    var input = "monCompte, valant 300000Ar le 01 du 01-2025";

    var expected = new Compte("monCompte", LocalDate.of(2025, JANUARY, 1), ariary(300_000));

    Compte actual = visitor.visit(input, PatriLangParser::compte);

    assertEquals(expected.nom(), actual.nom());
    assertEquals(expected.getDateOuverture(), actual.getDateOuverture());
    assertEquals(expected.valeurComptable(), actual.valeurComptable());
  }

  @Test
  void should_handle_compte_creation_and_sale_in_same_scope() {
    var inputCreation = "monCompte, valant 300000Ar le 01 du 01-2025";
    var inputVente =
        "* `vente` Dates:ajd, vendre Matériel:ordinateur pour 150000Ar vers Trésoreries:monCompte";

    Compte expected = visitor.visit(inputCreation, PatriLangParser::compte);
    Vente actual = venteTestVisitor.visit(inputVente, PatriLangParser::vente);

    assertEquals(expected.nom(), actual.getCompteBeneficiaire().nom());
    assertEquals(expected.valeurComptable(), actual.getCompteBeneficiaire().valeurComptable());
    assertEquals(expected.getDateOuverture(), actual.getCompteBeneficiaire().getDateOuverture());
    assertEquals(expected, actual.getCompteBeneficiaire());
  }

  @Test
  void should_share_compte_between_different_scopes() {
    var parentScope = new VariableScope();
    parentScope.add("ajd", DATE, AJD);
    parentScope.add("ordinateur", MATERIEL, ordinateur);
    parentScope.add("tablette", MATERIEL, tablette);

    VariableVisitor creationCompteVisitor = new VariableVisitor(Optional.of(parentScope));
    CompteVisitor compteVisitor = new CompteVisitor(creationCompteVisitor);
    VariableVisitor venteVisitor = new VariableVisitor(Optional.of(parentScope));
    VenteVisitor venteVisitorImpl = new VenteVisitor(venteVisitor);

    var inputCreation = "monCompte, valant 300000Ar le 01 du 01-2025";

    var inputVente =
        "* `vente` Dates:ajd, vendre Matériel:tablette pour 150000Ar vers Trésoreries:monCompte";

    Compte expected =
        new UnitTestVisitor() {
          @Override
          public Compte visitCompte(CompteContext ctx) {
            return compteVisitor.apply(ctx);
          }
        }.visit(inputCreation, PatriLangParser::compte);

    Vente actual =
        new UnitTestVisitor() {
          @Override
          public Vente visitVente(VenteContext ctx) {
            return venteVisitorImpl.apply(ctx);
          }
        }.visit(inputVente, PatriLangParser::vente);

    assertAll(
        () -> assertEquals(expected.nom(), actual.getCompteBeneficiaire().nom()),
        () ->
            assertEquals(
                expected.valeurComptable(), actual.getCompteBeneficiaire().valeurComptable()),
        () ->
            assertEquals(
                expected.getDateOuverture(), actual.getCompteBeneficiaire().getDateOuverture()),
        () -> assertEquals(expected, actual.getCompteBeneficiaire()));
  }
}
