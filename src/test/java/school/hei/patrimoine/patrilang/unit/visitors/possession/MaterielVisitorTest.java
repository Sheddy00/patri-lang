package school.hei.patrimoine.patrilang.unit.visitors.possession;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.PossedeMaterielContext;
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
import school.hei.patrimoine.patrilang.visitors.possession.MaterielVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

class MaterielVisitorTest {
  private static final LocalDate AJD = LocalDate.of(2025, 6, 23);
  private final VariableVisitor variableVisitor = new VariableVisitor();

  MaterielVisitor subject = new MaterielVisitor(variableVisitor);

  UnitTestVisitor visitor =
      new UnitTestVisitor() {
        @Override
        public Materiel visitPossedeMateriel(PossedeMaterielContext ctx) {
          return subject.apply(ctx);
        }
      };

  @BeforeEach
  void setUp() {
    variableVisitor.addToScope("ajd", DATE, AJD);
  }

  @Test
  void parser_materiel_who_has_appreciation_positive() {
    var au1Janvier2026 = LocalDate.of(2026, 1, 1);
    var input =
        """
    * `possèdeMateriel`, Dates:ajd posséder ordinateur valant 200000Ar, s'appréciant annuellement de 1%
""";

    var expected = new Materiel("ordinateur", AJD, AJD, ariary(200_000), 0.01);

    Materiel actual = visitor.visit(input, PatriLangParser::possedeMateriel);

    assertEquals(expected.nom(), actual.nom());
    assertEquals(expected.valeurComptable(), actual.valeurComptable());
    assertEquals(
        expected.projectionFuture(au1Janvier2026).valeurComptable(),
        actual.projectionFuture(au1Janvier2026).valeurComptable());
  }

  @Test
  void parser_materiel_who_has_appreciation_negative() {
    var au1Janvier2026 = LocalDate.of(2026, 1, 1);
    var input =
        """
    * `possèdeMateriel`, Dates:ajd posséder ordinateur valant 200000Ar, se dépréciant annuellement de 20%
""";

    var expected = new Materiel("ordinateur", AJD, AJD, ariary(200_000), -0.2);

    Materiel actual = visitor.visit(input, PatriLangParser::possedeMateriel);

    assertEquals(expected.nom(), actual.nom());
    assertEquals(expected.valeurComptable(), actual.valeurComptable());
    assertEquals(
        expected.projectionFuture(au1Janvier2026).valeurComptable(),
        actual.projectionFuture(au1Janvier2026).valeurComptable());
  }

  @Test
  void access_materiel_in_same_section() {
    var inputCreation =
        """
* `possèdeMateriel`, Dates:ajd posséder ordinateur valant 200000Ar, s'appréciant annuellement de 1%
""";

    Materiel expected = visitor.visit(inputCreation, PatriLangParser::possedeMateriel);

    var actual = variableVisitor.getVariableScope().root().get("ordinateur", MATERIEL);
    assertNotNull(actual);
    assertEquals(expected.nom(), actual.name());
    assertEquals(MATERIEL, actual.type());
  }

  @Test
  void access_materiel_in_different_section() {
    var compteBeneficiaire = new Compte("compteBeneficiaire", AJD, ariary(0));
    var parentScope = new VariableScope();
    parentScope.add("ajd", DATE, AJD);
    parentScope.add("compteBeneficiaire", TRESORERIES, compteBeneficiaire);

    var declarationVisitor = new VariableVisitor(Optional.of(parentScope));
    var materielVisitor = new MaterielVisitor(declarationVisitor);
    var inputDeclare =
        """
* `possèdeMateriel`, Dates:ajd posséder télévision valant 300000Ar, s'appréciant annuellement de 5%
""";

    Materiel expected =
        new UnitTestVisitor() {
          @Override
          public Materiel visitPossedeMateriel(PossedeMaterielContext ctx) {
            return materielVisitor.apply(ctx);
          }
        }.visit(inputDeclare, PatriLangParser::possedeMateriel);

    VariableVisitor usageVisitor =
        new VariableVisitor(Optional.of(new VariableScope(Optional.of(parentScope))));
    var actual = usageVisitor.getVariableScope().root().get("télévision", MATERIEL);

    assertNotNull(actual);
    assertEquals(expected.nom(), actual.name());
    assertEquals(MATERIEL, actual.type());
    assertEquals(expected, actual.value());
  }
}
