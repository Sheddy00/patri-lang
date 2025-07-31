package school.hei.patrimoine.modele.calculationMode;

import java.util.Collection;
import school.hei.patrimoine.modele.vente.ValeurMarche;

public class AddValeurOther implements AddValeurMarcheStrategy<ValeurMarche> {

  @Override
  public void ajouterValeur(Collection<ValeurMarche> valeursMarche, ValeurMarche valeurComptable) {
    throw new UnsupportedOperationException(
        "Seules les IMMOBILISATIONs et ENTREPRISEs peuvent avoir une valeur de marché");
  }
}
