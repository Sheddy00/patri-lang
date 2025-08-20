package school.hei.patrimoine.patrilang.unit.visitors.possession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.modele.Argent.euro;
import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.ArgentContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.NOMBRE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.patrilang.antlr.PatriLangParser;
import school.hei.patrimoine.patrilang.utils.UnitTestVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableArgentVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableDateVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableExpressionVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

import java.time.LocalDate;

class VariableArgentVisitorTest {
  VariableVisitor variableVisitor = new VariableVisitor();
  VariableArgentVisitor subject =
      new VariableArgentVisitor(
          variableVisitor.getVariableScope(),
          new VariableExpressionVisitor(
              variableVisitor.getVariableScope(), variableVisitor::getVariableDateVisitor),
          new VariableDateVisitor(
              variableVisitor.getVariableScope(), variableVisitor::getVariableExpressionVisitor));

  UnitTestVisitor visitor =
      new UnitTestVisitor() {
        @Override
        public Object visitArgent(ArgentContext ctx) {
          return subject.apply(ctx);
        }
      };

  @BeforeEach
  void setUp() {
    variableVisitor = new VariableVisitor();
    subject =
        new VariableArgentVisitor(
            variableVisitor.getVariableScope(),
            new VariableExpressionVisitor(
                variableVisitor.getVariableScope(), variableVisitor::getVariableDateVisitor),
            new VariableDateVisitor(
                variableVisitor.getVariableScope(), variableVisitor::getVariableExpressionVisitor));
  }

  private Argent parse_visit_argent(String input) {
    return visitor.visit(input, PatriLangParser::argent);
  }

  @Test
  void parse_argent_without_expression() {
    var input = "300000Ar";
    var expected = ariary(300_000);

    var actual = parse_visit_argent(input);

    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_negation() {
    var input = "-300_000Ar";
    var expected = ariary(-300_000);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_expression() {
    var input = "(300 `/*comment1*/` +  450 `/*comment2*/`)Ar";
    var expected = ariary(750);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_int_argent_with_underscore() {
    var input = "300_000_000Ar";
    var expected = ariary(300_000_000);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_int_argent_with_variable() {
    var input = "(Nombres:valeurOrdinateur)Ar";
    var expected = ariary(300_000_000);

    variableVisitor.addToScope("valeurOrdinateur", NOMBRE, 300_000_000d);
    var actual = parse_visit_argent(input);

    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_all_specification() {
    var valeurOrdinateur = 300_000_000;
    var input = "(-Nombres:valeurOrdinateur * 2 / 4)Ar";
    var expected = ariary(-1 * valeurOrdinateur * 2 / 4);

    variableVisitor.addToScope("valeurOrdinateur", NOMBRE, (double) valeurOrdinateur);
    var actual = parse_visit_argent(input);

    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_addition() {
    var input = "(300000Ar + 450000Ar) évalué le 01 Juin 2025";
    var expected = ariary(300_000)
        .add(ariary(450_000), LocalDate.of(2025, 6, 1));

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_subtraction() {
    var input = "(500000Ar - 200000Ar) évalué le 01 Mai 2025";
    var expected = ariary(500_000)
        .minus(ariary(200_000), LocalDate.of(2025, 5, 1));

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_multiplication_by_number() {
    var input = "(300000Ar * 2)";
    var expected = ariary(300_000)
        .mult(2);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_division_by_number() {
    var input = "(600000Ar / 2)";
    var expected = ariary(600_000)
        .div(2);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_all_complexe_operation() {
    var input = "((((50000Ar - (-50000Ar)) / 2) * 5) * 5) évalué le 01 Juin 2025";

    var expected = ariary(50_000)
        .minus(ariary(-50_000), LocalDate.of(2025, 6, 1))
        .div(2)
        .mult(5)
        .mult(5);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_long_operations() {
    var input = "((500000Ar + 200000Ar + 700000Ar + (-100000Ar) + 500000Ar + 500000Ar + 500000Ar + (-500000Ar) + (-500000Ar)) / 4)  évalué le 01 Mai 2025";

    var expected = ariary(500_000)
        .add(ariary(200_000), LocalDate.of(2025, 5, 1))
        .add(ariary(700_000), LocalDate.of(2025, 5, 1))
        .minus(ariary(100_000), LocalDate.of(2025, 5, 1))
        .add(ariary(500_000), LocalDate.of(2025, 5, 1))
        .add(ariary(500_000), LocalDate.of(2025, 5, 1))
        .add(ariary(500_000), LocalDate.of(2025, 5, 1))
        .minus(ariary(500_000), LocalDate.of(2025, 5, 1))
        .minus(ariary(500_000), LocalDate.of(2025, 5, 1))
        .div(4);

    var actual = parse_visit_argent(input);
    assertEquals(expected, actual);
  }

  @Test
  void parse_argent_with_different_currency() {
    var input = "((20000Ar + (1000€ * 3)) * 2) évalué le 14 fevrier 2025";
    var actual = parse_visit_argent(input);

    var somme = ariary(20_000)
            .add(euro(1_000).mult(3), LocalDate.of(2025, 2, 14));
    var expected = somme.mult(2);

    assertEquals(expected, actual);
  }
}
