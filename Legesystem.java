import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



class Legesystem{
    //Liste som holder på ulike objekter
    protected Liste<Pasient> pasienter = new Lenkeliste<Pasient>();
    protected Liste<Legemiddel> legemidler = new Lenkeliste<Legemiddel>();
    protected Liste<Lege> leger = new SortertLenkeliste<Lege>();
    protected Liste<Resept> resepter = new Lenkeliste<Resept>();

    Scanner scan = new Scanner(System.in);

    //Metode som leser fra fil og setter objekter inn i listene
    public void lesFil(String filnavn) throws FileNotFoundException{
        Scanner scanner = null;
        try{
            File fil = new File(filnavn);
            scanner =  new Scanner(fil);
        } catch(FileNotFoundException e){
            System.out.println("Fant ikke fil");
            return;
        }

        int objekttype = 0; //Sier hvilke type objekt som leses


        while(scanner.hasNextLine()){
            String linje = scanner.nextLine();
            if(linje.startsWith("#")){ //Ny type objekt
                objekttype++;

            } else {
                String[] data = linje.trim().split("\\s*,\\s*"); //Må fjerne tomme tegn

                if(objekttype == 1){ //Pasient
                    Pasient pasient = new Pasient(data[0], data[1]);
                    pasienter.leggTil(pasient);

                } else if(objekttype == 2){ //Legemidler
                    String navn = data[0];
                    String type = data[1];
                    float pris = Float.parseFloat(data[2]);
                    float virkestoff = Float.parseFloat(data[3]);
                    Legemiddel legemiddel = null;

                    if(type.equals("a") || type.equals("narkotisk")){ //Narkotisk
                        int styrke = Integer.parseInt(data[4]);
                        legemiddel = new Narkotisk(navn, pris, virkestoff, styrke);

                    } else if(type.equals("b") || type.equals("vanedannende")) { //Vanedannende
                        int styrke = Integer.parseInt(data[4]);
                        legemiddel= new Vanedannende(navn, pris, virkestoff, styrke);

                    } else if(type.equals("c") || type.equals("vanlig")){ //Vanlig
                        legemiddel = new VanligLegemiddel(navn, pris, virkestoff);
                    }
                legemidler.leggTil(legemiddel);

                } else if(objekttype == 3){ //Leger
                    Lege lege = new Lege(data[0], Integer.parseInt(data[1])); // denne funker ikke når data[1] = 0, ganske rart..
                    leger.leggTil(lege);

                } else if(objekttype == 4){ //Resepter
                    Legemiddel legemiddel = legemidler.hent(Integer.parseInt(data[0]));
                    String legeNavn = data[1];
                    Lege ritkigLege = null;
                    for(Lege enLege : leger){
                        if(legeNavn.equals(enLege.hentNavn())){
                            ritkigLege = enLege;
                        }
                    }
                    Pasient pasient = pasienter.hent(Integer.parseInt(data[2]));
                    int reit = 0;
                    Resept resepten = null;
                    if(data.length == 4){
                        reit = Integer.parseInt(data[3]);
                    }
                    if (data[3].equals("hvit")){
                        try {
                            resepten = ritkigLege.skrivHvitResept(legemiddel, pasient, reit);
                        } catch (UlovligUtskrift u) {
                            System.out.println(u.getMessage());
                        }
                    } else if (data[3].equals("militaer")) {
                        try {
                            resepten = ritkigLege.skrivMilitaerResept(legemiddel, pasient, reit);
                        } catch (UlovligUtskrift u) {
                            System.out.println(u.getMessage());
                        }
                    } else if (data[3].equals("p")) {
                        try {
                            resepten = ritkigLege.skrivPResept(legemiddel, pasient);
                        } catch (UlovligUtskrift u) {
                            System.out.println(u.getMessage());
                        }
                    } else if (data[3].equals("blaa")) {
                        try {
                            resepten = ritkigLege.skrivBlaaResept(legemiddel, pasient, reit);
                        } catch (UlovligUtskrift u) {
                            System.out.println(u.getMessage());
                        }
                    }
                    resepter.leggTil(resepten);
                    pasient.leggTilResept(resepten);
                }
            }
        }
    }                       // ferdig lesFil()





    public void ordrelokke(){
        int inputFraBruker = -1;

        while(inputFraBruker != 0){
            if(inputFraBruker == 1){
                seFullstendigListe();
            } else if(inputFraBruker == 2){
                leggTilElement();
            } else if(inputFraBruker == 3){
                brukEnResept();
            } else if(inputFraBruker == 4){
                //skrivUtStatestikk();
            } else if(inputFraBruker == 5){
                //skrivDataTilFil();
            } else if (6 < inputFraBruker || inputFraBruker < -1) {
                System.out.println("Velg en av de fem alternativene");
            }
            System.out.println();
            System.out.println("Hovedmeny:");
            System.out.println("1: Skriv ut fullstendig liste over pasienter, lege, legemidler, og resepter.");
            System.out.println("2: Legg til pasient, lege, legemiddel eller resept.");
            System.out.println("3: Bruk en resept.");
            System.out.println("4: Skriv ut statestikk om systemet.");
            System.out.println("5: Skriv alle data til fil."); // frivillig oppgave
            System.out.println("0: Avslutt.");
            inputFraBruker = Integer.parseInt(scan.nextLine());
        }
    }



