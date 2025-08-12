package school.hei.patrimoine.patrilang.visitors.possession;

import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.CompteContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.DETTE;
import static school.hei.patrimoine.patrilang.visitors.BaseVisitor.visitText;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.possession.Dette;
import school.hei.patrimoine.patrilang.visitors.SimpleVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

@RequiredArgsConstructor
public class DetteVisitor implements SimpleVisitor<CompteContext, Dette> {
  private final VariableVisitor variableVisitor;

  @Override
  public Dette apply(CompteContext ctx) {
    String nom = visitText(ctx.nom);
    Argent valeurComptable = this.variableVisitor.asArgent(ctx.valeurComptable);
    LocalDate t = this.variableVisitor.asDate(ctx.dateValue);

    Dette dette = new Dette(nom, t, valeurComptable.mult(-1));
    variableVisitor.addToScope(nom, DETTE, dette);
    variableVisitor
        .getVariableScope()
        .parentScope()
        .ifPresent(parent -> parent.add(nom, DETTE, dette));
    return dette;
  }
}
