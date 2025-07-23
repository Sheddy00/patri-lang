package school.hei.patrimoine.modele.calculationMode;

import java.util.Collection;

public interface AddValeurMarcheStrategy<T> {
  void ajouterValeur(Collection<T> collection, T elementToAdd);
}
