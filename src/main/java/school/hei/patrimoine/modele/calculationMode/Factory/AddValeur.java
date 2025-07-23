package school.hei.patrimoine.modele.calculationMode.Factory;

import school.hei.patrimoine.modele.calculationMode.AddValeurError;
import school.hei.patrimoine.modele.calculationMode.AddValeurMarcheDefaultStrategy;
import school.hei.patrimoine.modele.calculationMode.AddValeurMarcheStrategy;
import school.hei.patrimoine.modele.possession.TypeAgregat;

public class AddValeur {
  public static AddValeurMarcheStrategy addValeurMarche(TypeAgregat typeAgregat) {
    return switch (typeAgregat) {
      case ENTREPRISE, IMMOBILISATION -> new AddValeurMarcheDefaultStrategy();
      default -> new AddValeurError();
    };
  }
}
