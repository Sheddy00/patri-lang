package school.hei.patrimoine.patrilang.unit.visitors.possession;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.CompteContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.*;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.patrilang.antlr.PatriLangParser;
import school.hei.patrimoine.patrilang.modele.variable.VariableScope;
import school.hei.patrimoine.patrilang.utils.UnitTestVisitor;
import school.hei.patrimoine.patrilang.visitors.possession.CompteVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

class CompteVisitorTest {
  private static final LocalDate AJD = LocalDate.of(2025, 6, 23);
  private static final Materiel ordinateur =
      new Materiel("ordinateur", AJD, AJD, ariary(1_000_000), 10.0);
  private static final Materiel tablette =
      new Materiel("tablette", AJD, AJD, ariary(2_000_000), 11.0);
  private final VariableVisitor variableVisitor = new VariableVisitor();

  CompteVisitor subject = new CompteVisitor(variableVisitor);

  UnitTestVisitor visitor =
      new UnitTestVisitor() {
        @Override
        public Compte visitCompte(CompteContext ctx) {
          return subject.apply(ctx);
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
  void access_compte_in_same_section() {
    var inputCreation = "monCompte, valant 300000Ar le 01 du 01-2025";

    Compte expected = visitor.visit(inputCreation, PatriLangParser::compte);

    var actual = variableVisitor.getVariableScope().root().get("monCompte", TRESORERIES);
    assertNotNull(actual);
    assertEquals(expected.nom(), actual.name());
    assertEquals(TRESORERIES, actual.type());
  }

  @Test
  void access_compte_in_different_section() {
    var parentScope = new VariableScope();
    parentScope.add("ajd", DATE, AJD);
    parentScope.add("ordinateur", MATERIEL, ordinateur);
    parentScope.add("tablette", MATERIEL, tablette);

    var creationCompteVisitor = new VariableVisitor(Optional.of(parentScope));
    var compteVisitor = new CompteVisitor(creationCompteVisitor);
    var inputCreation = "monCompte, valant 300000Ar le 01 du 01-2025";

    Compte expected =
        new UnitTestVisitor() {
          @Override
          public Compte visitCompte(CompteContext ctx) {
            return compteVisitor.apply(ctx);
          }
        }.visit(inputCreation, PatriLangParser::compte);

    VariableVisitor usageVisitor =
        new VariableVisitor(Optional.of(new VariableScope(Optional.of(parentScope))));
    var actual = usageVisitor.getVariableScope().root().get("monCompte", TRESORERIES);

    assertNotNull(actual);
    assertEquals(expected.nom(), actual.name());
    assertEquals(TRESORERIES, actual.type());
    assertEquals(expected, actual.value());
  }
}
