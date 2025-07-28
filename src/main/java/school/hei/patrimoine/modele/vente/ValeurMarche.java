package school.hei.patrimoine.modele.vente;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.possession.Possession;

public record ValeurMarche(Possession possession, LocalDate t, Argent valeur)
    implements Serializable {
  public ValeurMarche(Possession possession, LocalDate t, Argent valeur) {
    this.possession = possession;
    this.t = t;
    this.valeur = valeur;
    this.possession.ajouterValeurMarche(this);
  }

  // stackoverflow equals and hashCode as infinite loop for possession if not exclude possession
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ValeurMarche that)) return false;
    return Objects.equals(t, that.t) && Objects.equals(valeur, that.valeur);
  }

  @Override
  public int hashCode() {
    return Objects.hash(t, valeur);
  }
}
