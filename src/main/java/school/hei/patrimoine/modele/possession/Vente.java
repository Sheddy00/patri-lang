package school.hei.patrimoine.modele.possession;

import static school.hei.patrimoine.modele.possession.TypeAgregat.FLUX;

import java.time.LocalDate;
import school.hei.patrimoine.modele.Argent;

public final class Vente extends Possession {
    private final LocalDate tVente;
    private final Possession possession;
    private final Compte compteBeneficiaire;
    private final Argent prixVente;

    public Vente(
            String nom,
            LocalDate t,
            Argent valeurComptable,
            LocalDate tVente,
            Possession possession,
            Argent prixVente,
            Compte compteBeneficiaire) {
        super(nom, t, valeurComptable);
        this.tVente = tVente;
        this.possession = possession;
        possession.vendre(tVente, prixVente, compteBeneficiaire);
        this.compteBeneficiaire = compteBeneficiaire;
        this.prixVente = prixVente;
    }

    private Vente(
            String nom,
            LocalDate t,
            Argent valeurComptable,
            LocalDate tVente,
            Possession possession,
            Argent prixVente) {
        super(nom, t, valeurComptable);
        this.tVente = tVente;
        this.possession = possession;
        this.prixVente = prixVente;
        this.compteBeneficiaire = null;
    }

    @Override
    public Vente projectionFuture(LocalDate tFutur) {
        if (tFutur.isBefore(tVente)) {
            return new Vente(
                    nom,
                    tFutur,
                    valeurComptable,
                    tVente,
                    possession.projectionFuture(tFutur),
                    prixVente
            );
        }
        return null;
    }

    @Override
    public TypeAgregat typeAgregat() {
        return FLUX;
    }
}
