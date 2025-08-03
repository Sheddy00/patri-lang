package school.hei.patrimoine.patrilang.unit.visitors.possession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.PossedeMaterielContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.DATE;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.patrilang.antlr.PatriLangParser;
import school.hei.patrimoine.patrilang.modele.variable.Variable;
import school.hei.patrimoine.patrilang.modele.variable.VariableType;
import school.hei.patrimoine.patrilang.utils.UnitTestVisitor;
import school.hei.patrimoine.patrilang.visitors.possession.MaterielVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

class MaterielVisitorTest {
  private static LocalDate AJD = LocalDate.of(2025, 6, 23);
  private static VariableVisitor variableVisitor = new VariableVisitor();
  private static UnitTestVisitor visitor;

  MaterielVisitor subject = new MaterielVisitor(variableVisitor);

  @BeforeEach
  void resetScope() {
    variableVisitor = new VariableVisitor();
    variableVisitor.addToScope("ajd", DATE, AJD);
    subject = new MaterielVisitor(variableVisitor);
    visitor =
        new UnitTestVisitor() {
          @Override
          public Materiel visitPossedeMateriel(PossedeMaterielContext ctx) {
            return subject.apply(ctx);
          }
        };
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
  void should_handle_materiel_scope() {
    var input =
        """
    * `possèdeMateriel`, Dates:ajd posséder ordinateur valant 200000Ar, s'appréciant annuellement de 1%
""";
    Materiel actual = visitor.visit(input, PatriLangParser::possedeMateriel);

    Variable variable = variableVisitor.getVariableScope().get("ordinateur", VariableType.MATERIEL);
    Materiel fromScope = (Materiel) variable.value();
    assertEquals(actual.nom(), fromScope.nom());
    assertEquals("ordinateur", fromScope.nom());
  }
}
