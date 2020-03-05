class Narkotisk extends Legemiddel {

	// Et legemiddel som tar inn navn, pris, virkestoff, og styrke. Har egen ID.

	protected int styrke;

	public Narkotisk(String navn, double pris, double virkestoff, int styrke0) {
		super(navn, pris, virkestoff);

		styrke = styrke0;
	}

	// Henter ut styrken
	public int hentNarkotiskStyrke() {
		return styrke;
	}

	

	// Override av toString for å skrive ut relevant info som String
	@Override
	public String toString() {
		return "LegemiddelID: "+hentId()+"\nNavn: "+hentNavn()+"\nOriginalpris: "+hentPris()+" kr"+
				"\nVirkestoff: "+hentVirkestoff()+" mg\nStyrke: "+hentNarkotiskStyrke();
	}
}