package com.mehdi.pharmacie;

import java.util.Comparator;

public class Pharmacie{

    private String nom;
    private String zone;
    private String adresse;
    private String telephone;
    private double distance;
    private String etat;


    private String coordonnee;

    public Pharmacie(String nom,String zone, String adresse, String telephone, String coordonnee,String etat) {
        this.nom = nom;
        this.zone=zone;
        this.adresse = adresse;
        this.telephone = telephone;
        this.coordonnee = coordonnee;
        this.etat=etat;

    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public static Comparator<Pharmacie> ComparatorDistance = new Comparator<Pharmacie>() {

        @Override
        public int compare(Pharmacie o1, Pharmacie o2) {
            return (int) (o1.getDistance() - o2.getDistance());
        }


    };

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }



    public String getNom() {
        return nom;
    }

    public String getZone() {
        return zone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCoordonnee() {
        return coordonnee;
    }

    @Override
    public String toString() {
        return "Pharmacie{" +
                "nom='" + nom + '\'' +
                ", zone='" + zone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", distance=" + distance +
                ", etat='" + etat + '\'' +
                ", coordonnee='" + coordonnee + '\'' +
                '}';
    }
}
