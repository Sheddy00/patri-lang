package school.hei.patrimoine.modele.calculationMode;

import java.time.LocalDate;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.possession.Possession;

public class ValeurComptableStrategy implements ValeurCaseStrategy {

  @Override
  public Argent calculateValeurCase(Possession possession, LocalDate date) {
    return possession.valeurComptable();
  }
}
