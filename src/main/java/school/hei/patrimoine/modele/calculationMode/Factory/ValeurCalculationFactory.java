package school.hei.patrimoine.modele.calculationMode.Factory;

import school.hei.patrimoine.modele.calculationMode.ValeurCaseStrategy;
import school.hei.patrimoine.modele.calculationMode.ValeurComptableStrategy;
import school.hei.patrimoine.modele.calculationMode.ValeurHistoriqueStrategy;
import school.hei.patrimoine.modele.possession.TypeAgregat;

public class ValeurCalculationFactory {
  // stop instanciation and calculation for each type of possession
  public static ValeurCaseStrategy getCalculation(TypeAgregat typeAgregat) {
    return switch (typeAgregat) {
      case IMMOBILISATION, ENTREPRISE -> new ValeurHistoriqueStrategy();
      default -> new ValeurComptableStrategy();
    };
  }
}