    protected void seFullstendigListe(){
      System.out.println("--- Liste over leger ---");
      skrivUtLegeliste();
      //System.out.println("\n\n");
      System.out.println("--- Liste over pasienter ---");
      skrivUtPasientliste();
      System.out.println("--- Liste over legemiddler ---");

      //System.out.println("\n\n");
      System.out.println("--- Liste over resepter ---");
      skrivUtReseptliste();
    }

    protected void skrivUtLegeliste(){
      for (int i = 0; i < leger.stoerrelse(); i++) {
  			System.out.print(i+" "+leger.hent(i).navn);
            System.out.print(", "+leger.hent(i).ikkeSpesialist+"\n");
      }
    }

    protected void skrivUtPasientliste() {
        for (int i = 0; i < pasienter.stoerrelse(); i++) {
            System.out.println(i +": "+ pasienter.hent(i).toString());
		}
    }
    protected void skrivUtReseptliste() {
        for (int i = 0; i < resepter.stoerrelse(); i++) {
            System.out.println(i +": "+ resepter.hent(i).legemiddelet.navn +" "+ resepter.hent(i).reit);
		}
    }


    protected static void skrivUtStatestikk(){}
    protected static void skrivDataTilFil(){}
    protected static void skrivUtEnResept(){}


    //protected static void seFullstendigListe(){}
    protected void leggTilElement(){
        int inputFraBruker = -1;

        while(inputFraBruker != 0){
            if(inputFraBruker == 1){ //Legger til pasient
                System.out.println();
                System.out.println("Hva heter pasienten?");
                String pasientNavn = scan.nextLine();

                System.out.println("Hva er fødselsnummeret til pasienten?");
                String foedselsnummer = scan.nextLine();

                pasienter.leggTil(new Pasient(pasientNavn, foedselsnummer));
                System.out.println("Pasienten er lagt til i systemet.");
            } else if(inputFraBruker == 2){ //Legger til lege
                System.out.println();
                System.out.println("Hva heter legen?");
                String legeNavn = scan.nextLine();

                System.out.println("Hva er kontrollID til legen? (0 hvis ingen)");
                int kontrollId = Integer.parseInt(scan.nextLine());

                leger.leggTil(new Lege(legeNavn, kontrollId));
                System.out.println("Legen er lagt til i systemet.");
            } else if(inputFraBruker == 3){ //Legger til legemiddel
                System.out.println();
                System.out.println("Hvilke type legemiddel vil du legge til?");
                System.out.println("1: Narkotisk.");
                System.out.println("2: Vanedannende.");
                System.out.println("3: Vanlig.");
                int type = Integer.parseInt(scan.nextLine());

                System.out.println("Hva heter legemiddelet?");
                String legemiddelNavn = scan.nextLine();

                System.out.println("Hva er prisen på legemiddelet?");
                float pris = Float.parseFloat(scan.nextLine());

                System.out.println("Hvor mye virkemiddel?");
                float virkemiddel = Float.parseFloat(scan.nextLine());

                Legemiddel legemiddel = null;
                if(type == 1 || type == 2){
                    System.out.println("Hva er styrken på legemiddelet?");
                    int styrke = Integer.parseInt(scan.nextLine());
                    if(type == 1){
                        legemiddel = new Narkotisk(legemiddelNavn, pris, virkemiddel, styrke);
                    } else{
                        legemiddel = new Vanedannende(legemiddelNavn, pris, virkemiddel, styrke);
                    }
                }

                legemiddel = new VanligLegemiddel(legemiddelNavn, pris, virkemiddel); // burde ikke denne være en else if i løkka if(type == 1 || type == 2){
                legemidler.leggTil(legemiddel);
                System.out.println("Legemiddelet er lagt til i systemet.");
            } else if(inputFraBruker == 4){
                System.out.println();
                System.out.println("Hvilke type resept vil du legge til?");
                System.out.println("1: Hvit");
                System.out.println("2: Militær");
                System.out.println("3: P");
                System.out.println("4: Blå");
                int type = Integer.parseInt(scan.nextLine());

                System.out.println("\nHvilke legemiddel vil du bruke?");
                for(int i = 0; i < legemidler.stoerrelse(); i++){
                    System.out.println((i+1) + ": " + legemidler.hent(i).hentNavn());
                }
                int legemiddelIndeks = Integer.parseInt(scan.nextLine()) -1;
                Legemiddel legemiddel = legemidler.hent(legemiddelIndeks);

                System.out.println("\nHvilken lege skriver ut resepten?");
                for(int i = 0; i < leger.stoerrelse(); i++){
                    System.out.println((i+1) + ": " + leger.hent(i));
                }
                int legeIndeks = Integer.parseInt(scan.nextLine());
                Lege lege = leger.hent(legeIndeks -1);

                System.out.println("\nHvilken pasient skrives resepten ut til?");
                for(int i = 0; i < pasienter.stoerrelse(); i++){
                    System.out.println((i+1) + ": " + pasienter.hent(i));
                }
                int pasientIndeks = Integer.parseInt(scan.nextLine());
                Pasient pasient = pasienter.hent(pasientIndeks -1);

                System.out.println("\nHva skal reiten være på?");
                int reit = Integer.parseInt(scan.nextLine());

                Resept resept = null;
                if (type == 1){
                    try {
                        resept = lege.skrivHvitResept(legemiddel, pasient, reit);
                    } catch (UlovligUtskrift u) {
                        System.out.println(u.getMessage());
                    }
                } else if (type == 2) {
                    try {
                        resept = lege.skrivMilitaerResept(legemiddel, pasient, reit);
                    } catch (UlovligUtskrift u) {
                        System.out.println(u.getMessage());
                    }
                } else if (type == 3) {
                    try {
                        resept = lege.skrivPResept(legemiddel, pasient);
                    } catch (UlovligUtskrift u) {
                        System.out.println(u.getMessage());
                    }
                } else if (type == 4) {
                    try {
                        resept = lege.skrivBlaaResept(legemiddel, pasient, reit);
                    } catch (UlovligUtskrift u) {
                        System.out.println(u.getMessage());
                    }
                }
                resepter.leggTil(resept);
                System.out.println("Resepten er lagt til.");

            } else if(inputFraBruker == 0){
                ordrelokke(); //Går tilbake
            } else if (5 < inputFraBruker || inputFraBruker < -1) {
                System.out.println("Velg en av de fem alternativene!");
            }
            System.out.println();
            System.out.println("Hva vil du legge til?");
            System.out.println("1: Pasient.");
            System.out.println("2: Lege.");
            System.out.println("3: Legemiddel.");
            System.out.println("4: Resept.");
            System.out.println("0: Gå tilbake.");

            inputFraBruker = Integer.parseInt(scan.nextLine());
        }
    }


