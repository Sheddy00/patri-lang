package school.hei.patrimoine.modele.calculationMode;

import java.time.LocalDate;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.possession.Possession;

public interface ValeurMarcheCase {
  Argent calculateValeurCase(Possession possession, LocalDate date);
}
