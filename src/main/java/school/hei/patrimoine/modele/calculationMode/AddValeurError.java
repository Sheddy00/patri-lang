package school.hei.patrimoine.modele.calculationMode;

import java.util.Collection;
import school.hei.patrimoine.modele.vente.ValeurMarche;

public class AddValeurError implements AddValeurMarcheStrategy<ValeurMarche> {

  @Override
  public void ajouterValeur(Collection<ValeurMarche> ValeurMarche, ValeurMarche elementToAdd) {
    throw new IllegalArgumentException(
        "Seules les IMMOBILISATIONS et ENTREPRISEs peuvent avoir une valeur de marché");
  }
}
