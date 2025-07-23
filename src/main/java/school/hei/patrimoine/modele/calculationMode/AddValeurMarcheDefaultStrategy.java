package school.hei.patrimoine.modele.calculationMode;

import java.util.Collection;
import school.hei.patrimoine.modele.vente.ValeurMarche;

public class AddValeurMarcheDefaultStrategy implements AddValeurMarcheStrategy<ValeurMarche> {
  @Override
  public void ajouterValeur(Collection<ValeurMarche> valeursMarche, ValeurMarche valeurMarche) {
    valeursMarche.add(valeurMarche);
  }
}