    protected void brukEnResept(){
        System.out.println("Hvilken pasient vil du se resepter for?");
        for (int i = 0; i < pasienter.stoerrelse(); i++) {
            System.out.println(i +": "+ pasienter.hent(i).toString()); // Lister opp pasientene
		}
        int inputFraBruker = Integer.parseInt(scan.nextLine());
        if ((pasienter.stoerrelse()-1) < inputFraBruker || inputFraBruker < -1) { // hvis utenfor lista
            System.out.println("Feil inntasting! Tallet var utenfor lista.");
            System.out.println("Velg et tall fra 0 til " + (pasienter.stoerrelse()-1));
            ordrelokke();
        }
        Pasient pasient = pasienter.hent(inputFraBruker);
        System.out.println("Valgt pasient: " + pasient);

        System.out.println("Hvilken resept vil du bruke?");
        Stabel<Resept> reseptstabel = pasient.hentResepter();   // Oppretter en stabel med reseptene til pasienten
        int index = 0;
        if (reseptstabel.stoerrelse() == 0) {                   // hvis det er 0 resepter
            System.out.println("Pasienten har ingen resepter.");
            System.out.println();
            ordrelokke();
        } else {
            for (Resept resept : reseptstabel) {                // lister opp pasientens reseptene med reit
                System.out.println(index+": "+resept.legemiddelet.navn +" ("+ resept.reit+" reit)");
                index ++;
            }
        }
        int reseptIndex = Integer.parseInt(scan.nextLine());
        if ((pasient.resepter.stoerrelse()-1) < reseptIndex || reseptIndex < -1) { // hvis utenfor lista
            System.out.println("Feil inntasting! Tallet var utenfor lista.");
            System.out.println("Trykk en tast for å komme tilbake til hovedmeny");
            String ventHer = scan.nextLine();
            ordrelokke();
        }else {
            System.out.println("Hvor mange reit ønsekr du å bruke?");
            int reitOnske = Integer.parseInt(scan.nextLine());
            if ((pasient.resepter.hent(reseptIndex).reit - reitOnske) >= 0) {
                pasient.resepter.hent(reseptIndex).bruk(reitOnske); // bruker ønsket reit på valgt resept
                System.out.println("Brukte resept på " + pasient.resepter.hent(reseptIndex).legemiddelet.navn+". Antall gjenvarende reit: "+ pasient.resepter.hent(reseptIndex).reit);
                System.out.println("Trykk en tast for å komme tilbake til hovedmeny");
                String ventHer = scan.nextLine();
            } else {
                System.out.println("Trykk en tast for å komme tilbake til hovedmeny");
                String ventHer = scan.nextLine();
            }
        }
    }
}
