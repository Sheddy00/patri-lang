package school.hei.patrimoine.modele.calculationMode;

import java.util.Collection;
import school.hei.patrimoine.modele.vente.ValeurMarche;

public class AddValeurOther implements AddValeurMarcheStrategy<ValeurMarche> {

  @Override
  public void ajouterValeur(Collection<ValeurMarche> valeursMarche, ValeurMarche valeurComptable) {
    valeursMarche.add(
        new ValeurMarche(
            valeurComptable.possession(),
            valeurComptable.t(),
            valeurComptable.possession().valeurComptable()));
  }
}
