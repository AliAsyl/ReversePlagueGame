public class Virus {
    public static int virusSpreadRate = 1000;
    public static Virus gameVirus;
    public static double baseSpreadRate = 0.05; 
    public static double baseCureRate = 0.0005; 
    public static double baseImmunity = 0.00005; 
    public static double coverageOfVaccine = 0.0000001; 
    public static double effectivenessOfVaccine = 0.7; 
    public static double deadRate = 0.00005;

    private VaccineTopUp immunityBooster;
    private VaccineTopUp pathogenDetection;
    private VaccineTopUp rapidRejuvenation;
    private VaccineTopUp transmissionBlocker;
    private VaccineTopUp globalAdaptation;
    private VaccineTopUp emergencyResponse;
    private VaccineTopUp antiContagion;
    private VaccineTopUp deliveryOptimization;
    private VaccineTopUp universalAntibody;
    public Virus() {
        gameVirus = this;
        initTopUps();
        

    }
    public static void startVirusThread(){
        new Thread(() -> {
            while(GameWindow.isGameRunning){
                
                    for(Country c : Country.allCountries){
                        Virus.gameVirus.spreadInCountry(c);
                    }
                    try {
                        Thread.sleep(virusSpreadRate);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void initTopUps(){
        immunityBooster = new VaccineTopUp("Immunity Booster", "Enhances the body's natural immune response.", 100);
        pathogenDetection = new VaccineTopUp("Pathogen Detection Upgrade","Improves the vaccine's ability to recognize new strains of the disease.", 700);
        rapidRejuvenation = new VaccineTopUp("Rapid Rejuvenation Formula", "Accelerates cellular repair in infected tissues.", 300);
        transmissionBlocker = new VaccineTopUp("Transmission Blocker","Includes a viral neutralizer that prevents carriers from spreading the disease.",400);
        globalAdaptation = new VaccineTopUp("Global Adaptation Formula","Adjusts the vaccine for compatibility with various climates and demographics.",500);
        emergencyResponse = new VaccineTopUp("Emergency Response Enhancer","Adds an adrenaline-based agent for rapid action in critical patients.",600);
        antiContagion = new VaccineTopUp("Anti-Contagion Sealant","Adds a protective coating to block external contamination.",700);
        deliveryOptimization = new VaccineTopUp("Delivery Optimization Agent", "Improves the efficiency of vaccine distribution networks.",800);
        universalAntibody = new VaccineTopUp("Universal Antibody Upgrade", "Introduces a synthetic universal antibody targeting all known forms of the disease.", 800);

        GameWindow.vaccineTopUpGUI.addTopUp(immunityBooster);
        GameWindow.vaccineTopUpGUI.addTopUp(pathogenDetection);
        GameWindow.vaccineTopUpGUI.addTopUp(rapidRejuvenation);
        GameWindow.vaccineTopUpGUI.addTopUp(transmissionBlocker);
        GameWindow.vaccineTopUpGUI.addTopUp(globalAdaptation);
        GameWindow.vaccineTopUpGUI.addTopUp(emergencyResponse);
        GameWindow.vaccineTopUpGUI.addTopUp(antiContagion);
        GameWindow.vaccineTopUpGUI.addTopUp(deliveryOptimization);
        GameWindow.vaccineTopUpGUI.addTopUp(universalAntibody);

        VaccineTopUp.updateButtons();
    }
    public void spreadInCountry(Country c) {

        double population = c.getPopulation();
        double vaccinated = c.getVaccinated();
        double cured = c.getCured();
        double infected = c.getInfected();
        double dead = c.getDead();

        double vaccineEffectiveness = effectivenessOfVaccine * (1 + immunityBooster.getValue() + pathogenDetection.getValue() + universalAntibody.getValue());
        double adjustedCoverage = coverageOfVaccine * (1 + deliveryOptimization.getValue() + globalAdaptation.getValue());

        double spreadRate = baseSpreadRate * (1 - transmissionBlocker.getValue());
        double cureRate = baseCureRate * (1 + rapidRejuvenation.getValue() + emergencyResponse.getValue());
        double immunity = baseImmunity * (1 + antiContagion.getValue());

        double newInfections = Math.min((population - vaccinated - infected - dead) * spreadRate / 1000.0, population - infected - dead);

        newInfections *= (1 - vaccineEffectiveness) * (1 - adjustedCoverage);

        double newCures = Math.min(infected * cureRate, infected);
        double newDeaths = Math.min(infected * deadRate, infected);

        double immuneBoost = Math.min( cured * immunity, population - vaccinated - cured - infected - dead );

        c.setInfected(Math.max(0, infected + newInfections - newCures - newDeaths));
        c.setCured(Math.max(0, cured + newCures + immuneBoost));
        GameWindow.points += (int)((newCures + immuneBoost) / MapCoin.requiredCuredAmountToSpawn);

        for(int i = MapCoin.requiredCuredAmountToSpawn; i < ((int)(newCures)); i += MapCoin.requiredCuredAmountToSpawn){
            if(Math.random() > 0.9){
                new MapCoin(c);

            }
        }
        GameWindow.points += (int)(Math.random() * (float)(newCures)/100);
        c.setDead(dead + newDeaths);

        double newVaccinations = Math.min(population * adjustedCoverage, population - vaccinated - cured - infected - dead);
        c.setVaccinated(Math.max(0, vaccinated + newVaccinations));

        double total = c.getVaccinated() + c.getCured() + c.getInfected() + c.getDead();
        if (total > population) {
            double excess = total - population;
            c.setInfected(Math.max(0, c.getInfected() - excess));
        }
        c.setPopulation(c.getPopulation() - c.getDead());
    }

}
