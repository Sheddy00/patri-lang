package school.hei.patrimoine.patrilang.visitors.possession;

import static school.hei.patrimoine.patrilang.antlr.PatriLangParser.CompteContext;
import static school.hei.patrimoine.patrilang.modele.variable.VariableType.TRESORERIES;
import static school.hei.patrimoine.patrilang.visitors.BaseVisitor.visitText;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.patrilang.visitors.SimpleVisitor;
import school.hei.patrimoine.patrilang.visitors.variable.VariableVisitor;

@RequiredArgsConstructor
public class CompteVisitor implements SimpleVisitor<CompteContext, Compte> {
  private final VariableVisitor variableVisitor;

  @Override
  public Compte apply(CompteContext ctx) {
    String nom = visitText(ctx.nom);
    Argent valeurComptable = this.variableVisitor.asArgent(ctx.valeurComptable);
    LocalDate t = this.variableVisitor.asDate(ctx.dateValue);

    Compte compte = new Compte(nom, t, valeurComptable);
    variableVisitor.addToScope(nom, TRESORERIES, compte);
    variableVisitor
        .getVariableScope()
        .parentScope()
        .ifPresent(parent -> parent.add(nom, TRESORERIES, compte));
    return compte;
  }
}
