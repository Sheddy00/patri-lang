package school.hei.patrimoine.modele.possession;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import school.hei.patrimoine.modele.Argent;
import school.hei.patrimoine.modele.Devise;
import school.hei.patrimoine.modele.calculationMode.Factory.AddValeur;
import school.hei.patrimoine.modele.calculationMode.AddValeurMarcheStrategy;
import school.hei.patrimoine.modele.calculationMode.Factory.ValeurCalculationFactory;
import school.hei.patrimoine.modele.calculationMode.ValeurCaseStrategy;
import school.hei.patrimoine.modele.objectif.Objectivable;
import school.hei.patrimoine.modele.vente.ValeurMarche;
import school.hei.patrimoine.modele.vente.Vendable;

@ToString
@EqualsAndHashCode(callSuper = false)
public abstract sealed class Possession extends Objectivable
    implements Vendable, Serializable /*note(no-serializable)*/
    permits AchatMaterielAuComptant,
        Compte,
        CompteCorrection,
        Correction,
        FluxArgent,
        GroupePossession,
        Materiel,
        PatrimoinePersonnel,
        PersonneMorale,
        RemboursementDette,
        TransfertArgent {

  protected final String nom;
  protected final LocalDate t;
  protected final Argent valeurComptable;
  protected final Set<ValeurMarche> valeursMarche;

  @EqualsAndHashCode.Exclude @ToString.Exclude private CompteCorrection compteCorrection;
  @EqualsAndHashCode.Exclude @ToString.Exclude private boolean estVendu = false;
  @EqualsAndHashCode.Exclude @ToString.Exclude private LocalDate dateVente;
  @EqualsAndHashCode.Exclude @ToString.Exclude private Argent prixVente;

  protected Possession(String nom, LocalDate t, Argent valeurComptable) {
    this.nom = nom;
    this.t = t;
    this.valeurComptable = valeurComptable;
    this.valeursMarche = new HashSet<>(Set.of(new ValeurMarche(t, valeurComptable)));
  }

  public CompteCorrection getCompteCorrection() {
    if (compteCorrection == null) {
      compteCorrection = new CompteCorrection(nom, valeurComptable.devise());
    }
    return compteCorrection;
  }

  public Argent valeurComptable() {
    return estVendu ? new Argent(0, devise()) : valeurComptable;
  }

  public final Devise devise() {
    return valeurComptable.devise();
  }

  public final Argent valeurComptableFuture(LocalDate tFutur) {
    return projectionFuture(tFutur).valeurComptable();
  }

  public abstract Possession projectionFuture(LocalDate tFutur);

  public abstract TypeAgregat typeAgregat();

  @Override
  public String nom() {
    return nom;
  }

  @Override
  public Argent valeurAObjectifT(LocalDate t) {
    return projectionFuture(t).valeurComptable;
  }

  @Override
  public void vendre(LocalDate dateVente, Argent prixVente, Compte compteBeneficiaire) {
    if (estVendu) {
      throw new IllegalStateException("Possession déjà vendue");
    }
    this.estVendu = true;
    this.dateVente = dateVente;
    this.prixVente = prixVente;

    new FluxArgent("Vente de " + nom, compteBeneficiaire, dateVente, prixVente);
  }

  @Override
  public boolean estVendu(LocalDate t) {
    return dateVente != null && !dateVente.isAfter(t);
  }

  @Override
  public Optional<LocalDate> getDateVente() {
    return Optional.ofNullable(dateVente);
  }

  @Override
  public Optional<Argent> getPrixVente() {
    return Optional.ofNullable(prixVente);
  }

  public void ajouterValeurMarche(ValeurMarche valeurMarche) {
    AddValeurMarcheStrategy addValeurMarcheStrategy = AddValeur.addValeurMarche(this.typeAgregat());
    addValeurMarcheStrategy.ajouterValeur(this.valeursMarche, valeurMarche);
  }

  public Argent getValeurMarche(LocalDate t) {
    ValeurCaseStrategy calculator = ValeurCalculationFactory.getCalculation(this.typeAgregat());
    return calculator.calculateValeurCase(this, t);
  }

  public Set<ValeurMarche> historiqueValeurMarche() {
    return new HashSet<>(valeursMarche);
  }
}
